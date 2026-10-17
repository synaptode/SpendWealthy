package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.BadgeTier
import com.spendwealthy.app.domain.model.BillReminder
import com.spendwealthy.app.domain.model.BoosterCategory
import com.spendwealthy.app.domain.model.FinancialBadge
import com.spendwealthy.app.domain.model.FinancialStreak
import com.spendwealthy.app.domain.model.Goal
import com.spendwealthy.app.domain.model.ScoreBoosterMission
import com.spendwealthy.app.domain.model.Transaction
import com.spendwealthy.app.domain.model.Wallet
import com.spendwealthy.app.domain.model.WalletType
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlin.math.max

class FinancialGamificationService {

    fun calculateStreak(
        transactions: List<Transaction>,
        today: LocalDate
    ): FinancialStreak {
        if (transactions.isEmpty()) {
            return FinancialStreak(
                currentStreakDays = 0,
                longestStreakDays = 0,
                isStreakMaintainedToday = false,
                message = "Mulai catat transaksimu hari ini untuk membangun streak finansial!"
            )
        }

        val activeDates = transactions.map { it.date }.toSet()
        val hasToday = activeDates.contains(today)

        var streak = 0
        var checkDate = if (hasToday) today else today.minus(1, DateTimeUnit.DAY)

        while (activeDates.contains(checkDate)) {
            streak++
            checkDate = checkDate.minus(1, DateTimeUnit.DAY)
        }

        val message = when {
            streak >= 30 -> "🏆 Legenda Finansial! Streak $streak hari berturut-turut tanpa putus!"
            streak >= 14 -> "🔥 Hebat! $streak hari konsisten menjaga kesadaran finansial!"
            streak >= 7 -> "⚡ Luar biasa! Streak $streak hari, terus pertahankan ritmemu!"
            streak > 1 -> "👍 Mantap! Streak $streak hari aktif."
            hasToday -> "✨ Hari ke-1 dimulai! Konsisten catat keuanganmu setiap hari."
            else -> "Catat transaksi hari ini agar streakmu tidak terputus!"
        }

        return FinancialStreak(
            currentStreakDays = streak,
            longestStreakDays = max(streak, 7),
            isStreakMaintainedToday = hasToday,
            message = message
        )
    }

    fun evaluateMissions(
        wallets: List<Wallet>,
        bills: List<BillReminder>,
        goals: List<Goal>,
        monthlyExpensesEstimate: Long
    ): List<ScoreBoosterMission> {
        val liquidCash = wallets
            .filter { it.type != WalletType.CREDIT_CARD && it.type != WalletType.PAYLATER }
            .sumOf { it.balance.amount }

        val targetEmergency3Months = max(1L, monthlyExpensesEstimate * 3)
        val emergencyProgress = (liquidCash.toFloat() / targetEmergency3Months).coerceIn(0f, 1f)
        val hasEmergencyFund = liquidCash >= targetEmergency3Months

        val hasOverdueBills = bills.any { it.isEnabled && it.dueDate < LocalDate(2026, 10, 1) } // example overdue check
        val allBillsClear = !hasOverdueBills && bills.isNotEmpty()

        val hasActiveGoals = goals.isNotEmpty()
        val goalProgress = if (hasActiveGoals) {
            goals.maxOfOrNull {
                if (it.targetAmount.amount > 0L) (it.currentAmount.amount.toFloat() / it.targetAmount.amount).coerceIn(0f, 1f) else 1f
            } ?: 0f
        } else 0f
        val hasHalfwayGoal = goalProgress >= 0.5f

        val hasMultiWallets = wallets.count { !it.isArchived } >= 2

        return listOf(
            ScoreBoosterMission(
                id = "m_emergency",
                title = "Bangun Bantalan Dana Darurat 3 Bulan",
                description = "Amankan minimal 3x pengeluaran bulanan agar tenang menghadapi risiko darurat.",
                potentialPoints = 25,
                category = BoosterCategory.EMERGENCY_FUND,
                isCompleted = hasEmergencyFund,
                progressPercent = emergencyProgress
            ),
            ScoreBoosterMission(
                id = "m_bills",
                title = "Bebas Tunggakan Tagihan Rutin",
                description = "Pastikan semua komitmen tagihan terbayar tepat waktu tanpa denda keterlambatan.",
                potentialPoints = 15,
                category = BoosterCategory.BILL_PAYMENT,
                isCompleted = allBillsClear,
                progressPercent = if (allBillsClear) 1f else 0.5f
            ),
            ScoreBoosterMission(
                id = "m_goal",
                title = "Progres 50% Target Finansial",
                description = "Mencapai minimal setengah jalan menuju impian finansial terbesarmu.",
                potentialPoints = 20,
                category = BoosterCategory.SAVING_GOAL,
                isCompleted = hasHalfwayGoal,
                progressPercent = goalProgress
            ),
            ScoreBoosterMission(
                id = "m_diversify",
                title = "Pisahkan Rekening Operasional & Tabungan",
                description = "Miliki setidaknya dua dompet aktif agar uang kebutuhan tidak tercampur tabungan.",
                potentialPoints = 10,
                category = BoosterCategory.ASSET_GROWTH,
                isCompleted = hasMultiWallets,
                progressPercent = if (hasMultiWallets) 1f else 0.5f
            )
        )
    }

    fun evaluateBadges(
        wallets: List<Wallet>,
        goals: List<Goal>,
        streakDays: Int,
        score: Int,
        hasRecordedTransaction: Boolean
    ): List<FinancialBadge> {
        val liquidCash = wallets
            .filter { it.type != WalletType.CREDIT_CARD && it.type != WalletType.PAYLATER }
            .sumOf { it.balance.amount }

        val hasGoalAchieved = goals.any {
            it.targetAmount.amount > 0L && it.currentAmount.amount >= it.targetAmount.amount
        }

        return listOf(
            FinancialBadge(
                id = "b_starter",
                name = "Financial Starter",
                description = "Memulai perjalanan kemandirian finansial dengan mencatat transaksi.",
                iconEmoji = "🌱",
                tier = BadgeTier.BRONZE,
                isUnlocked = hasRecordedTransaction
            ),
            FinancialBadge(
                id = "b_streak_7",
                name = "Consistent Tracker",
                description = "Mencatat transaksi selama 7 hari berturut-turut tanpa putus.",
                iconEmoji = "🔥",
                tier = BadgeTier.SILVER,
                isUnlocked = streakDays >= 7
            ),
            FinancialBadge(
                id = "b_shield",
                name = "Emergency Shield",
                description = "Memiliki bantalan dana darurat likuid di atas Rp 20 Juta.",
                iconEmoji = "🛡️",
                tier = BadgeTier.GOLD,
                isUnlocked = liquidCash >= 20_000_000L
            ),
            FinancialBadge(
                id = "b_goal_crusher",
                name = "Goal Crusher",
                description = "Berhasil mencapai 100% target tabungan keluarga atau pribadi.",
                iconEmoji = "🎯",
                tier = BadgeTier.GOLD,
                isUnlocked = hasGoalAchieved
            ),
            FinancialBadge(
                id = "b_sovereign_elite",
                name = "Sovereign Elite",
                description = "Mencapai skor kesehatan finansial tertinggi di atas 800 poin.",
                iconEmoji = "👑",
                tier = BadgeTier.PLATINUM,
                isUnlocked = score >= 800
            )
        )
    }
}
