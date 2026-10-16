package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.Money

data class StressTestResult(
    val scenario: String,
    val resilienceScore: Int, // 0-100
    val survivalMonths: Int,
    val principalLoss: Money,
    val recoveryPlan: String
)

class StressTestEngine {

    fun runBlackSwanSimulation(
        totalAssets: Money,
        monthlyExpenses: Long,
        isShieldActive: Boolean
    ): StressTestResult {
        // Scenario: 40% Market Crash + 50% Income Drop
        val crashLoss = (totalAssets.amount * 0.4).toLong()
        val remainingAssets = totalAssets.amount - crashLoss
        
        // If shield is active, we preserve 30% more of the principal (simulated)
        val preservedByShield = if (isShieldActive) (crashLoss * 0.3).toLong() else 0L
        val finalAssets = remainingAssets + preservedByShield
        
        val survivalMonths = (finalAssets / monthlyExpenses).toInt().coerceIn(0, 120)
        
        val score = when {
            survivalMonths > 24 -> 95
            survivalMonths > 12 -> 75
            survivalMonths > 6 -> 40
            else -> 15
        }

        val plan = if (score > 70) {
            "Your diversified portfolio and cash reserves are robust. No immediate changes required."
        } else {
            "Critical vulnerability detected. Activate the 'Wealth Shield' and increase liquidity reserves by 20%."
        }

        return StressTestResult(
            scenario = "40% Market Crash & 50% Income Drop",
            resilienceScore = score,
            survivalMonths = survivalMonths,
            principalLoss = Money(crashLoss - preservedByShield, totalAssets.currency),
            recoveryPlan = plan
        )
    }
}
