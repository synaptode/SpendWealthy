package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.domain.model.ParsedTransaction
import com.spendwealthy.app.domain.model.ParsingSource
import com.spendwealthy.app.domain.model.ReceiptLineItem
import com.spendwealthy.app.domain.model.SplitBillAllocation
import com.spendwealthy.app.domain.model.Wallet

class SmartTransactionParserService {

    /**
     * Parse raw text from banking SMS, push notifications, or receipt text.
     */
    fun parse(rawText: String, wallets: List<Wallet> = emptyList()): ParsedTransaction {
        val trimmed = rawText.trim()
        val lower = trimmed.lowercase()

        // 1. Detect Parsing Source
        val source = detectSource(lower)

        // 2. Extract Amount
        val extractedAmount = extractAmount(trimmed, lower)

        // 3. Extract Merchant / Recipient
        val merchant = extractMerchant(trimmed, lower, source)

        // 4. Extract Line Items (if any receipt layout is present)
        val lineItems = extractLineItems(trimmed)

        // 5. Extract Tax, Service, and Subtotal
        val subtotal = extractSubtotal(trimmed, lineItems, extractedAmount)
        val tax = extractTax(trimmed, subtotal)
        val serviceCharge = extractServiceCharge(trimmed, subtotal)
        val finalAmount = if (extractedAmount > 0L) extractedAmount else (subtotal + tax + serviceCharge)

        // 6. Suggest Category
        val category = suggestCategory(merchant, lower)

        // 7. Auto-Match Wallet
        val matchedWallet = findMatchingWallet(lower, source, wallets)

        // 8. Extract Date Text
        val dateText = extractDateText(trimmed)

        return ParsedTransaction(
            merchantName = merchant,
            amount = finalAmount,
            dateText = dateText,
            source = source,
            suggestedCategory = category,
            matchedWalletId = matchedWallet?.id,
            matchedWalletName = matchedWallet?.name,
            items = lineItems,
            subtotal = subtotal,
            tax = tax,
            serviceCharge = serviceCharge,
            rawText = trimmed,
            confidence = calculateConfidence(merchant, finalAmount, source)
        )
    }

    private fun detectSource(lower: String): ParsingSource {
        return when {
            lower.contains("bca") || lower.contains("m-transfer") || lower.contains("klikbca") -> ParsingSource.BCA_SMS
            lower.contains("mandiri") || lower.contains("livin") -> ParsingSource.MANDIRI_LIVIN
            lower.contains("brimo") || lower.contains("bri ") || lower.contains("trx rek") -> ParsingSource.BRI_BRIMO
            lower.contains("wondr") || lower.contains("bni ") -> ParsingSource.BNI_WONDR
            lower.contains("gopay") || lower.contains("gojek") -> ParsingSource.GOPAY_PUSH
            lower.contains("ovo ") || lower.contains("ovo cash") -> ParsingSource.OVO_PUSH
            lower.contains("shopeepay") || lower.contains("shopee") -> ParsingSource.SHOPEEPAY_PUSH
            lower.contains("dana") -> ParsingSource.DANA_PUSH
            lower.contains("qris") -> ParsingSource.QRIS_PAYMENT
            lower.contains("subtotal") || lower.contains("total:") || lower.contains("pb1") || lower.contains("cashier") || lower.contains("kasir") -> ParsingSource.PHYSICAL_RECEIPT
            else -> ParsingSource.FREE_TEXT
        }
    }

    fun extractAmount(raw: String, lower: String): Long {
        // Pattern 0: Explicit Total / Grand Total on receipts e.g. "Total: 127.100" or "Grand Total Rp 127.100"
        val totalExplicitRegex = Regex("""\b(?:grand\s+total|total\s+bayar|total)\b\s*[:=]?\s*(?:rp\.?)?\s*(\d{1,3}(?:[.,]\d{3})*(?:[.,]\d{2})?)""", RegexOption.IGNORE_CASE)
        val totalExplicitMatch = totalExplicitRegex.find(raw)
        if (totalExplicitMatch != null) {
            val numStr = totalExplicitMatch.groupValues[1]
            val parsedTotal = parseIndonesianNumber(numStr)
            if (parsedTotal > 0L) return parsedTotal
        }

        // Pattern 1: Rp. 150.000,00 or Rp 150.000 or IDR 150.000
        val rpRegex = Regex("""(?:rp\.?|idr)\s*(\d{1,3}(?:[.,]\d{3})*(?:[.,]\d{2})?)""", RegexOption.IGNORE_CASE)
        val rpMatch = rpRegex.find(raw)
        if (rpMatch != null) {
            val numStr = rpMatch.groupValues[1]
            return parseIndonesianNumber(numStr)
        }

        // Pattern 2: 50rb, 50k, 120.5k
        val kRegex = Regex("""(\d+[\d.,]*)\s*(?:rb|k)\b""", RegexOption.IGNORE_CASE)
        val kMatch = kRegex.find(lower)
        if (kMatch != null) {
            val num = kMatch.groupValues[1].replace(".", "").replace(",", ".").toDoubleOrNull() ?: 0.0
            return (num * 1000L).toLong()
        }

        // Pattern 3: Total: 150000 or Sebesar 150000
        val totalRegex = Regex("""(?:total|sebesar|sebesar\s+rp|bayar|nominal)\s*[:=]?\s*(\d{4,9})""", RegexOption.IGNORE_CASE)
        val totalMatch = totalRegex.find(raw)
        if (totalMatch != null) {
            return totalMatch.groupValues[1].toLongOrNull() ?: 0L
        }

        // Pattern 4: Bare 5-8 digit integer
        val bareDigits = Regex("""\b\d{4,9}\b""").find(raw.replace(".", "").replace(",", ""))
        return bareDigits?.value?.toLongOrNull() ?: 0L
    }

