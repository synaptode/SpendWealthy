package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.Budget
import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.domain.model.Transaction
import com.spendwealthy.app.domain.model.TransactionType
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class BudgetEngineTest {

    private val engine = BudgetEngine()
    private val now = Instant.fromEpochMilliseconds(1700000000000L)
    private val date = LocalDate(2026, 10, 16)

    @Test
    fun testCalculateSpentAmountForCategoryBudget() {
        val budget = Budget(
            id = "b1",
            categoryId = "cat-food",
            limit = Money(2_000_000L, "IDR"),
            month = 10,
            year = 2026,
            createdAt = now
        )

        val transactions = listOf(
            Transaction(
                id = "t1",
                amount = Money(250_000L, "IDR"),
                type = TransactionType.EXPENSE,
                categoryId = "cat-food",
                walletId = "w1",
                targetWalletId = null,
                date = date,
                note = "Lunch",
                merchant = "Resto",
                createdAt = now,
                updatedAt = now
            ),
            Transaction(
                id = "t2",
                amount = Money(150_000L, "IDR"),
                type = TransactionType.EXPENSE,
                categoryId = "cat-transport",
                walletId = "w1",
                targetWalletId = null,
                date = date,
                note = "Grab",
                merchant = "Grab",
                createdAt = now,
                updatedAt = now
            ),
            Transaction(
                id = "t3",
                amount = Money(500_000L, "IDR"),
                type = TransactionType.INCOME,
                categoryId = "cat-food",
                walletId = "w1",
                targetWalletId = null,
                date = date,
                note = "Reimburse",
                merchant = "Kantor",
                createdAt = now,
                updatedAt = now
            )
        )

        val spent = engine.calculateSpentAmount(budget, transactions)
        assertEquals(250_000L, spent.amount)

        val remaining = engine.calculateRemainingAmount(budget, transactions)
        assertEquals(1_750_000L, remaining.amount)

        val usage = engine.calculateUsagePercentage(budget, transactions)
        assertEquals(0.125, usage, 0.001)
    }

    @Test
    fun testCalculateSpentAmountForTotalBudget() {
        val totalBudget = Budget(
            id = "b_total",
            categoryId = null, // null represents total budget
            limit = Money(5_000_000L, "IDR"),
            month = 10,
            year = 2026,
            createdAt = now
        )

        val transactions = listOf(
            Transaction(
                id = "t1",
                amount = Money(1_000_000L, "IDR"),
                type = TransactionType.EXPENSE,
                categoryId = "cat-food",
                walletId = "w1",
                targetWalletId = null,
                date = date,
                note = "",
                merchant = "",
                createdAt = now,
                updatedAt = now
            ),
            Transaction(
                id = "t2",
                amount = Money(1_500_000L, "IDR"),
                type = TransactionType.EXPENSE,
                categoryId = "cat-utilities",
                walletId = "w1",
                targetWalletId = null,
                date = date,
                note = "",
                merchant = "",
                createdAt = now,
                updatedAt = now
            )
        )

        val spent = engine.calculateSpentAmount(totalBudget, transactions)
        assertEquals(2_500_000L, spent.amount)

        val remaining = engine.calculateRemainingAmount(totalBudget, transactions)
        assertEquals(2_500_000L, remaining.amount)

        val usage = engine.calculateUsagePercentage(totalBudget, transactions)
        assertEquals(0.5, usage, 0.001)
    }

    @Test
    fun testCalculateProjectionWarningProjectedOverbudget() {
        // Plafon 3.000.000, Oktober (31 hari)
        // Pada tanggal 10 Oktober, terpakai 1.500.000 (run-rate 150.000/hari)
        // Proyeksi akhir bulan = 150.000 * 31 = 4.650.000 (> 3.000.000) -> WARNING_PROJECTED_OVERBUDGET
        val budget = Budget(
            id = "b_food",
            categoryId = "cat-food",
            limit = Money(3_000_000L, "IDR"),
            month = 10,
            year = 2026,
            createdAt = now
        )

        val txs = listOf(
            Transaction(
                id = "t1",
                amount = Money(1_500_000L, "IDR"),
                type = TransactionType.EXPENSE,
                categoryId = "cat-food",
                walletId = "w1",
                targetWalletId = null,
                date = LocalDate(2026, 10, 10),
                note = "",
                merchant = "",
                createdAt = now,
                updatedAt = now
            )
        )

        val projection = engine.calculateProjection(budget, txs, LocalDate(2026, 10, 10))

        assertEquals(150_000L, projection.dailyRunRate.amount)
        assertEquals(4_650_000L, projection.projectedMonthEndSpending.amount)
        assertEquals(10, projection.currentDayOfMonth)
        assertEquals(21, projection.daysRemainingInMonth)
        assertEquals(31, projection.totalDaysInMonth)
        // Sisa plafon 1.500.000 / 21 hari = ~71.428/hari
        assertEquals(71_428L, projection.safeDailyAllowanceRemaining.amount)
        // Estimasi habis dalam: 1.500.000 / 150.000 = 10 hari lagi
        assertEquals(10, projection.estimatedDaysUntilDepleted)
        assertEquals(ProjectionStatus.WARNING_PROJECTED_OVERBUDGET, projection.status)
    }

    @Test
    fun testCalculateProjectionHealthy() {
        // Plafon 5.000.000, Oktober (31 hari)
        // Pada tanggal 10 Oktober, baru terpakai 500.000 (run-rate 50.000/hari)
        // Proyeksi akhir bulan = 50.000 * 31 = 1.550.000 (< 85% plafon) -> HEALTHY
        val budget = Budget(
            id = "b_util",
            categoryId = "cat-util",
            limit = Money(5_000_000L, "IDR"),
            month = 10,
            year = 2026,
            createdAt = now
        )

        val txs = listOf(
            Transaction(
                id = "t1",
                amount = Money(500_000L, "IDR"),
                type = TransactionType.EXPENSE,
                categoryId = "cat-util",
                walletId = "w1",
                targetWalletId = null,
                date = LocalDate(2026, 10, 10),
                note = "",
                merchant = "",
                createdAt = now,
                updatedAt = now
            )
        )

        val projection = engine.calculateProjection(budget, txs, LocalDate(2026, 10, 10))

        assertEquals(50_000L, projection.dailyRunRate.amount)
        assertEquals(1_550_000L, projection.projectedMonthEndSpending.amount)
        assertEquals(ProjectionStatus.HEALTHY, projection.status)
    }

    @Test
    fun testCalculateProjectionAlreadyExceeded() {
        val budget = Budget(
            id = "b_exceeded",
            categoryId = "cat-food",
            limit = Money(1_000_000L, "IDR"),
            month = 10,
            year = 2026,
            createdAt = now
        )

        val txs = listOf(
            Transaction(
                id = "t1",
                amount = Money(1_200_000L, "IDR"),
                type = TransactionType.EXPENSE,
                categoryId = "cat-food",
                walletId = "w1",
                targetWalletId = null,
                date = LocalDate(2026, 10, 10),
                note = "",
                merchant = "",
                createdAt = now,
                updatedAt = now
            )
        )

        val projection = engine.calculateProjection(budget, txs, LocalDate(2026, 10, 10))

        assertEquals(0, projection.estimatedDaysUntilDepleted)
        assertEquals(0L, projection.safeDailyAllowanceRemaining.amount)
        assertEquals(ProjectionStatus.ALREADY_EXCEEDED, projection.status)
    }
}

