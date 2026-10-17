package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.BillReminder
import com.spendwealthy.app.domain.model.Goal
import com.spendwealthy.app.domain.model.GoalType
import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.domain.model.RecurrenceType
import com.spendwealthy.app.domain.model.SplitStrategy
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PaycheckSplitterEngineTest {

    private val engine = PaycheckSplitterEngine()

    private val sampleInstant = Instant.fromEpochMilliseconds(1773000000000)
    private val sampleDate = LocalDate(2026, 10, 25)

    @Test
    fun calculateSplit_withBalancedStrategy_splitsCorrectly() {
        val income = Money(15_000_000L, "IDR")
        val bills = listOf(
            BillReminder("b1", "Listrik", Money(1_000_000L, "IDR"), null, sampleDate, RecurrenceType.MONTHLY, true, null),
            BillReminder("b2", "WiFi", Money(500_000L, "IDR"), null, sampleDate, RecurrenceType.MONTHLY, true, null)
        )
        val goals = listOf(
            Goal("g1", "Dana Darurat", Money(20_000_000L, "IDR"), Money(10_000_000L, "IDR"), GoalType.SAVINGS, null, sampleInstant),
            Goal("g2", "Liburan Akhir Tahun", Money(10_000_000L, "IDR"), Money(5_000_000L, "IDR"), GoalType.SAVINGS, null, sampleInstant)
        )

        val breakdown = engine.calculateSplit(
            income = income,
            strategy = SplitStrategy.BALANCED_50_30_20,
            upcomingBills = bills,
            activeGoals = goals,
            daysRemainingInMonth = 30
        )

        // 50% = 7.500.000, 30% = 4.500.000, 20% = 3.000.000
        assertEquals(7_500_000L, breakdown.needsAmount.amount)
        assertEquals(4_500_000L, breakdown.wantsAmount.amount)
        assertEquals(3_000_000L, breakdown.savingsAmount.amount)
        assertEquals(1_500_000L, breakdown.upcomingBillsTotal.amount)
        assertTrue(breakdown.isNeedsSufficientForBills)
        assertEquals(0L, breakdown.billsDeficit.amount)
        assertEquals(150_000L, breakdown.projectedDailyAllowance.amount)

        // Goals total deficit = (20-10) + (10-5) = 15.000.000
        // Goal 1 (10/15 * 3jt = 2.000.000), Goal 2 (5/15 * 3jt = 1.000.000)
        assertEquals(2, breakdown.goalAllocations.size)
        assertEquals(2_000_000L, breakdown.goalAllocations[0].allocatedAmount.amount)
        assertEquals(12_000_000L, breakdown.goalAllocations[0].projectedAmount.amount)
        assertFalse(breakdown.goalAllocations[0].willAchieveGoal)

        assertEquals(1_000_000L, breakdown.goalAllocations[1].allocatedAmount.amount)
        assertEquals(6_000_000L, breakdown.goalAllocations[1].projectedAmount.amount)
    }

    @Test
    fun calculateSplit_whenBillsExceedNeeds_flagsDeficit() {
        val income = Money(5_000_000L, "IDR")
        val heavyBills = listOf(
            BillReminder("b1", "Sewa Apartemen", Money(3_500_000L, "IDR"), null, sampleDate, RecurrenceType.MONTHLY, true, null)
        )

        val breakdown = engine.calculateSplit(
            income = income,
            strategy = SplitStrategy.BALANCED_50_30_20, // Needs 50% = 2.500.000 < 3.500.000
            upcomingBills = heavyBills,
            daysRemainingInMonth = 25
        )

        assertEquals(2_500_000L, breakdown.needsAmount.amount)
        assertEquals(3_500_000L, breakdown.upcomingBillsTotal.amount)
        assertFalse(breakdown.isNeedsSufficientForBills)
        assertEquals(1_000_000L, breakdown.billsDeficit.amount)
    }

    @Test
    fun calculateSplit_withAggressiveSaverStrategy() {
        val income = Money(20_000_000L, "IDR")
        val breakdown = engine.calculateSplit(
            income = income,
            strategy = SplitStrategy.AGGRESSIVE_SAVER_40_20_40
        )

        // 40% = 8.000.000, 20% = 4.000.000, 40% = 8.000.000
        assertEquals(8_000_000L, breakdown.needsAmount.amount)
        assertEquals(4_000_000L, breakdown.wantsAmount.amount)
        assertEquals(8_000_000L, breakdown.savingsAmount.amount)
    }

    @Test
    fun calculateSplit_withCustomStrategy() {
        val income = Money(10_000_000L, "IDR")
        val breakdown = engine.calculateSplit(
            income = income,
            strategy = SplitStrategy.CUSTOM,
            customNeedsRatio = 0.70f,
            customWantsRatio = 0.15f,
            customSavingsRatio = 0.15f
        )

        assertEquals(7_000_000L, breakdown.needsAmount.amount)
        assertEquals(1_500_000L, breakdown.wantsAmount.amount)
        assertEquals(1_500_000L, breakdown.savingsAmount.amount)
    }

    @Test
    fun calculateSplit_withZeroIncome_returnsZeroSafe() {
        val income = Money(0L, "IDR")
        val breakdown = engine.calculateSplit(
            income = income,
            strategy = SplitStrategy.BALANCED_50_30_20
        )

        assertEquals(0L, breakdown.needsAmount.amount)
        assertEquals(0L, breakdown.wantsAmount.amount)
        assertEquals(0L, breakdown.savingsAmount.amount)
        assertTrue(breakdown.isNeedsSufficientForBills)
    }
}
