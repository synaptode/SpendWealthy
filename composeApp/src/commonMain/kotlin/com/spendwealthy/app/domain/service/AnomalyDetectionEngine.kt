package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.Transaction
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

data class FinancialAnomaly(
    val id: String,
    val type: AnomalyType,
    val description: String,
    val severity: Severity,
    val suggestedAction: String
)

enum class AnomalyType {
    SPENDING_SPIKE,
    INCOME_DIP,
    RECURRING_BILL_CHANGE,
    UNUSUAL_VELOCITY
}

enum class Severity { LOW, MEDIUM, HIGH, CRITICAL }

class AnomalyDetectionEngine {

    fun detectAnomalies(
        recentTransactions: List<Transaction>,
        averageMonthlySpending: Long
    ): List<FinancialAnomaly> {
        val anomalies = mutableListOf<FinancialAnomaly>()
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        
        // 1. Spending Spike Detection
        val dailySpends = recentTransactions
            .filter { it.amount.amount < 0 }
            .groupBy { it.date }
            .mapValues { it.value.sumOf { t -> kotlin.math.abs(t.amount.amount) } }
            
        dailySpends.forEach { (date, amount) ->
            if (amount > averageMonthlySpending * 0.2) { // Spike > 20% of monthly average in one day
                anomalies.add(FinancialAnomaly(
                    id = "spike_${date}",
                    type = AnomalyType.SPENDING_SPIKE,
                    description = "Unusual spending spike detected on ${date}. You spent 20% of your monthly average in a single day.",
                    severity = Severity.MEDIUM,
                    suggestedAction = "Review these transactions for potential errors or fraud."
                ))
            }
        }
        
        // 2. Velocity Detection
        val weekAgo = Clock.System.now().minus(7, DateTimeUnit.DAY, TimeZone.currentSystemDefault()).toLocalDateTime(TimeZone.currentSystemDefault()).date
        val weeklySpend = recentTransactions
            .filter { it.date >= weekAgo && it.amount.amount < 0 }
            .sumOf { kotlin.math.abs(it.amount.amount) }
            
        if (weeklySpend > averageMonthlySpending * 0.5) { // Spent 50% of monthly avg in a week
            anomalies.add(FinancialAnomaly(
                id = "velocity_high",
                type = AnomalyType.UNUSUAL_VELOCITY,
                description = "High spending velocity detected. You've used 50% of your monthly budget in the last 7 days.",
                severity = Severity.HIGH,
                suggestedAction = "Slow down discretionary spending to avoid end-of-month crunch."
            ))
        }

        return anomalies
    }
}
