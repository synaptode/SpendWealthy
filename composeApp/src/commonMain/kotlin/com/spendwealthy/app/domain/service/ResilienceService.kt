package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.domain.model.Transaction
import com.spendwealthy.app.domain.model.TransactionType
import com.spendwealthy.app.domain.model.Wallet

data class ResilienceReport(
    val runwayMonths: Double,
    val resilienceScore: Int, // 0-100
    val averageMonthlyExpense: Money,
    val status: ResilienceStatus
)

enum class ResilienceStatus {
    CRITICAL, VULNERABLE, STABLE, SECURE, INVINCIBLE
}

sealed class ResilienceScenario {
    object Standard : ResilienceScenario()
    object JobLoss : ResilienceScenario()
    data class ExpenseSurge(val percentage: Double) : ResilienceScenario()
    data class Emergency(val amount: Long) : ResilienceScenario()
}

class ResilienceService {

    fun calculateResilience(
        wallets: List<Wallet>,
        transactions: List<Transaction>,
        scenario: ResilienceScenario = ResilienceScenario.Standard
    ): ResilienceReport {
        val currency = wallets.firstOrNull()?.balance?.currency ?: "IDR"
        var totalLiquidity = wallets.sumOf { it.balance.amount }
        
        val expenses = transactions.filter { it.type == TransactionType.EXPENSE }
        var avgExpense = if (expenses.isNotEmpty()) {
            expenses.sumOf { it.amount.amount } / 1 
        } else {
            3_000_000L
        }
        
        // Apply Scenarios
        when (scenario) {
            is ResilienceScenario.JobLoss -> {
                // Income is zero, but we already calculate runway based on expenses
                // No change to liquidity or expense here, just helps label the report
            }
            is ResilienceScenario.ExpenseSurge -> {
                avgExpense = (avgExpense * (1 + scenario.percentage)).toLong()
            }
            is ResilienceScenario.Emergency -> {
                totalLiquidity = (totalLiquidity - scenario.amount).coerceAtLeast(0)
            }
            ResilienceScenario.Standard -> {}
        }
        
        val runway = if (avgExpense > 0) {
            totalLiquidity.toDouble() / avgExpense.toDouble()
        } else {
            0.0
        }
        
        val (score, status) = when {
            runway < 1.0 -> 20 to ResilienceStatus.CRITICAL
            runway < 3.0 -> 50 to ResilienceStatus.VULNERABLE
            runway < 6.0 -> 75 to ResilienceStatus.STABLE
            runway < 12.0 -> 90 to ResilienceStatus.SECURE
            else -> 100 to ResilienceStatus.INVINCIBLE
        }
        
        return ResilienceReport(
            runwayMonths = runway,
            resilienceScore = score,
            averageMonthlyExpense = Money(avgExpense, currency),
            status = status
        )
    }
}
