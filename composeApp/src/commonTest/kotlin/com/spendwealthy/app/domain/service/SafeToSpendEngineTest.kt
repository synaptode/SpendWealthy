package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.BillReminder
import com.spendwealthy.app.domain.model.Budget
import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.domain.model.RecurrenceType
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class SafeToSpendEngineTest {

    private val engine = SafeToSpendEngine()
    private val now = Instant.fromEpochMilliseconds(1700000000000L)

    @Test
    fun testSafeToSpendCalculationBasic() {
        val totalLiquidity = Money(10_000_000L, "IDR")
        val unpaidBills = listOf(
            BillReminder(
                id = "bill-1",
                name = "Wifi Indihome",
                amount = Money(500_000L, "IDR"),
                categoryId = "cat-utilities",
                dueDate = LocalDate(2026, 10, 20),
                recurrenceType = RecurrenceType.MONTHLY,
                isEnabled = true,
                lastPaidAt = null
            )
        )
        val budgets = listOf(
            Budget(
                id = "b1",
                categoryId = null,
                limit = Money(2_000_000L, "IDR"),
                month = 10,
                year = 2026,
                createdAt = now
            )
        )
        val currentSpent: Map<String?, Long> = mapOf(null to 500_000L)
        val today = LocalDate(2026, 10, 16)
        val payday = LocalDate(2026, 10, 25) // 9 days remaining

        val result = engine.calculateAdvanced(
            totalLiquidity = totalLiquidity,
            unpaidBills = unpaidBills,
            budgets = budgets,
            currentSpentInBudgets = currentSpent,
            today = today,
            payday = payday,
            safetyBufferPercentage = 0.05, // 5% of 10M = 500k
            savingsTargetPercentage = 0.10 // 10% of 10M = 1.0M
        )

        // Bills = 500k
        // Remaining budget = 2M - 500k = 1.5M
        // Safety buffer = 500k
        // Savings target = 1M
        // Total reserved = 500k + 1.5M + 500k + 1M = 3.5M
        // Total safe = 10M - 3.5M = 6.5M
        assertEquals(6_500_000L, result.totalSafe.amount)
        assertEquals("IDR", result.totalSafe.currency)

        // Daily safe = 6.5M / 9 days = 722,222
        assertEquals(6_500_000L / 9, result.dailySafe.amount)

        // Breakdown checks
        assertEquals(500_000L, result.breakdown.reservedForBills.amount)
        assertEquals(1_500_000L, result.breakdown.reservedForBudgets.amount)
        assertEquals(500_000L, result.breakdown.safetyBuffer.amount)
        assertEquals(1_000_000L, result.breakdown.savingsReserve.amount)
    }

    @Test
    fun testSafeToSpendWhenDeficitCoercesToZero() {
        val totalLiquidity = Money(1_000_000L, "IDR")
        val heavyBills = listOf(
            BillReminder(
                id = "bill-1",
                name = "Sewa Rumah",
                amount = Money(3_000_000L, "IDR"),
                categoryId = null,
                dueDate = LocalDate(2026, 10, 20),
                recurrenceType = RecurrenceType.MONTHLY,
                isEnabled = true,
                lastPaidAt = null
            )
        )
        val result = engine.calculateAdvanced(
            totalLiquidity = totalLiquidity,
            unpaidBills = heavyBills,
            budgets = emptyList(),
            currentSpentInBudgets = emptyMap(),
            today = LocalDate(2026, 10, 16),
            payday = LocalDate(2026, 10, 20)
        )

        assertEquals(0L, result.totalSafe.amount)
        assertEquals(0L, result.dailySafe.amount)
    }
}
