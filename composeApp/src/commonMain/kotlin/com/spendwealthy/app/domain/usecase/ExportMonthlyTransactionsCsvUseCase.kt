package com.spendwealthy.app.domain.usecase

import com.spendwealthy.app.core.util.monthRange
import com.spendwealthy.app.domain.gateway.FileShareGateway
import com.spendwealthy.app.domain.gateway.GatewayResult
import com.spendwealthy.app.domain.repository.CategoryRepository
import com.spendwealthy.app.domain.repository.TransactionRepository
import com.spendwealthy.app.domain.service.CsvReportExporter
import kotlinx.coroutines.flow.firstOrNull

class ExportMonthlyTransactionsCsvUseCase(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val csvReportExporter: CsvReportExporter,
    private val fileShareGateway: FileShareGateway
) {
    suspend operator fun invoke(year: Int, month: Int): GatewayResult<Unit> {
        val range = monthRange(year, month)
        val label = "transactions-$year-${month.toString().padStart(2, '0')}"
        return exportRange(range.start, range.endInclusive, label = label)
    }

    suspend fun exportRange(
        startDate: kotlinx.datetime.LocalDate,
        endDate: kotlinx.datetime.LocalDate,
        label: String = "transactions",
        walletId: String? = null
    ): GatewayResult<Unit> {
        val allTransactions = transactionRepository.getTransactionsByDateRange(startDate, endDate)
            .firstOrNull()
            .orEmpty()
        val transactions = if (walletId != null) {
            allTransactions.filter { it.walletId == walletId }
        } else {
            allTransactions
        }
        val categories = categoryRepository.getCategories().firstOrNull().orEmpty()
        val csv = csvReportExporter.exportTransactions(transactions, categories)
        val fileName = "spendwealthy-$label.csv"

        return fileShareGateway.shareTextFile(
            fileName = fileName,
            mimeType = "text/csv",
            content = csv
        )
    }
}
