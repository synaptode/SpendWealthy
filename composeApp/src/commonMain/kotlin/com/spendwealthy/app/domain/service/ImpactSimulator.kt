package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.domain.model.Wallet
import com.spendwealthy.app.domain.model.Transaction

data class ImpactReport(
    val runwayChange: Double, // e.g., -2.5 months
    val resilienceScoreChange: Int, // e.g., -10
    val newRunwayMonths: Double,
    val isSafe: Boolean,
    val warning: String? = null
)

class ImpactSimulator(
    private val resilienceService: ResilienceService
) {

    fun simulatePurchaseImpact(
        amount: Money,
        currentWallets: List<Wallet>,
        recentTransactions: List<Transaction>
    ): ImpactReport {
        val currentReport = resilienceService.calculateResilience(currentWallets, recentTransactions)
        
        // Simulate liquidity reduction
        val simulatedWallets = currentWallets.mapIndexed { index, wallet ->
            if (index == 0) {
                wallet.copy(balance = Money((wallet.balance.amount - amount.amount).coerceAtLeast(0), wallet.balance.currency))
            } else {
                wallet
            }
        }
        
        val simulatedReport = resilienceService.calculateResilience(simulatedWallets, recentTransactions)
        
        val runwayDiff = simulatedReport.runwayMonths - currentReport.runwayMonths
        val scoreDiff = simulatedReport.resilienceScore - currentReport.resilienceScore
        
        val isSafe = simulatedReport.resilienceScore >= 50 && simulatedReport.runwayMonths >= 3.0
        
        val warning = when {
            simulatedReport.runwayMonths < 1.0 -> "DANGER: This purchase will leave you with less than 1 month of runway."
            simulatedReport.runwayMonths < 3.0 -> "CAUTION: Your runway will drop below the safe 3-month threshold."
            !isSafe -> "This purchase significantly impacts your financial resilience."
            else -> null
        }

        return ImpactReport(
            runwayChange = runwayDiff,
            resilienceScoreChange = scoreDiff,
            newRunwayMonths = simulatedReport.runwayMonths,
            isSafe = isSafe,
            warning = warning
        )
    }
}
