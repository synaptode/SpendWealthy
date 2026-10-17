package com.spendwealthy.app.domain.service

import com.spendwealthy.app.core.util.format
import com.spendwealthy.app.domain.model.Money

data class CategoryExportItem(
    val categoryName: String,
    val amount: Money,
    val percentage: Double
)

class FinancialSummaryExporter {

    fun generateExecutiveSummary(
        periodLabel: String,
        walletName: String,
        totalIncome: Money,
        totalExpense: Money,
        savingRate: Double,
        averageDailyExpense: Money,
        topExpenseCategory: String?,
        expenseBreakdowns: List<CategoryExportItem>,
        incomeBreakdowns: List<CategoryExportItem>,
        insight: String
    ): String {
        val netSavingsAmount = totalIncome.amount - totalExpense.amount
        val netSavings = Money(netSavingsAmount, totalIncome.currency)
        val surplusPrefix = if (netSavingsAmount >= 0) "+" else ""

        return buildString {
            appendLine("========================================")
            appendLine("📊 SPENDWEALTHY - RINGKASAN FINANSIAL")
            appendLine("========================================")
            appendLine("Periode  : $periodLabel")
            appendLine("Akun     : $walletName")
            appendLine()
            appendLine("💰 ARUS KAS UTAMA (CASHFLOW)")
            appendLine("----------------------------------------")
            appendLine("• Total Pemasukan   : ${totalIncome.formatWithSymbol()}")
            appendLine("• Total Pengeluaran : ${totalExpense.formatWithSymbol()}")
            appendLine("• Surplus / Defisit : $surplusPrefix${netSavings.formatWithSymbol()}")
            appendLine("• Rasio Tabungan    : ${savingRate.format(1)}% (Saving Rate)")
            appendLine("• Rata-rata Harian  : ${averageDailyExpense.formatWithSymbol()} / hari")
            if (!topExpenseCategory.isNullOrBlank()) {
                appendLine("• Beban Terbesar    : $topExpenseCategory")
            }
            appendLine()
            if (expenseBreakdowns.isNotEmpty()) {
                appendLine("🏷️ ALOKASI PENGELUARAN TERATAS")
                appendLine("----------------------------------------")
                expenseBreakdowns.take(8).forEachIndexed { index, item ->
                    appendLine("${index + 1}. ${item.categoryName.padEnd(18)}: ${item.amount.formatWithSymbol()} (${item.percentage.format(1)}%)")
                }
                appendLine()
            }
            if (incomeBreakdowns.isNotEmpty()) {
                appendLine("📈 SUMBER PEMASUKAN")
                appendLine("----------------------------------------")
                incomeBreakdowns.take(5).forEachIndexed { index, item ->
                    appendLine("${index + 1}. ${item.categoryName.padEnd(18)}: ${item.amount.formatWithSymbol()} (${item.percentage.format(1)}%)")
                }
                appendLine()
            }
            if (insight.isNotBlank()) {
                appendLine("💡 CATATAN & REKOMENDASI KEUANGAN")
                appendLine("----------------------------------------")
                appendLine("\"$insight\"")
                appendLine()
            }
            appendLine("========================================")
            appendLine("Dikelola secara bijak via SpendWealthy App")
            appendLine("========================================")
        }
    }
}
