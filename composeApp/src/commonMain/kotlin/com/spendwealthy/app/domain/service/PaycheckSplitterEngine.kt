package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.BillReminder
import com.spendwealthy.app.domain.model.Goal
import com.spendwealthy.app.domain.model.GoalSplitAllocation
import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.domain.model.PaycheckSplitBreakdown
import com.spendwealthy.app.domain.model.SplitStrategy
import kotlin.math.max

class PaycheckSplitterEngine {

    fun calculateSplit(
        income: Money,
        strategy: SplitStrategy,
        customNeedsRatio: Float = 0.50f,
        customWantsRatio: Float = 0.30f,
        customSavingsRatio: Float = 0.20f,
        upcomingBills: List<BillReminder> = emptyList(),
        activeGoals: List<Goal> = emptyList(),
        daysRemainingInMonth: Int = 30
    ): PaycheckSplitBreakdown {
        if (income.amount <= 0L) {
            return PaycheckSplitBreakdown(
                totalIncome = income,
                strategy = strategy,
                needsAmount = Money(0L, income.currency),
                needsRatio = strategy.needsRatio,
                wantsAmount = Money(0L, income.currency),
                wantsRatio = strategy.wantsRatio,
                savingsAmount = Money(0L, income.currency),
                savingsRatio = strategy.savingsRatio,
                upcomingBillsTotal = Money(0L, income.currency),
                isNeedsSufficientForBills = true,
                billsDeficit = Money(0L, income.currency),
                projectedDailyAllowance = Money(0L, income.currency),
                goalAllocations = emptyList()
            )
        }

        val effectiveNeedsRatio = if (strategy == SplitStrategy.CUSTOM) customNeedsRatio else strategy.needsRatio
        val effectiveWantsRatio = if (strategy == SplitStrategy.CUSTOM) customWantsRatio else strategy.wantsRatio
        val effectiveSavingsRatio = if (strategy == SplitStrategy.CUSTOM) customSavingsRatio else strategy.savingsRatio

        val rawNeeds = (income.amount * effectiveNeedsRatio).toLong()
        val rawWants = (income.amount * effectiveWantsRatio).toLong()
        // Ensure rounding doesn't drop pennies/rupiah
        val rawSavings = max(0L, income.amount - rawNeeds - rawWants)

        val totalBills = upcomingBills
            .filter { it.isEnabled }
            .sumOf { it.amount.amount }

        val isSufficient = rawNeeds >= totalBills
        val deficit = if (isSufficient) 0L else (totalBills - rawNeeds)

        val safeDays = max(1, daysRemainingInMonth)
        val dailyAllowance = rawWants / safeDays

        // Distribute savings to active goals
        val goalAllocations = distributeSavingsToGoals(rawSavings, activeGoals, income.currency)

        return PaycheckSplitBreakdown(
            totalIncome = income,
            strategy = strategy,
            needsAmount = Money(rawNeeds, income.currency),
            needsRatio = effectiveNeedsRatio,
            wantsAmount = Money(rawWants, income.currency),
            wantsRatio = effectiveWantsRatio,
            savingsAmount = Money(rawSavings, income.currency),
            savingsRatio = effectiveSavingsRatio,
            upcomingBillsTotal = Money(totalBills, income.currency),
            isNeedsSufficientForBills = isSufficient,
            billsDeficit = Money(deficit, income.currency),
            projectedDailyAllowance = Money(dailyAllowance, income.currency),
            goalAllocations = goalAllocations
        )
    }

    private fun distributeSavingsToGoals(
        savingsAmount: Long,
        goals: List<Goal>,
        currency: String
    ): List<GoalSplitAllocation> {
        if (goals.isEmpty() || savingsAmount <= 0L) return emptyList()

        // Calculate needed amounts
        val deficits = goals.map { goal ->
            max(0L, goal.targetAmount.amount - goal.currentAmount.amount)
        }
        val totalDeficit = deficits.sum()

        var remainingSavings = savingsAmount

        return goals.mapIndexed { index, goal ->
            val needed = deficits[index]
            val allocated: Long = when {
                index == goals.lastIndex -> remainingSavings
                totalDeficit > 0L -> {
                    val portion = ((needed.toDouble() / totalDeficit) * savingsAmount).toLong()
                    portion.coerceAtMost(remainingSavings)
                }
                else -> {
                    val portion = savingsAmount / goals.size
                    portion.coerceAtMost(remainingSavings)
                }
            }

            remainingSavings = max(0L, remainingSavings - allocated)
            val projected = goal.currentAmount.amount + allocated
            val willAchieve = projected >= goal.targetAmount.amount && goal.targetAmount.amount > 0L

            GoalSplitAllocation(
                goal = goal,
                allocatedAmount = Money(allocated, currency),
                projectedAmount = Money(projected, currency),
                willAchieveGoal = willAchieve
            )
        }
    }
}
