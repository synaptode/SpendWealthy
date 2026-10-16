package com.spendwealthy.app.domain.service

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class FinancialChallenge(
    val id: String,
    val title: String,
    val description: String,
    val rewardPoints: Int,
    val category: ChallengeCategory,
    val isCompleted: Boolean = false
)

enum class ChallengeCategory { DISCIPLINE, OPTIMIZATION, SECURITY, FAMILY }

class CoachingService {

    fun generateDailyChallenge(): FinancialChallenge {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val dayOfWeek = now.dayOfWeek.name
        
        return when (now.dayOfMonth % 4) {
            0 -> FinancialChallenge(
                id = "ch_discipline_${now.dayOfMonth}",
                title = "No-Spend Day: Discretionary",
                description = "Challenge yourself to spend Rp 0 on non-essential categories today.",
                rewardPoints = 15,
                category = ChallengeCategory.DISCIPLINE
            )
            1 -> FinancialChallenge(
                id = "ch_opt_${now.dayOfMonth}",
                title = "Wealth Sweep Drill",
                description = "Review and execute at least one 'Idle Cash' recommendation.",
                rewardPoints = 20,
                category = ChallengeCategory.OPTIMIZATION
            )
            2 -> FinancialChallenge(
                id = "ch_sec_${now.dayOfMonth}",
                title = "Vault Verification",
                description = "Access your secure vault and verify your emergency document links.",
                rewardPoints = 10,
                category = ChallengeCategory.SECURITY
            )
            else -> FinancialChallenge(
                id = "ch_fam_${now.dayOfMonth}",
                title = "Family Synergy Sync",
                description = "Review the family liquidity pool status with another member.",
                rewardPoints = 25,
                category = ChallengeCategory.FAMILY
            )
        }
    }
}
