package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.*
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PurchaseSimulationEngineTest {

    private val engine = PurchaseSimulationEngine()
    private val now = Instant.fromEpochMilliseconds(1700000000000L)
    private val today = LocalDate(2026, 10, 17)
    private val payday = LocalDate(2026, 10, 27) // 10 days remaining

    private val defaultWallets = listOf(
        Wallet(
            id = "w1",
            name = "BCA Utama",
            balance = Money(30_000_000L, "IDR"),
            type = WalletType.BANK,
            createdAt = now,
            isArchived = false
        ),
        Wallet(
            id = "w2",
            name = "GoPay",
            balance = Money(500_000L, "IDR"),
            type = WalletType.EWALLET,
            createdAt = now,
            isArchived = false
        )
    )

    private val recentExpenses = listOf(
        Transaction(
            id = "t1",
            amount = Money(6_000_000L, "IDR"),
            type = TransactionType.EXPENSE,
            categoryId = "cat-living",
            walletId = "w1",
            targetWalletId = null,
            date = today,
            note = "Biaya Hidup Bulanan",
            merchant = "Supermarket",
            createdAt = now,
            updatedAt = now
        )
    )

    private val unpaidBills = listOf(
        BillReminder(
            id = "b1",
            name = "PLN Listrik",
            amount = Money(1_000_000L, "IDR"),
            categoryId = "cat-bills",
            dueDate = LocalDate(2026, 10, 20),
            recurrenceType = RecurrenceType.MONTHLY,
            isEnabled = true,
            lastPaidAt = null
        )
    )

    private val budgets = listOf(
        Budget(
            id = "bg1",
            categoryId = null,
            limit = Money(5_000_000L, "IDR"),
            month = 10,
            year = 2026,
            createdAt = now
        )
    )

    private val activeGoals = listOf(
        Goal(
            id = "g1",
            name = "Liburan Keluarga Akhir Tahun",
            targetAmount = Money(40_000_000L, "IDR"),
            currentAmount = Money(28_000_000L, "IDR"),
            type = GoalType.SAVINGS,
            deadline = LocalDate(2026, 12, 31),
            createdAt = now
        )
    )

    @Test
    fun testSafeToBuyWhenLiquidityIsAbundant() {
        val request = PurchaseSimulationRequest(
            itemName = "Kopi & Makan Siang",
            price = Money(150_000L, "IDR"),
            selectedWalletId = "w1"
        )

        val result = engine.simulate(
            request = request,
            wallets = defaultWallets,
            transactions = recentExpenses,
            unpaidBills = unpaidBills,
            budgets = budgets,
            currentSpentInBudgets = mapOf(null to 1_000_000L),
            activeGoals = activeGoals,
            today = today,
            payday = payday
        )

        assertEquals(PurchaseVerdict.SAFE_TO_BUY, result.verdict)
        assertTrue(result.canAffordDirectly)
        assertTrue(result.simulatedDailyAllowance.amount > 0L)
        assertTrue(result.emergencyRunwayMonthsSimulated >= 3.0)
    }

    @Test
    fun testHighRiskDeficitWhenWalletBalanceInsufficient() {
        val request = PurchaseSimulationRequest(
            itemName = "Laptop Kerja",
            price = Money(15_000_000L, "IDR"),
            selectedWalletId = "w2" // GoPay only has 500k
        )

        val result = engine.simulate(
            request = request,
            wallets = defaultWallets,
            transactions = recentExpenses,
            unpaidBills = unpaidBills,
            budgets = budgets,
            currentSpentInBudgets = mapOf(null to 1_000_000L),
            activeGoals = activeGoals,
            today = today,
            payday = payday
        )

        assertEquals(PurchaseVerdict.HIGH_RISK_DEFICIT, result.verdict)
        assertFalse(result.canAffordDirectly)
        assertEquals("Saldo Tidak Cukup", result.verdictTitle)
    }

    @Test
    fun testProceedWithCautionWhenAllowanceTightensSignificantly() {
        // Total liquidity = 30.5M, Bills = 1M, Budget remaining = 4M, Buffer+Savings = 1.5M + 3.0M -> Total safe ~21M
        // If user buys an item of 15M, daily allowance drops significantly
        val request = PurchaseSimulationRequest(
            itemName = "Smartphone Baru",
            price = Money(16_000_000L, "IDR"),
            selectedWalletId = "w1"
        )

        val result = engine.simulate(
            request = request,
            wallets = defaultWallets,
            transactions = recentExpenses,
            unpaidBills = unpaidBills,
            budgets = budgets,
            currentSpentInBudgets = mapOf(null to 1_000_000L),
            activeGoals = activeGoals,
            today = today,
            payday = payday
        )

        assertEquals(PurchaseVerdict.PROCEED_WITH_CAUTION, result.verdict)
        assertTrue(result.canAffordDirectly)
        assertTrue(result.impactOnFamilyGoalDaysDelayed > 0)
    }

    @Test
    fun testHighRiskWhenRunwayDepletedBelowOneMonth() {
        // Small wallet with low liquidity (2M) and monthly expense of 5M
        val lowWallets = listOf(
            Wallet(
                id = "w1",
                name = "Dompet Tunai",
                balance = Money(3_000_000L, "IDR"),
                type = WalletType.CASH,
                createdAt = now,
                isArchived = false
            )
        )
        val highExpenses = listOf(
            Transaction(
                id = "t1",
                amount = Money(4_000_000L, "IDR"),
                type = TransactionType.EXPENSE,
                categoryId = null,
                walletId = "w1",
                targetWalletId = null,
                date = today,
                note = "Expenses",
                merchant = "Various",
                createdAt = now,
                updatedAt = now
            )
        )

        val request = PurchaseSimulationRequest(
            itemName = "Belanja Barang Mewah",
            price = Money(2_500_000L, "IDR"),
            selectedWalletId = "w1"
        )

        val result = engine.simulate(
            request = request,
            wallets = lowWallets,
            transactions = highExpenses,
            unpaidBills = emptyList(),
            budgets = emptyList(),
            currentSpentInBudgets = emptyMap(),
            activeGoals = emptyList(),
            today = today,
            payday = payday
        )

        assertEquals(PurchaseVerdict.HIGH_RISK_DEFICIT, result.verdict)
        assertTrue(result.emergencyRunwayMonthsSimulated < 1.0)
    }
}