    private fun parseIndonesianNumber(raw: String): Long {
        var clean = raw.trim()
        // If ends with ,00 or .00 (cents), remove them
        if (clean.endsWith(",00") || clean.endsWith(".00")) {
            clean = clean.substring(0, clean.length - 3)
        }
        // Remove thousand separators (. or ,)
        val digitsOnly = clean.filter { it.isDigit() }
        return digitsOnly.toLongOrNull() ?: 0L
    }

    private fun extractMerchant(raw: String, lower: String, source: ParsingSource): String {
        // Bank specific patterns:
        // "di STARBUCKS SUDIRMAN" or "ke KOPI KENANGAN"
        val diKeRegex = Regex("""(?:di|ke)\s+([A-Za-z0-9\s&'.-]{3,35})(?:\s+tgl|\s+pada|\s+berhasil|\s+sukses|\s+ref|\s+sebesar|\s+rp|\n|$)""", RegexOption.IGNORE_CASE)
        val diKeMatch = diKeRegex.find(raw)
        if (diKeMatch != null) {
            val candidate = diKeMatch.groupValues[1].trim()
            if (!candidate.contains("rekening", ignoreCase = true) && candidate.length > 2) {
                return cleanMerchantName(candidate)
            }
        }

        // Check common merchants keywords
        val knownMerchants = listOf(
            "Starbucks", "Fore Coffee", "Kopi Kenangan", "Janji Jiwa", "Point Coffee",
            "Indomaret", "Alfamart", "Superindo", "Hypermart", "Transmart",
            "McDonald's", "KFC", "HokBen", "A&W", "Subway", "Burger King",
            "Tokopedia", "Shopee", "Blibli", "TikTok Shop", "Lazada",
            "Pertamina", "Shell", "BP-AKR", "Gojek", "Grab", "Bluebird",
            "Kimia Farma", "Apotek Century", "Guardian", "Watsons"
        )
        for (m in knownMerchants) {
            if (lower.contains(m.lowercase())) return m
        }

        // First non-empty line if receipt
        if (source == ParsingSource.PHYSICAL_RECEIPT) {
            val lines = raw.lines().map { it.trim() }.filter { it.isNotBlank() }
            val firstLine = lines.firstOrNull() ?: "Merchant Fisik"
            if (firstLine.length in 3..40 && !firstLine.contains("struk", ignoreCase = true)) {
                return cleanMerchantName(firstLine)
            }
        }

        return when (source) {
            ParsingSource.BCA_SMS -> "Transaksi BCA"
            ParsingSource.MANDIRI_LIVIN -> "Transaksi Mandiri"
            ParsingSource.BRI_BRIMO -> "Transaksi BRI"
            ParsingSource.BNI_WONDR -> "Transaksi BNI"
            ParsingSource.GOPAY_PUSH -> "Pembayaran GoPay"
            ParsingSource.OVO_PUSH -> "Pembayaran OVO"
            ParsingSource.SHOPEEPAY_PUSH -> "Pembayaran ShopeePay"
            ParsingSource.DANA_PUSH -> "Pembayaran DANA"
            ParsingSource.QRIS_PAYMENT -> "Pembayaran QRIS"
            else -> "Merchant Transaksi"
        }
    }

