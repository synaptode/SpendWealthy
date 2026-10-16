package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.*
import kotlinx.datetime.Clock

interface OptimizationStrategy {
    val id: String
    val priority: Int
    fun evaluate(
        wallets: List<Wallet>,
        goals: List<Goal>,
        billReminders: List<BillReminder>,
        familyMembers: List<FamilyMember>
    ): OptimizerRecommendation?
}

class IdleCashSweepStrategy : OptimizationStrategy {
    override val id = "strat_idle_sweep"
    override val priority = 10
    
    override fun evaluate(
        wallets: List<Wallet>,
        goals: List<Goal>,
        billReminders: List<BillReminder>,
        familyMembers: List<FamilyMember>
    ): OptimizerRecommendation? {
        val mainWallet = wallets.firstOrNull() ?: return null
        val upcomingBillsTotal = billReminders.filter { it.isEnabled && it.lastPaidAt == null }.sumOf { it.amount.amount }
        val liquidCash = mainWallet.balance.amount
        
        if (liquidCash > upcomingBillsTotal + 2_000_000L) {
            val excess = liquidCash - upcomingBillsTotal - 1_000_000L
            val targetGoal = goals.minByOrNull { it.currentAmount.amount.toDouble() / it.targetAmount.amount.toDouble() }
            
            if (targetGoal != null && excess > 100_000L) {
                return OptimizerRecommendation(
                    id = "rec_sweep_${Clock.System.now().epochSeconds}",
                    title = "Maximize Your Idle Cash",
                    explanation = "You have surplus liquidity. Moving Rp ${excess / 1000}k to '${targetGoal.name}' could accelerate your progress significantly.",
                    impactScore = 85,
                    actions = listOf(
                        OptimizationAction(
                            sourceId = mainWallet.id,
                            targetId = targetGoal.id,
                            amount = Money(excess, mainWallet.balance.currency),
                            type = OptimizationType.IDLE_CASH_SWEEP
                        )
                    )
                )
            }
        }
        return null
    }
}

class InflationHedgeStrategy : OptimizationStrategy {
    override val id = "strat_inflation_hedge"
    override val priority = 5
    
    override fun evaluate(
        wallets: List<Wallet>,
        goals: List<Goal>,
        billReminders: List<BillReminder>,
        familyMembers: List<FamilyMember>
    ): OptimizerRecommendation? {
        // High liquidity in savings vs long-term goals
        val totalLiquidity = wallets.sumOf { it.balance.amount }
        val longTermGoals = goals.filter { it.targetAmount.amount > 100_000_000L }
        
        if (totalLiquidity > 50_000_000L && longTermGoals.isNotEmpty()) {
            return OptimizerRecommendation(
                id = "rec_inflation_${Clock.System.now().epochSeconds}",
                title = "Inflation Defense Strategy",
                explanation = "Your cash holdings are high. Given current inflation trends, we recommend diversifying a portion into higher-yield assets to preserve purchasing power for your long-term goals.",
                impactScore = 75,
                actions = emptyList()
            )
        }
        return null
    }
}

class FamilySynergyStrategy : OptimizationStrategy {
    override val id = "strat_family_synergy"
    override val priority = 20

    override fun evaluate(
        wallets: List<Wallet>,
        goals: List<Goal>,
        billReminders: List<BillReminder>,
        familyMembers: List<FamilyMember>
    ): OptimizerRecommendation? {
        if (familyMembers.size < 2 || goals.isEmpty()) return null

        val bestGoal = goals.minByOrNull {
            if (it.targetAmount.amount <= 0L) 0.0 else it.currentAmount.amount.toDouble() / it.targetAmount.amount.toDouble()
        } ?: return null

        return OptimizerRecommendation(
            id = "rec_family_${Clock.System.now().epochSeconds}",
            title = "Family Goal Momentum",
            explanation = "Your family space has active members and a shared goal. Coordinated contributions could accelerate '${bestGoal.name}'.",
            impactScore = 88,
            actions = listOf(
                OptimizationAction(
                    sourceId = wallets.firstOrNull()?.id ?: "family_pool",
                    targetId = bestGoal.id,
                    amount = Money(250_000L, wallets.firstOrNull()?.balance?.currency ?: "IDR"),
                    type = OptimizationType.GOAL_BOOST
                )
            )
        )
    }
}

class GoalDriftStrategy : OptimizationStrategy {
    override val id = "strat_goal_drift"
    override val priority = 25

    override fun evaluate(
        wallets: List<Wallet>,
        goals: List<Goal>,
        billReminders: List<BillReminder>,
        familyMembers: List<FamilyMember>
    ): OptimizerRecommendation? {
        val driftingGoal = goals.firstOrNull {
            it.targetAmount.amount > 0L &&
                it.currentAmount.amount.toDouble() / it.targetAmount.amount.toDouble() < 0.4
        } ?: return null

        return OptimizerRecommendation(
            id = "rec_goal_drift_${Clock.System.now().epochSeconds}",
            title = "Goal Drift Correction",
            explanation = "Your '${driftingGoal.name}' target is falling behind pace. A small reallocation now will reduce future pressure.",
            impactScore = 82,
            actions = listOf(
                OptimizationAction(
                    sourceId = wallets.firstOrNull()?.id ?: "main_wallet",
                    targetId = driftingGoal.id,
                    amount = Money(150_000L, wallets.firstOrNull()?.balance?.currency ?: "IDR"),
                    type = OptimizationType.GOAL_BOOST
                )
            )
        )
    }
}

class BudgetRebalanceStrategy : OptimizationStrategy {
    override val id = "strat_budget_rebalance"
    override val priority = 15
    
    override fun evaluate(
        wallets: List<Wallet>,
        goals: List<Goal>,
        billReminders: List<BillReminder>,
        familyMembers: List<FamilyMember>
    ): OptimizerRecommendation? {
        // Mock logic: detect an over-budget category and an under-budget one
        // In a real app, this would use BudgetRepository data
        val overBudgetName = "Entertainment"
        val underBudgetName = "Groceries"
        val shiftAmount = 500_000L
        
        return OptimizerRecommendation(
            id = "rec_rebalance_${Clock.System.now().epochSeconds}",
            title = "Budget Synergy Opportunity",
            explanation = "Your '$overBudgetName' spending is slightly high, but you have a surplus in '$underBudgetName'. Shifting Rp ${shiftAmount / 1000}k will keep your total monthly budget in the green.",
            impactScore = 85,
            actions = emptyList()
        )
    }
}
