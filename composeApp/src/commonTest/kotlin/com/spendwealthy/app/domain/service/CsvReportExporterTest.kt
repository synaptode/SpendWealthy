package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.Category
import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.domain.model.Transaction
import com.spendwealthy.app.domain.model.TransactionType
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class CsvReportExporterTest {

    private val exporter = CsvReportExporter()
    private val createdAt = Instant.parse("2026-05-03T00:00:00Z")

    @Test
    fun exportsHeaderAndTransactionRow() {
        val csv = exporter.exportTransactions(
            transactions = listOf(
                Transaction(
                    id = "t1",
                    amount = Money(125000, "IDR"),
                    type = TransactionType.EXPENSE,
                    categoryId = "cat_food",
                    walletId = "wallet_cash",
                    targetWalletId = null,
                    date = LocalDate(2026, 5, 3),
                    merchant = "Warung",
                    note = "Makan siang",
                    createdAt = createdAt,
                    updatedAt = createdAt
                )
            ),
            categories = listOf(
                Category("cat_food", "Makanan", "food", "#FFAA00", false)
            )
        )

        val lines = csv.lines()
        assertEquals("date,type,amount,currency,category,walletId,targetWalletId,merchant,note", lines.first())
        assertContains(csv, "2026-05-03,EXPENSE,125000,IDR,Makanan,wallet_cash,,Warung,Makan siang")
    }

    @Test
    fun escapesCommasQuotesAndNewlines() {
        val csv = exporter.exportTransactions(
            transactions = listOf(
                Transaction(
                    id = "t1",
                    amount = Money(50000, "IDR"),
                    type = TransactionType.EXPENSE,
                    categoryId = null,
                    walletId = "wallet_cash",
                    targetWalletId = null,
                    date = LocalDate(2026, 5, 3),
                    merchant = "Cafe, \"A\"",
                    note = "Baris 1\nBaris 2",
                    createdAt = createdAt,
                    updatedAt = createdAt
                )
            ),
            categories = emptyList()
        )

        assertContains(csv, "\"Cafe, \"\"A\"\"\"")
        assertContains(csv, "\"Baris 1\nBaris 2\"")
    }
}
