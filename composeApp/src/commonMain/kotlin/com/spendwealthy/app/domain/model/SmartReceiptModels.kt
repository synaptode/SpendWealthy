package com.spendwealthy.app.domain.model

enum class ParsingSource(val displayName: String, val badgeColorHex: Long) {
    BCA_SMS("BCA SMS / Debit", 0xFF005BAC),
    MANDIRI_LIVIN("Mandiri Livin'", 0xFF003066),
    BRI_BRIMO("BRI BRImo", 0xFF00529C),
    BNI_WONDR("BNI Wondr", 0xFFF15A24),
    GOPAY_PUSH("GoPay Notif", 0xFF00AA13),
    OVO_PUSH("OVO Notif", 0xFF4C2A86),
    SHOPEEPAY_PUSH("ShopeePay", 0xFFEE4D2D),
    DANA_PUSH("DANA Notif", 0xFF118EEA),
    QRIS_PAYMENT("QRIS Transaksi", 0xFFE1251B),
    PHYSICAL_RECEIPT("Struk Fisik", 0xFF10B981),
    FREE_TEXT("Teks Bebas", 0xFF6B7280)
}

data class ReceiptLineItem(
    val name: String,
    val quantity: Int = 1,
    val unitPrice: Long,
    val totalPrice: Long = quantity * unitPrice
)

data class SplitBillAllocation(
    val participantName: String,
    val itemIndices: List<Int>,
    val subtotal: Long,
    val taxAndServiceShare: Long,
    val totalToPay: Long
)

data class ParsedTransaction(
    val merchantName: String,
    val amount: Long,
    val dateText: String,
    val source: ParsingSource,
    val suggestedCategory: String,
    val matchedWalletId: String? = null,
    val matchedWalletName: String? = null,
    val items: List<ReceiptLineItem> = emptyList(),
    val subtotal: Long = amount,
    val tax: Long = 0L,
    val serviceCharge: Long = 0L,
    val referenceId: String? = null,
    val rawText: String = "",
    val confidence: Float = 0.9f
)
