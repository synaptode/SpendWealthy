package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.OptimizerRecommendation
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import com.spendwealthy.app.core.util.format

data class FinancialAuditReport(
    val generatedAt: String,
    val overallHealthScore: Int,
    val keyInsights: List<String>,
    val vulnerabilityIndex: Double, // 0.0 - 1.0
    val executionScore: Int,
    val recommendationSummary: String
)

class AuditService {

    fun generateExecutiveAudit(
        telemetry: FinancialHealthTelemetry,
        resilience: ResilienceReport,
        narrative: WealthNarrative,
        projection: WealthProjection,
        recommendations: List<OptimizerRecommendation>
    ): FinancialAuditReport {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val dateString = "${now.dayOfMonth} ${now.month.name} ${now.year}"
        
        // Principal scoring logic: Weighted average of different metrics
        val healthScore = ((telemetry.savingsRate * 40) + (resilience.resilienceScore * 0.4) + (telemetry.optimizationEfficiency * 0.2)).toInt()
        
        val insights = mutableListOf<String>()
        insights.add("Financial runway is currently ${resilience.runwayMonths.format(1)} months.")
        insights.add("Projected net worth in 12 months: ${projection.projectedNetWorthAt12Months.format()}.")
        
        if (telemetry.healthTrend == HealthTrend.IMPROVING) {
            insights.add("Your financial trajectory is significantly improving month-over-month.")
        }
        
        insights.add("Strategic Narrative: ${narrative.headline}. ${narrative.summary}")
        
        val vulnerability = (1.0 - (resilience.resilienceScore / 100.0)).coerceIn(0.0, 1.0)
        
        return FinancialAuditReport(
            generatedAt = dateString,
            overallHealthScore = healthScore.coerceIn(0, 100),
            keyInsights = insights,
            vulnerabilityIndex = vulnerability,
            executionScore = telemetry.optimizationEfficiency,
            recommendationSummary = "Priority: ${recommendations.firstOrNull()?.title ?: "Maintain current stability"}"
        )
    }
}
