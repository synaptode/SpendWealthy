package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.Money

data class WealthScore(
    val score: Int, // 300-850 (Credit score style)
    val label: String, // Elite, Professional, Developing, Critical
    val breakdown: ScoreBreakdown,
    val delta: Int
)

data class ScoreBreakdown(
    val resilience: Int,
    val momentum: Int,
    val adherence: Int,
    val liquidity: Int,
    val legacy: Int
)

class WealthScoreEngine {

    fun calculateScore(
        resilienceDays: Int,
        momentumScore: Double,
        adherenceScore: Int,
        liquidityScore: Int,
        legacyEfficiency: Int
    ): WealthScore {
        // Resilience (0-150 points) - Cap at 365 days
        val rScore = (resilienceDays.toDouble() / 365.0 * 150.0).toInt().coerceIn(0, 150)
        
        // Momentum (0-150 points) - Cap at 2.0 velocity
        val mScore = (momentumScore / 2.0 * 150.0).toInt().coerceIn(0, 150)
        
        // Adherence (0-150 points) - Direct percentage
        val aScore = (adherenceScore.toDouble() / 100.0 * 150.0).toInt().coerceIn(0, 150)
        
        // Liquidity (0-100 points) - Direct percentage
        val lScore = liquidityScore.coerceIn(0, 100)
        
        // Legacy (0-100 points) - Direct efficiency
        val legScore = legacyEfficiency.coerceIn(0, 100)
        
        val totalRaw = rScore + mScore + aScore + lScore + legScore
        val baseScore = 300
        val finalScore = baseScore + totalRaw
        
        val label = when {
            finalScore > 800 -> "Sovereign Elite"
            finalScore > 700 -> "Wealth Professional"
            finalScore > 500 -> "Developing Builder"
            else -> "Financial Critical"
        }

        return WealthScore(
            score = finalScore,
            label = label,
            breakdown = ScoreBreakdown(rScore, mScore, aScore, lScore, legScore),
            delta = +12 // Mocking improvement
        )
    }
}
