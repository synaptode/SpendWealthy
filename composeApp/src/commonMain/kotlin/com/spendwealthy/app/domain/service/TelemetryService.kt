package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.domain.model.Transaction
import com.spendwealthy.app.domain.model.TransactionType
import com.spendwealthy.app.domain.model.Wallet

data class FinancialHealthTelemetry(
    val liquidityCoverageRatio: Double,
    val savingsRate: Double,
    val optimizationEfficiency: Int, // 0-100
    val healthTrend: HealthTrend
)

enum class HealthTrend {
    IMPROVING, STABLE, DECLINING
}

class TelemetryService {

    fun generateHealthTelemetry(
        wallets: List<Wallet>,
        transactions: List<Transaction>,
        executedOptimizations: Int,
        totalOptimizations: Int
    ): FinancialHealthTelemetry {
        val totalLiquidity = wallets.sumOf { it.balance.amount }
        val income = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount.amount }
        val expenses = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount.amount }
        
        // 1. Savings Rate: (Income - Expense) / Income
        val savingsRate = if (income > 0) {
            (income - expenses).toDouble() / income.toDouble()
        } else {
            0.0
        }
        
        // 2. LCR: Liquidity / Avg Monthly Expense
        val lcr = if (expenses > 0) {
            totalLiquidity.toDouble() / expenses.toDouble()
        } else {
            totalLiquidity.toDouble() / 3_000_000.0 // Baseline
        }
        
        // 3. Optimization Efficiency
        val efficiency = if (totalOptimizations > 0) {
            (executedOptimizations.toDouble() / totalOptimizations.toDouble() * 100).toInt()
        } else {
            100
        }
        
        val trend = when {
            savingsRate > 0.2 && lcr > 3.0 -> HealthTrend.IMPROVING
            savingsRate < 0.05 || lcr < 1.0 -> HealthTrend.DECLINING
            else -> HealthTrend.STABLE
        }
        
        return FinancialHealthTelemetry(
            liquidityCoverageRatio = lcr,
            savingsRate = savingsRate,
            optimizationEfficiency = efficiency,
            healthTrend = trend
        )
    }
}
