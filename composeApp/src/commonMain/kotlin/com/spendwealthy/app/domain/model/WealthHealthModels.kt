package com.spendwealthy.app.domain.model

import com.spendwealthy.app.domain.service.WealthScore
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class FinancialStreak(
    val currentStreakDays: Int,
    val longestStreakDays: Int,
    val isStreakMaintainedToday: Boolean,
    val message: String
)

@Serializable
enum class BoosterCategory {
    EMERGENCY_FUND, BILL_PAYMENT, BUDGET_DISCIPLINE, SAVING_GOAL, ASSET_GROWTH
}

@Serializable
data class ScoreBoosterMission(
    val id: String,
    val title: String,
    val description: String,
    val potentialPoints: Int,
    val category: BoosterCategory,
    val isCompleted: Boolean,
    val progressPercent: Float = 0f
)

@Serializable
enum class BadgeTier {
    BRONZE, SILVER, GOLD, PLATINUM
}

@Serializable
data class FinancialBadge(
    val id: String,
    val name: String,
    val description: String,
    val iconEmoji: String,
    val tier: BadgeTier,
    val isUnlocked: Boolean,
    val unlockedAt: Instant? = null
)

data class ComprehensiveHealthState(
    val score: WealthScore,
    val streak: FinancialStreak,
    val boosters: List<ScoreBoosterMission>,
    val badges: List<FinancialBadge>
)
