package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.*
import kotlinx.datetime.*

class WealthOptimizerEngine(
    private val auditService: DecisionAuditService
) {
    
    private val strategies = listOf(
        IdleCashSweepStrategy(),
        FamilySynergyStrategy(),
        InflationHedgeStrategy(),
        GoalDriftStrategy(),
        BudgetRebalanceStrategy()
    )

    fun generateRecommendations(
        wallets: List<Wallet>,
        goals: List<Goal>,
        billReminders: List<BillReminder>,
        safeToSpend: Money,
        familyMembers: List<FamilyMember> = emptyList()
    ): List<OptimizerRecommendation> {
        val recommendations = strategies
            .mapNotNull { it.evaluate(wallets, goals, billReminders, familyMembers) }
            .sortedByDescending { it.impactScore }
            
        recommendations.forEach { rec ->
            auditService.logDecision(
                engineName = "WealthOptimizer",
                decision = rec.title,
                inputs = mapOf("wallets" to "${wallets.size}", "goals" to "${goals.size}"),
                reasoning = listOf(rec.explanation),
                confidence = rec.impactScore / 100.0
            )
        }
        
        return recommendations
    }
}
