package com.spendwealthy.app.domain.service

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FireCalculatorEngineTest {

    private val engine = FireCalculatorEngine()

    @Test
    fun testStandardFireTargetCalculation() {
        // Desired monthly expense: 10,000,000 -> Annual: 120,000,000 -> 25x = 3,000,000,000
        val input = FireInput(
            currentAge = 30,
            targetRetirementAge = 55,
            currentNetWorth = 50_000_000L,
            monthlySavings = 5_000_000L,
            desiredMonthlyExpenseAtRetirement = 10_000_000L,
            annualReturnRate = 0.08,
            inflationRate = 0.035
        )

        val result = engine.calculate(input)

        assertEquals(3_000_000_000L, result.standardFireTarget)
        assertEquals(2_250_000_000L, result.leanFireTarget)
        assertEquals(4_500_000_000L, result.fatFireTarget)
        assertTrue(result.yearsToFire > 0.0)
        assertTrue(result.projectedFireAge in 31..65)
        assertTrue(result.accelerationBoosterYears >= 0.0)
        assertFalse(result.timeline.isEmpty())
    }

    @Test
    fun testAlreadyFireScenario() {
        val input = FireInput(
            currentAge = 40,
            targetRetirementAge = 50,
            currentNetWorth = 5_000_000_000L, // 5 Billion (exceeds 3 Billion FIRE target)
            monthlySavings = 0L,
            desiredMonthlyExpenseAtRetirement = 10_000_000L
        )

        val result = engine.calculate(input)

        assertTrue(result.isAlreadyFire)
        assertEquals(0.0, result.yearsToFire)
        assertEquals(40, result.projectedFireAge)
        assertEquals(1.0f, result.currentFireProgressPercent)
        assertTrue(result.isCoastFire)
    }

    @Test
    fun testCoastFireDetection() {
        // High net worth relative to a far retirement age (25 years away)
        val input = FireInput(
            currentAge = 25,
            targetRetirementAge = 55, // 30 years compound time
            currentNetWorth = 800_000_000L, // 800M will easily compound to > 2.4B at 8%
            monthlySavings = 0L,
            desiredMonthlyExpenseAtRetirement = 8_000_000L
        )

        val result = engine.calculate(input)

        assertTrue(result.isCoastFire)
        assertTrue(result.coastFireRequiredAmount <= 800_000_000L)
    }

    @Test
    fun testAccelerationBooster() {
        val input = FireInput(
            currentAge = 28,
            targetRetirementAge = 55,
            currentNetWorth = 20_000_000L,
            monthlySavings = 2_000_000L,
            desiredMonthlyExpenseAtRetirement = 8_000_000L
        )

        val result = engine.calculate(input)

        // Extra 500k savings must shave off time to reach FIRE
        assertTrue(result.accelerationBoosterYears > 0.0)
    }
}
