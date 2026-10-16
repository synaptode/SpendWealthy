package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.Money
import kotlinx.datetime.Instant

data class MomentumSnapshot(
    val timestamp: Instant,
    val healthScore: Int,
    val netWorth: Money,
    val savingsRate: Double
)

data class MomentumAnalysis(
    val momentumScore: Int, // -100 to 100
    val direction: MomentumDirection,
    val primaryDriver: String,
    val snapshots: List<MomentumSnapshot>
)

enum class MomentumDirection { ACCELERATING, STABLE, DECELERATING }

class TrendAnalysisService {
    
    fun calculateMomentum(
        currentTelemetry: FinancialHealthTelemetry,
        pastSnapshots: List<MomentumSnapshot>
    ): MomentumAnalysis {
        if (pastSnapshots.isEmpty()) {
            return MomentumAnalysis(
                momentumScore = 0,
                direction = MomentumDirection.STABLE,
                primaryDriver = "Baseline established",
                snapshots = emptyList()
            )
        }

        val previous = pastSnapshots.last()
        
        // Momentum score based on health score delta and savings rate improvement
        val healthDelta = currentTelemetry.savingsRate.toInt() * 100 - (previous.savingsRate * 100).toInt()
        val score = (healthDelta * 2).coerceIn(-100, 100)
        
        val direction = when {
            score > 10 -> MomentumDirection.ACCELERATING
            score < -10 -> MomentumDirection.DECELERATING
            else -> MomentumDirection.STABLE
        }
        
        val driver = if (score >= 0) {
            "Following ${currentTelemetry.optimizationEfficiency}% of optimizations is driving growth."
        } else {
            "Recent spending surges are decelerating your wealth building."
        }

        return MomentumAnalysis(
            momentumScore = score,
            direction = direction,
            primaryDriver = driver,
            snapshots = pastSnapshots
        )
    }
}
