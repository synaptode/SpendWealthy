package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.AutoMove
import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.domain.model.OptimizerRecommendation
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class AutoPilotStatus(
    val isActive: Boolean,
    val totalOptimizationsExecuted: Int,
    val totalWealthCreated: Money,
    val nextScheduledRun: String
)

class AutoPilotService {

    fun evaluateAutoPilot(
        recommendations: List<OptimizerRecommendation>,
        isAutoPilotEnabled: Boolean
    ): List<AutoMove> {
        if (!isAutoPilotEnabled) return emptyList()

        // Auto-Pilot only executes high-confidence, low-risk strategies automatically
        // e.g. Idle Cash Sweep and Inflation Hedge
        return recommendations
            .filter { it.impactScore >= 90 } // Only elite recommendations
            .flatMap { rec ->
                rec.actions.map { action ->
                    AutoMove(
                        id = "ap_${Clock.System.now().epochSeconds}",
                        name = "Auto-Pilot: ${rec.title}",
                        amount = action.amount,
                        sourceWalletId = action.sourceId,
                        targetGoalId = action.targetId,
                        frequency = com.spendwealthy.app.domain.model.RecurrenceType.NONE,
                        nextRunDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                    )
                }
            }
    }

    fun getStatus(executedCount: Int, wealthCreated: Long): AutoPilotStatus {
        return AutoPilotStatus(
            isActive = true,
            totalOptimizationsExecuted = executedCount,
            totalWealthCreated = Money(wealthCreated, "IDR"),
            nextScheduledRun = "Tomorrow, 09:00 AM"
        )
    }
}
