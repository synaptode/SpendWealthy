package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.domain.model.ParsingSource
import com.spendwealthy.app.domain.model.ReceiptLineItem
import com.spendwealthy.app.domain.model.Wallet
import com.spendwealthy.app.domain.model.WalletType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

import kotlinx.datetime.Clock

class SmartTransactionParserServiceTest {

    private val service = SmartTransactionParserService()
    private val now = Clock.System.now()

    private val testWallets = listOf(
        Wallet("w_bca", "BCA Prioritas", Money(20_000_000, "IDR"), WalletType.BANK, now, false),
        Wallet("w_mandiri", "Mandiri Utama", Money(5_000_000, "IDR"), WalletType.BANK, now, false),
        Wallet("w_gopay", "GoPay Saldo", Money(300_000, "IDR"), WalletType.EWALLET, now, false),
        Wallet("w_cash", "Dompet Tunai", Money(500_000, "IDR"), WalletType.CASH, now, false)
    )

    @Test
    fun testParseBcaDebitSms() {
        val bcaSms = "Debit BCA: 17/10 14:20:11 Rp 75.000 di STARBUCKS SUDIRMAN"
        val parsed = service.parse(bcaSms, testWallets)

        assertEquals(ParsingSource.BCA_SMS, parsed.source)
        assertEquals(75_000L, parsed.amount)
        assertTrue(parsed.merchantName.contains("Starbucks", ignoreCase = true))
        assertEquals("Makanan & Minuman", parsed.suggestedCategory)
        assertEquals("w_bca", parsed.matchedWalletId)
        assertEquals("BCA Prioritas", parsed.matchedWalletName)
    }

    @Test
    fun testParseMandiriLivinQris() {
        val mandiriText = "Livin' Mandiri: Transaksi Berhasil! Pembayaran QRIS Rp 45.000 di KOPI KENANGAN tgl 17/10/2026 dari rek 137000xxxxxx"
        val parsed = service.parse(mandiriText, testWallets)

        assertEquals(ParsingSource.MANDIRI_LIVIN, parsed.source)
        assertEquals(45_000L, parsed.amount)
        assertTrue(parsed.merchantName.contains("Kopi Kenangan", ignoreCase = true))
        assertEquals("w_mandiri", parsed.matchedWalletId)
    }

    @Test
    fun testParseGoPayPushNotification() {
        val gopayText = "Pembayaran Rp35.000 ke Fore Coffee berhasil! Saldo GoPay kamu sekarang Rp165.000"
        val parsed = service.parse(gopayText, testWallets)

        assertEquals(ParsingSource.GOPAY_PUSH, parsed.source)
        assertEquals(35_000L, parsed.amount)
        assertTrue(parsed.merchantName.contains("Fore Coffee", ignoreCase = true))
        assertEquals("w_gopay", parsed.matchedWalletId)
    }

    @Test
    fun testParseColloquialTextWithRbAndK() {
        val text1 = "makan siang bebek sinjay 45rb tunai"
        val parsed1 = service.parse(text1, testWallets)
        assertEquals(45_000L, parsed1.amount)
        assertEquals("w_cash", parsed1.matchedWalletId)

        val text2 = "bensin pertamax 150k bca"
        val parsed2 = service.parse(text2, testWallets)
        assertEquals(150_000L, parsed2.amount)
        assertEquals("Transportasi", parsed2.suggestedCategory)
        assertEquals("w_bca", parsed2.matchedWalletId)
    }

    @Test
    fun testParseItemizedReceiptWithTaxAndService() {
        val receiptText = """
            KOPI KENANGAN SCBD
            2x Iced Americano 76.000
            1x Toast Cokelat 35.000
            Subtotal: 111.000
            PB1 (10%): 11.100
            Service: 5.000
            Total: 127.100
        """.trimIndent()

        val parsed = service.parse(receiptText, testWallets)

        assertEquals(127_100L, parsed.amount)
        assertEquals(2, parsed.items.size)
        assertEquals("Iced Americano", parsed.items[0].name)
        assertEquals(2, parsed.items[0].quantity)
        assertEquals(76_000L, parsed.items[0].totalPrice)
        assertEquals(111_000L, parsed.subtotal)
        assertEquals(11_100L, parsed.tax)
        assertEquals(5_000L, parsed.serviceCharge)
    }

    @Test
    fun testCalculateSplitBillProportionalFee() {
        val items = listOf(
            ReceiptLineItem("Steak Wagyu", 1, 200_000L, 200_000L),
            ReceiptLineItem("Pasta Carbonara", 1, 100_000L, 100_000L)
        )
        // Subtotal = 300.000. Tax = 30.000 (10%). Service = 15.000 (5%). Total fee = 45.000
        val assignments = mapOf(
            "Alex" to listOf(0), // Alex has Steak (200k = 2/3 of subtotal -> fee = 30k -> total = 230k)
            "Bella" to listOf(1) // Bella has Pasta (100k = 1/3 of subtotal -> fee = 15k -> total = 115k)
        )

        val split = service.calculateSplitBill(
            items = items,
            tax = 30_000L,
            serviceCharge = 15_000L,
            participantAssignments = assignments
        )

        assertEquals(2, split.size)
        val alex = split.find { it.participantName == "Alex" }
        assertNotNull(alex)
        assertEquals(200_000L, alex.subtotal)
        assertEquals(30_000L, alex.taxAndServiceShare)
        assertEquals(230_000L, alex.totalToPay)

        val bella = split.find { it.participantName == "Bella" }
        assertNotNull(bella)
        assertEquals(100_000L, bella.subtotal)
        assertEquals(15_000L, bella.taxAndServiceShare)
        assertEquals(115_000L, bella.totalToPay)

        // Verify total sum equals original total
        assertEquals(345_000L, alex.totalToPay + bella.totalToPay)
    }

    @Test
    fun testFormatWhatsAppSummary() {
        val items = listOf(
            ReceiptLineItem("Menu A", 1, 100_000L, 100_000L)
        )
        val assignments = mapOf("Citra" to listOf(0))
        val split = service.calculateSplitBill(items, 10_000L, 0L, assignments)

        val message = service.formatWhatsAppSplitSummary("Resto Nusantara", 110_000L, split)

        assertTrue(message.contains("Resto Nusantara"))
        assertTrue(message.contains("Citra"))
        assertTrue(message.contains("Rp110.000") || message.contains("110.000"))
    }
}
