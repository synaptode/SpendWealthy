package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.Money
import kotlinx.datetime.Clock

data class WealthProjection(
    val months: List<ProjectionPoint>,
    val projectedNetWorthAt12Months: Money,
    val confidenceScore: Int // 0-100
)

data class ProjectionPoint(
    val monthIndex: Int,
    val projectedBalance: Money
)

class WealthProjectionEngine {

    fun generate12MonthProjection(
        currentLiquidity: Money,
        telemetry: FinancialHealthTelemetry,
        activeGoals: List<com.spendwealthy.app.domain.model.Goal>
    ): WealthProjection {
        val currency = currentLiquidity.currency
        val points = mutableListOf<ProjectionPoint>()
        var runningBalance = currentLiquidity.amount
        
        // Mock average monthly savings from telemetry
        val monthlySavings = (telemetry.savingsRate * 10_000_000).toLong() // Assume 10M income for projection baseline
        
        for (i in 0..12) {
            points.add(ProjectionPoint(i, Money(runningBalance, currency)))
            runningBalance += monthlySavings
            
            // Deduct goal contributions if they are active (simplified)
            activeGoals.forEach { goal ->
                if (goal.currentAmount.amount < goal.targetAmount.amount) {
                    // Assume 5% of savings go to each goal
                    runningBalance += (monthlySavings * 0.05).toLong() // Goals are usually part of savings, but let's assume they grow
                }
            }
        }

        return WealthProjection(
            months = points,
            projectedNetWorthAt12Months = Money(runningBalance, currency),
            confidenceScore = if (telemetry.healthTrend == HealthTrend.STABLE) 85 else 60
        )
    }
}