    private fun cleanMerchantName(name: String): String {
        return name.replace(Regex("""^(PT|CV|Toko|Merchant)\s+""", RegexOption.IGNORE_CASE), "")
            .trim()
            .split(" ")
            .joinToString(" ") { word -> word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() } }
    }

    fun extractLineItems(raw: String): List<ReceiptLineItem> {
        val items = mutableListOf<ReceiptLineItem>()
        val lines = raw.lines()

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isBlank()) continue

            // Skip total/subtotal lines
            if (trimmed.contains(Regex("""(?:subtotal|total|pajak|tax|pb1|service|kembali|cash|tunai|change)""", RegexOption.IGNORE_CASE))) {
                continue
            }

            // Pattern: "2x Iced Americano 76.000" or "2 Iced Americano 76000" or "Iced Americano @38000 76000"
            val itemRegex = Regex("""(?:(\d+)\s*[xX*]\s+)?([A-Za-z\s&'-]{3,30})\s+(?:@\s*(\d+[\d.,]*)\s+)?(\d{1,3}(?:[.,]\d{3})*)""")
            val match = itemRegex.find(trimmed)
            if (match != null) {
                val qty = match.groupValues[1].toIntOrNull() ?: 1
                val name = match.groupValues[2].trim()
                val totalStr = match.groupValues[4]
                val totalPrice = parseIndonesianNumber(totalStr)
                if (totalPrice > 0L && name.length >= 3) {
                    val unitPrice = if (qty > 0) totalPrice / qty else totalPrice
                    items.add(ReceiptLineItem(name = name, quantity = qty, unitPrice = unitPrice, totalPrice = totalPrice))
                }
            }
        }
        return items
    }

    private fun extractSubtotal(raw: String, items: List<ReceiptLineItem>, detectedTotal: Long): Long {
        val subRegex = Regex("""(?:subtotal|sub-total|sub\s+total)\s*[:=]?\s*(?:rp\.?)?\s*(\d{1,3}(?:[.,]\d{3})*)""", RegexOption.IGNORE_CASE)
        val match = subRegex.find(raw)
        if (match != null) {
            return parseIndonesianNumber(match.groupValues[1])
        }
        if (items.isNotEmpty()) {
            return items.sumOf { it.totalPrice }
        }
        return detectedTotal
    }

    private fun extractTax(raw: String, subtotal: Long): Long {
        // Look for PB1 or Pajak or Tax nominal
        val taxRegex = Regex("""(?:pb1|pajak|tax|ppn)\s*(?:\(?\s*\d+%\s*\)?)?\s*[:=]?\s*(?:rp\.?)?\s*(\d{1,3}(?:[.,]\d{3})*)""", RegexOption.IGNORE_CASE)
        val match = taxRegex.find(raw)
        if (match != null) {
            val amount = parseIndonesianNumber(match.groupValues[1])
            if (amount > 0L) return amount
        }
        // If "PB1 10%" or "PPN 11%" mentioned without amount
        if (raw.contains("pb1", ignoreCase = true) || raw.contains("pajak 10%", ignoreCase = true)) {
            return (subtotal * 0.10).toLong()
        }
        if (raw.contains("ppn 11%", ignoreCase = true)) {
            return (subtotal * 0.11).toLong()
        }
        return 0L
    }

    private fun extractServiceCharge(raw: String, subtotal: Long): Long {
        val scRegex = Regex("""(?:service|service\s+charge|sc)\s*(?:\(?\s*\d+%\s*\)?)?\s*[:=]?\s*(?:rp\.?)?\s*(\d{1,3}(?:[.,]\d{3})*)""", RegexOption.IGNORE_CASE)
        val match = scRegex.find(raw)
        if (match != null) {
            val amount = parseIndonesianNumber(match.groupValues[1])
            if (amount > 0L) return amount
        }
        if (raw.contains("service 5%", ignoreCase = true)) {
            return (subtotal * 0.05).toLong()
        }
        return 0L
    }

    private fun suggestCategory(merchant: String, lower: String): String {
        return when {
            lower.contains("kopi") || lower.contains("cafe") || lower.contains("starbucks") || lower.contains("kenangan") || lower.contains("fore") || lower.contains("mcd") || lower.contains("kfc") || lower.contains("bakso") || lower.contains("restoran") || lower.contains("kuliner") -> "Makanan & Minuman"
            lower.contains("indomaret") || lower.contains("alfamart") || lower.contains("superindo") || lower.contains("hypermart") || lower.contains("tokopedia") || lower.contains("shopee") || lower.contains("belanja") -> "Belanja"
            lower.contains("bensin") || lower.contains("pertamina") || lower.contains("shell") || lower.contains("gojek") || lower.contains("grab") || lower.contains("gocar") || lower.contains("goride") || lower.contains("transportasi") || lower.contains("parkir") || lower.contains("tol") -> "Transportasi"
            lower.contains("apotek") || lower.contains("obat") || lower.contains("kimia farma") || lower.contains("century") || lower.contains("guardian") || lower.contains("rs") || lower.contains("dokter") -> "Kesehatan"
            lower.contains("pln") || lower.contains("listrik") || lower.contains("pdam") || lower.contains("wifi") || lower.contains("indihome") || lower.contains("pulsa") || lower.contains("kuota") -> "Tagihan & Utilitas"
            lower.contains("bioskop") || lower.contains("cinema") || lower.contains("xxi") || lower.contains("netflix") || lower.contains("spotify") -> "Hiburan"
            else -> "Lainnya"
        }
    }

    private fun findMatchingWallet(lower: String, source: ParsingSource, wallets: List<Wallet>): Wallet? {
        if (wallets.isEmpty()) return null

        val keyword = when (source) {
            ParsingSource.BCA_SMS -> "bca"
            ParsingSource.MANDIRI_LIVIN -> "mandiri"
            ParsingSource.BRI_BRIMO -> "bri"
            ParsingSource.BNI_WONDR -> "bni"
            ParsingSource.GOPAY_PUSH -> "gopay"
            ParsingSource.OVO_PUSH -> "ovo"
            ParsingSource.SHOPEEPAY_PUSH -> "shopeepay"
            ParsingSource.DANA_PUSH -> "dana"
            else -> {
                when {
                    lower.contains("bca") -> "bca"
                    lower.contains("mandiri") -> "mandiri"
                    lower.contains("bri") -> "bri"
                    lower.contains("gopay") -> "gopay"
                    lower.contains("ovo") -> "ovo"
                    lower.contains("dana") -> "dana"
                    lower.contains("tunai") || lower.contains("cash") -> "tunai"
                    else -> null
                }
            }
        }

        if (keyword != null) {
            val matched = wallets.find { it.name.lowercase().contains(keyword) }
            if (matched != null) return matched
        }

        return wallets.firstOrNull()
    }

    private fun extractDateText(raw: String): String {
        // e.g. "17/10/2026" or "17/10 14:20" or "tgl 17 Okt 2026"
        val dateRegex = Regex("""(\d{1,2}[/-]\d{1,2}(?:[/-]\d{2,4})?(?:\s+\d{1,2}:\d{2})?)""")
        val match = dateRegex.find(raw)
        return match?.value ?: "Hari ini, Auto-Extracted"
    }

    private fun calculateConfidence(merchant: String, amount: Long, source: ParsingSource): Float {
        var score = 0.5f
        if (amount > 0L) score += 0.3f
        if (merchant != "Merchant Transaksi" && merchant != "Merchant Fisik") score += 0.15f
        if (source != ParsingSource.FREE_TEXT) score += 0.05f
        return score.coerceIn(0.1f, 0.99f)
    }

    /**
     * Smart Split-Bill Algorithm ("Bagi Bon Adil")
     * Calculates proportional tax & service share for each participant.
     */
    fun calculateSplitBill(
        items: List<ReceiptLineItem>,
        tax: Long,
        serviceCharge: Long,
        participantAssignments: Map<String, List<Int>>
    ): List<SplitBillAllocation> {
        val totalSubtotal = items.sumOf { it.totalPrice }.coerceAtLeast(1L)
        val totalAdditionalFee = tax + serviceCharge

        return participantAssignments.map { (participant, indices) ->
            val participantSubtotal = indices.sumOf { idx -> items.getOrNull(idx)?.totalPrice ?: 0L }
            val proportionalFee = if (totalSubtotal > 0L) {
                ((participantSubtotal.toDouble() / totalSubtotal.toDouble()) * totalAdditionalFee).toLong()
            } else 0L

            SplitBillAllocation(
                participantName = participant,
                itemIndices = indices,
                subtotal = participantSubtotal,
                taxAndServiceShare = proportionalFee,
                totalToPay = participantSubtotal + proportionalFee
            )
        }
    }

    /**
     * Formats a clean, professional WhatsApp text summary for split bill sharing.
     */
    fun formatWhatsAppSplitSummary(
        merchantName: String,
        totalAmount: Long,
        allocations: List<SplitBillAllocation>
    ): String {
        val sb = StringBuilder()
        sb.append("🧾 *Rincian Split Bon: $merchantName*\n")
        sb.append("Total Tagihan: ${Money(totalAmount, "IDR").formatWithSymbol()}\n")
        sb.append("──────────────────────\n")

        for (a in allocations) {
            sb.append("👤 *${a.participantName}*\n")
            sb.append("   • Subtotal: ${Money(a.subtotal, "IDR").formatWithSymbol()}\n")
            if (a.taxAndServiceShare > 0L) {
                sb.append("   • Pajak & Service: ${Money(a.taxAndServiceShare, "IDR").formatWithSymbol()}\n")
            }
            sb.append("   👉 *Total: ${Money(a.totalToPay, "IDR").formatWithSymbol()}*\n\n")
        }

        sb.append("💳 _Catat otomatis & hemat waktu bersama SpendWealthy_")
        return sb.toString().trim()
    }
}
