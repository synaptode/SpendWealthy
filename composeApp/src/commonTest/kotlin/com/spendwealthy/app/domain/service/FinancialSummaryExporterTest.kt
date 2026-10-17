package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.Money
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertTrue

class FinancialSummaryExporterTest {

    private val exporter = FinancialSummaryExporter()

    @Test
    fun generatesCompleteExecutiveSummary() {
        val summary = exporter.generateExecutiveSummary(
            periodLabel = "Oktober 2026",
            walletName = "Semua Dompet",
            totalIncome = Money(15_000_000, "IDR"),
            totalExpense = Money(9_000_000, "IDR"),
            savingRate = 40.0,
            averageDailyExpense = Money(300_000, "IDR"),
            topExpenseCategory = "Makanan & Minuman",
            expenseBreakdowns = listOf(
                CategoryExportItem("Makanan & Minuman", Money(5_000_000, "IDR"), 55.6),
                CategoryExportItem("Transportasi", Money(2_000_000, "IDR"), 22.2)
            ),
            incomeBreakdowns = listOf(
                CategoryExportItem("Gaji Pokok", Money(15_000_000, "IDR"), 100.0)
            ),
            insight = "Pengeluaran stabil dan rasio tabungan terjaga dengan baik."
        )

        assertContains(summary, "SPENDWEALTHY - RINGKASAN FINANSIAL")
        assertContains(summary, "Periode  : Oktober 2026")
        assertContains(summary, "Akun     : Semua Dompet")
        assertContains(summary, "Total Pemasukan   : Rp15.000.000")
        assertContains(summary, "Total Pengeluaran : Rp9.000.000")
        assertContains(summary, "Surplus / Defisit : +Rp6.000.000")
        assertContains(summary, "Rasio Tabungan    : 40.0% (Saving Rate)")
        assertContains(summary, "Beban Terbesar    : Makanan & Minuman")
        assertContains(summary, "Makanan & Minuman")
        assertContains(summary, "Gaji Pokok")
        assertContains(summary, "Pengeluaran stabil dan rasio tabungan terjaga dengan baik.")
        assertTrue(summary.endsWith("========================================\n"))
    }
}
