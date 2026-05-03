package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.Budget
import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.domain.model.Transaction
import com.spendwealthy.app.domain.model.TransactionType

/**
 * Service to calculate budget usage and remaining limits.
 */
class BudgetEngine {

    /**
     * Calculates the total spent amount for a specific budget.
     */
    fun calculateSpentAmount(budget: Budget, transactions: List<Transaction>): Money {
        val currency = budget.limit.currency
        val spent = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .filter { it.categoryId == budget.categoryId || budget.categoryId == null }
            .sumOf { it.amount.amount }
        
        return Money(spent, currency)
    }

    /**
     * Calculates the remaining amount for a budget.
     */
    fun calculateRemainingAmount(budget: Budget, transactions: List<Transaction>): Money {
        val spent = calculateSpentAmount(budget, transactions)
        return budget.limit - spent
    }

    /**
     * Calculates usage percentage (0.0 to 1.0+).
     */
    fun calculateUsagePercentage(budget: Budget, transactions: List<Transaction>): Double {
        if (budget.limit.amount == 0L) return 0.0
        val spent = calculateSpentAmount(budget, transactions)
        return spent.amount.toDouble() / budget.limit.amount.toDouble()
    }
}
