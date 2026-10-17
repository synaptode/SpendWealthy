package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.domain.model.Transaction
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

data class SimulationComparison(
    val actualNetWorth: Money,
    val simulatedNetWorth: Money,
    val wealthGap: Money,
    val keyDifferenceReason: String
)

class HistoricalSimulationEngine {

    fun simulateAlternatePast(
        transactions: List<Transaction>,
        actualCurrentBalance: Money,
        followedOptimizations: Boolean = true
    ): SimulationComparison {
        val currency = actualCurrentBalance.currency
        var simulatedBalance = actualCurrentBalance.amount
        
        // Simplified logic: If followed optimizations, we assume an extra 5% savings efficiency
        val efficiencyMultiplier = if (followedOptimizations) 1.05 else 1.0
        
        val threeMonthsAgo = Clock.System.now().minus(3, DateTimeUnit.MONTH, TimeZone.currentSystemDefault()).toLocalDateTime(TimeZone.currentSystemDefault()).date
        
        val recentSpends = transactions
            .filter { it.date >= threeMonthsAgo && it.amount.amount < 0 }
            .sumOf { it.amount.amount }
            
        // The "Gap" is the 5% improvement on spending efficiency over 3 months
        val gap = (kotlin.math.abs(recentSpends) * (efficiencyMultiplier - 1.0)).toLong()
        
        val simulatedWorth = actualCurrentBalance.amount + gap
        
        return SimulationComparison(
            actualNetWorth = actualCurrentBalance,
            simulatedNetWorth = Money(simulatedWorth, currency),
            wealthGap = Money(gap, currency),
            keyDifferenceReason = "By following all 'Idle Cash Sweep' and 'Budget Synergy' advice over the last 90 days, you would have an additional ${Money(gap, currency).formatWithSymbol()} in your goals."
        )
    }
}
