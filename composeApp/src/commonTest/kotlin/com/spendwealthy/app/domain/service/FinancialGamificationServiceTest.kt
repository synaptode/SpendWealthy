package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.Goal
import com.spendwealthy.app.domain.model.GoalType
import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.domain.model.Transaction
import com.spendwealthy.app.domain.model.TransactionType
import com.spendwealthy.app.domain.model.Wallet
import com.spendwealthy.app.domain.model.WalletType
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FinancialGamificationServiceTest {

    private val service = FinancialGamificationService()
    private val now = Instant.fromEpochMilliseconds(1773000000000)

    @Test
    fun calculateStreak_consecutiveDays_returnsCorrectCount() {
        val today = LocalDate(2026, 10, 17)
        val transactions = listOf(
            createTx("tx1", LocalDate(2026, 10, 17)),
            createTx("tx2", LocalDate(2026, 10, 16)),
            createTx("tx3", LocalDate(2026, 10, 15)),
            createTx("tx4", LocalDate(2026, 10, 14))
        )

        val streak = service.calculateStreak(transactions, today)
        assertEquals(4, streak.currentStreakDays)
        assertTrue(streak.isStreakMaintainedToday)
    }

    @Test
    fun calculateStreak_missedToday_maintainsStreakIfYesterdayExists() {
        val today = LocalDate(2026, 10, 17)
        val transactions = listOf(
            createTx("tx1", LocalDate(2026, 10, 16)),
            createTx("tx2", LocalDate(2026, 10, 15))
        )

        val streak = service.calculateStreak(transactions, today)
        assertEquals(2, streak.currentStreakDays)
        assertFalse(streak.isStreakMaintainedToday)
    }

    @Test
    fun calculateStreak_empty_returnsZero() {
        val streak = service.calculateStreak(emptyList(), LocalDate(2026, 10, 17))
        assertEquals(0, streak.currentStreakDays)
        assertFalse(streak.isStreakMaintainedToday)
    }

    @Test
    fun evaluateMissions_detectsCompletedEmergencyAndGoal() {
        val wallets = listOf(
            Wallet("w1", "BCA", Money(30_000_000L, "IDR"), WalletType.BANK, now, false)
        )
        val goals = listOf(
            Goal("g1", "Tabungan", Money(10_000_000L, "IDR"), Money(6_000_000L, "IDR"), GoalType.SAVINGS, null, now)
        )

        val missions = service.evaluateMissions(
            wallets = wallets,
            bills = emptyList(),
            goals = goals,
            monthlyExpensesEstimate = 5_000_000L // 3x = 15.000.000 < 30.000.000
        )

        val emergencyMission = missions.first { it.id == "m_emergency" }
        assertTrue(emergencyMission.isCompleted)
        assertEquals(25, emergencyMission.potentialPoints)

        val goalMission = missions.first { it.id == "m_goal" }
        assertTrue(goalMission.isCompleted)
        assertEquals(20, goalMission.potentialPoints)
    }

    @Test
    fun evaluateBadges_identifiesUnlockedBadges() {
        val wallets = listOf(
            Wallet("w1", "BCA", Money(25_000_000L, "IDR"), WalletType.BANK, now, false)
        )
        val goals = listOf(
            Goal("g1", "Liburan", Money(5_000_000L, "IDR"), Money(5_000_000L, "IDR"), GoalType.SAVINGS, null, now)
        )

        val badges = service.evaluateBadges(
            wallets = wallets,
            goals = goals,
            streakDays = 7,
            score = 820,
            hasRecordedTransaction = true
        )

        val starter = badges.first { it.id == "b_starter" }
        assertTrue(starter.isUnlocked)

        val streakBadge = badges.first { it.id == "b_streak_7" }
        assertTrue(streakBadge.isUnlocked)

        val shieldBadge = badges.first { it.id == "b_shield" }
        assertTrue(shieldBadge.isUnlocked)

        val goalBadge = badges.first { it.id == "b_goal_crusher" }
        assertTrue(goalBadge.isUnlocked)

        val eliteBadge = badges.first { it.id == "b_sovereign_elite" }
        assertTrue(eliteBadge.isUnlocked)
    }

    private fun createTx(id: String, date: LocalDate): Transaction {
        return Transaction(
            id = id,
            amount = Money(50_000L, "IDR"),
            type = TransactionType.EXPENSE,
            categoryId = null,
            walletId = "w1",
            targetWalletId = null,
            date = date,
            note = "",
            merchant = "",
            createdAt = now,
            updatedAt = now
        )
    }
}
