package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class WealthOptimizerEngineTest {

    private val auditService = DecisionAuditService()
    private val optimizerEngine = WealthOptimizerEngine(auditService)
    private val now = Instant.fromEpochMilliseconds(1773000000000)

    @Test
    fun `test idle cash sweep recommendation generated when liquidity exceeds threshold`() {
        val wallet = Wallet(
            id = "w_main",
            name = "BCA Main",
            balance = Money(15_000_000L, "IDR"),
            type = WalletType.BANK,
            createdAt = now,
            isArchived = false
        )

        val goal = Goal(
            id = "g_emergency",
            name = "Emergency Fund",
            targetAmount = Money(25_000_000L, "IDR"),
            currentAmount = Money(5_000_000L, "IDR"),
            type = GoalType.SAVINGS,
            deadline = null,
            createdAt = now
        )

        val recommendations = optimizerEngine.generateRecommendations(
            wallets = listOf(wallet),
            goals = listOf(goal),
            billReminders = emptyList(),
            safeToSpend = Money(10_000_000L, "IDR")
        )

        assertFalse(recommendations.isEmpty(), "Should produce optimizer recommendations")
        val rec = recommendations.firstOrNull { it.actions.any { a -> a.type == OptimizationType.IDLE_CASH_SWEEP } }
        assertNotNull(rec, "Should recommend idle cash sweep")
        assertEquals(85, rec.impactScore)
        assertEquals("w_main", rec.actions.first().sourceId)
        assertEquals("g_emergency", rec.actions.first().targetId)

        // Verify audit logging
        assertTrue(auditService.logs.isNotEmpty())
        assertEquals("WealthOptimizer", auditService.logs.first().engineName)
    }

    @Test
    fun `test no idle sweep recommendation when liquidity is below threshold`() {
        val wallet = Wallet(
            id = "w_small",
            name = "Pocket Money",
            balance = Money(1_500_000L, "IDR"),
            type = WalletType.CASH,
            createdAt = now,
            isArchived = false
        )

        val goal = Goal(
            id = "g_vacation",
            name = "Japan Trip",
            targetAmount = Money(30_000_000L, "IDR"),
            currentAmount = Money(2_000_000L, "IDR"),
            type = GoalType.SAVINGS,
            deadline = null,
            createdAt = now
        )

        val recommendations = optimizerEngine.generateRecommendations(
            wallets = listOf(wallet),
            goals = listOf(goal),
            billReminders = emptyList(),
            safeToSpend = Money(500_000L, "IDR")
        )

        val sweepRec = recommendations.firstOrNull { it.actions.any { a -> a.type == OptimizationType.IDLE_CASH_SWEEP } }
        assertEquals(null, sweepRec, "Should not recommend idle cash sweep when liquidity is insufficient")
    }
}
