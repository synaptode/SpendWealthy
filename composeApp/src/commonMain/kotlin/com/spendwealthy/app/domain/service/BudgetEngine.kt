package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.Budget
import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.domain.model.Transaction
import com.spendwealthy.app.domain.model.TransactionType
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

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

    /**
     * Calculates smart run-rate projections, estimated days until depleted,
     * and safe daily spending recommendations for the remainder of the month.
     */
    fun calculateProjection(
        budget: Budget,
        transactions: List<Transaction>,
        currentDate: kotlinx.datetime.LocalDate = kotlinx.datetime.Clock.System.now()
            .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date
    ): BudgetProjection {
        val currency = budget.limit.currency
        val spent = calculateSpentAmount(budget, transactions)
        val remaining = calculateRemainingAmount(budget, transactions)

        val totalDays = com.spendwealthy.app.core.util.daysInMonth(budget.year, budget.month)
        val isCurrentMonth = currentDate.year == budget.year && currentDate.monthNumber == budget.month
        val dayOfMonth = when {
            isCurrentMonth -> currentDate.dayOfMonth.coerceIn(1, totalDays)
            currentDate.year > budget.year || (currentDate.year == budget.year && currentDate.monthNumber > budget.month) -> totalDays
            else -> 1
        }
        val daysRemaining = (totalDays - dayOfMonth).coerceAtLeast(0)

        val dailyRunRateAmount = if (dayOfMonth > 0) spent.amount / dayOfMonth else 0L
        val dailyRunRate = Money(dailyRunRateAmount, currency)

        val projectedAmount = dailyRunRateAmount * totalDays
        val projectedMonthEndSpending = Money(projectedAmount, currency)

        val safeDailyAllowanceAmount = when {
            remaining.amount <= 0L -> 0L
            daysRemaining > 0 -> remaining.amount / daysRemaining
            else -> remaining.amount
        }
        val safeDailyAllowance = Money(safeDailyAllowanceAmount, currency)

        val daysUntilDepleted = when {
            remaining.amount <= 0L -> 0
            dailyRunRateAmount > 0L -> (remaining.amount / dailyRunRateAmount).toInt()
            else -> null
        }

        val status = when {
            remaining.amount < 0L -> ProjectionStatus.ALREADY_EXCEEDED
            dailyRunRateAmount > 0L && projectedAmount > budget.limit.amount -> ProjectionStatus.WARNING_PROJECTED_OVERBUDGET
            dailyRunRateAmount > 0L && projectedAmount > (budget.limit.amount * 0.85).toLong() -> ProjectionStatus.ON_TRACK
            else -> ProjectionStatus.HEALTHY
        }

        return BudgetProjection(
            dailyRunRate = dailyRunRate,
            projectedMonthEndSpending = projectedMonthEndSpending,
            safeDailyAllowanceRemaining = safeDailyAllowance,
            daysRemainingInMonth = daysRemaining,
            currentDayOfMonth = dayOfMonth,
            totalDaysInMonth = totalDays,
            estimatedDaysUntilDepleted = daysUntilDepleted,
            status = status
        )
    }
}

data class BudgetProjection(
    val dailyRunRate: Money,
    val projectedMonthEndSpending: Money,
    val safeDailyAllowanceRemaining: Money,
    val daysRemainingInMonth: Int,
    val currentDayOfMonth: Int,
    val totalDaysInMonth: Int,
    val estimatedDaysUntilDepleted: Int?,
    val status: ProjectionStatus
)

enum class ProjectionStatus {
    HEALTHY,                      // Projected month-end <= 85% limit
    ON_TRACK,                     // Projected 85% - 100% limit
    WARNING_PROJECTED_OVERBUDGET, // Currently under limit, but daily run-rate will breach before month end
    ALREADY_EXCEEDED              // Already exceeded limit
}

