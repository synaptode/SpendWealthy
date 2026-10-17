package com.spendwealthy.app.domain.usecase

import com.spendwealthy.app.domain.gateway.FileShareGateway
import com.spendwealthy.app.domain.gateway.GatewayResult
import com.spendwealthy.app.domain.service.FinancialSummaryExporter

class ExportFinancialSummaryUseCase(
    val financialSummaryExporter: FinancialSummaryExporter,
    private val fileShareGateway: FileShareGateway
) {
    suspend fun exportSummary(
        summaryContent: String,
        label: String = "ringkasan"
    ): GatewayResult<Unit> {
        val fileName = "spendwealthy-$label.txt"
        return fileShareGateway.shareTextFile(
            fileName = fileName,
            mimeType = "text/plain",
            content = summaryContent
        )
    }
}
