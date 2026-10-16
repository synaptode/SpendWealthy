package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil

data class SafeToSpendResult(
    val totalSafe: Money,
    val dailySafe: Money,
    val breakdown: SafeToSpendBreakdown
)

data class SafeToSpendBreakdown(
    val totalLiquidity: Money,
    val reservedForBills: Money,
    val reservedForBudgets: Money,
    val safetyBuffer: Money,
    val savingsReserve: Money
)

class SafeToSpendEngine {

    /**
     * Calculates a multi-layered Safe to Spend amount.
     * Incorporates mandatory obligations, planned budgets, and safety margins.
     */
    fun calculateAdvanced(
        totalLiquidity: Money,
        unpaidBills: List<BillReminder>,
        budgets: List<Budget>,
        currentSpentInBudgets: Map<String?, Long>,
        today: LocalDate,
        payday: LocalDate,
        safetyBufferPercentage: Double = 0.05, // 5% absolute safety buffer
        savingsTargetPercentage: Double = 0.10  // 10% voluntary savings target
    ): SafeToSpendResult {
        val currency = totalLiquidity.currency
        val rawLiquidity = totalLiquidity.amount

        // 1. Mandatory Obligations (Bills)
        val billsAmount = unpaidBills.sumOf { it.amount.amount }
        
        // 2. Planned Consumption (Budgets)
        val totalBudgetLimit = budgets.firstOrNull { it.categoryId == null }?.limit?.amount 
            ?: budgets.sumOf { it.limit.amount }
        val totalSpent = currentSpentInBudgets.values.sum()
        val remainingBudget = (totalBudgetLimit - totalSpent).coerceAtLeast(0)

        // 3. Risk Management: Safety Buffer
        // Reserve a percentage of total liquidity for unexpected micro-expenses
        val safetyBuffer = (rawLiquidity * safetyBufferPercentage).toLong()

        // 4. Growth Management: Savings Reserve
        // Automatically set aside a target for goals before calculating spendable cash
        val savingsReserve = (rawLiquidity * savingsTargetPercentage).toLong()

        // 5. Final Calculation
        val totalSafeAmount = (rawLiquidity - billsAmount - remainingBudget - safetyBuffer - savingsReserve).coerceAtLeast(0)
        val totalSafe = Money(totalSafeAmount, currency)

        // Daily Calculation
        val daysRemaining = today.daysUntil(payday).coerceAtLeast(1)
        val dailySafe = Money(totalSafeAmount / daysRemaining, currency)

        return SafeToSpendResult(
            totalSafe = totalSafe,
            dailySafe = dailySafe,
            breakdown = SafeToSpendBreakdown(
                totalLiquidity = totalLiquidity,
                reservedForBills = Money(billsAmount, currency),
                reservedForBudgets = Money(remainingBudget, currency),
                safetyBuffer = Money(safetyBuffer, currency),
                savingsReserve = Money(savingsReserve, currency)
            )
        )
    }

    // Keep legacy for compatibility during migration if needed
    fun calculate(
        totalLiquidity: Money,
        unpaidBills: List<BillReminder>,
        budgets: List<Budget>,
        currentSpentInBudgets: Map<String?, Long>,
        today: LocalDate,
        payday: LocalDate
    ): Money = calculateAdvanced(totalLiquidity, unpaidBills, budgets, currentSpentInBudgets, today, payday).totalSafe
}
