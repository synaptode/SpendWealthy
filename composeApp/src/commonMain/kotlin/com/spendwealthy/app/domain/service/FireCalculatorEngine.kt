package com.spendwealthy.app.domain.service

import kotlin.math.pow
import kotlin.math.roundToInt

enum class FireMilestoneType {
    COAST_FIRE,
    LEAN_FIRE,
    STANDARD_FIRE,
    FAT_FIRE
}

data class FireMilestone(
    val type: FireMilestoneType,
    val title: String,
    val targetAmount: Long,
    val isAchieved: Boolean,
    val projectedAge: Int?
)

data class FireProjectionYear(
    val yearIndex: Int,
    val age: Int,
    val totalWealth: Long,
    val principalContributed: Long,
    val compoundInterestGain: Long,
    val fireTarget: Long
)

data class FireInput(
    val currentAge: Int = 28,
    val targetRetirementAge: Int = 55,
    val currentNetWorth: Long = 30_000_000L,
    val monthlySavings: Long = 3_000_000L,
    val desiredMonthlyExpenseAtRetirement: Long = 8_000_000L,
    val annualReturnRate: Double = 0.08, // 8% per annum
    val inflationRate: Double = 0.035 // 3.5% inflation
)

data class FireResult(
    val standardFireTarget: Long,
    val leanFireTarget: Long,
    val fatFireTarget: Long,
    val yearsToFire: Double,
    val projectedFireAge: Int,
    val currentFireProgressPercent: Float,
    val isAlreadyFire: Boolean,
    val isCoastFire: Boolean,
    val coastFireRequiredAmount: Long,
    val timeline: List<FireProjectionYear>,
    val monthlyPassiveIncomeAtFire: Long,
    val accelerationBoosterYears: Double,
    val insightMessage: String
)

/**
 * Autonomous Financial Independence & Retire Early (FIRE) and Wealth Compounding Engine.
 * Calculates FIRE targets using the global 4% Safe Withdrawal Rule (Rule of 25)
 * adjusted for real compound growth, inflation, and incremental acceleration scenarios.
 */
class FireCalculatorEngine {

    fun calculate(input: FireInput): FireResult {
        val safeMonthlyExpense = input.desiredMonthlyExpenseAtRetirement.coerceAtLeast(1_000_000L)
        val annualRetirementExpense = safeMonthlyExpense * 12

        // Standard FIRE Target = 25x Annual Expenses (4% rule)
        val standardFireTarget = annualRetirementExpense * 25
        val leanFireTarget = (standardFireTarget * 0.75).toLong()
        val fatFireTarget = (standardFireTarget * 1.50).toLong()

        val currentWealth = input.currentNetWorth.coerceAtLeast(0L)
        val currentProgress = (currentWealth.toFloat() / standardFireTarget.toFloat()).coerceIn(0f, 1f)
        val isAlreadyFire = currentWealth >= standardFireTarget

        // Real return rate adjusted for inflation
        val realAnnualReturn = (input.annualReturnRate - input.inflationRate).coerceAtLeast(0.01)
        val monthlyRate = realAnnualReturn / 12.0

        // Coast FIRE calculation: Net worth needed today that will grow to FIRE target by targetRetirementAge without further contributions
        val yearsToRetireAge = (input.targetRetirementAge - input.currentAge).coerceAtLeast(1)
        val compoundFactor = (1.0 + realAnnualReturn).pow(yearsToRetireAge.toDouble())
        val coastFireRequired = (standardFireTarget / compoundFactor).toLong().coerceAtLeast(0L)
        val isCoastFire = currentWealth >= coastFireRequired

        // Month-by-month simulation up to 40 years (480 months)
        val maxMonths = 480
        var balance = currentWealth.toDouble()
        var totalPrincipal = currentWealth.toDouble()
        var fireAchievedMonth: Int? = null

        val yearlyTimeline = mutableListOf<FireProjectionYear>()
        // Initial Year 0
        yearlyTimeline.add(
            FireProjectionYear(
                yearIndex = 0,
                age = input.currentAge,
                totalWealth = currentWealth,
                principalContributed = currentWealth,
                compoundInterestGain = 0L,
                fireTarget = standardFireTarget
            )
        )

        for (month in 1..maxMonths) {
            balance = (balance * (1.0 + monthlyRate)) + input.monthlySavings
            totalPrincipal += input.monthlySavings

            if (balance >= standardFireTarget && fireAchievedMonth == null) {
                fireAchievedMonth = month
            }

            if (month % 12 == 0) {
                val year = month / 12
                val currentAge = input.currentAge + year
                val wealthLong = balance.toLong().coerceAtLeast(0L)
                val principalLong = totalPrincipal.toLong().coerceAtLeast(0L)
                val interestLong = (wealthLong - principalLong).coerceAtLeast(0L)

                yearlyTimeline.add(
                    FireProjectionYear(
                        yearIndex = year,
                        age = currentAge,
                        totalWealth = wealthLong,
                        principalContributed = principalLong,
                        compoundInterestGain = interestLong,
                        fireTarget = standardFireTarget
                    )
                )

                if (currentAge >= 70 && year >= 15) {
                    break // Stop simulation beyond age 70 if adequate data is collected
                }
            }
        }

        val yearsToFire: Double = when {
            isAlreadyFire -> 0.0
            fireAchievedMonth != null -> fireAchievedMonth / 12.0
            else -> 40.0
        }

        val projectedFireAge = (input.currentAge + yearsToFire).roundToInt().coerceAtMost(80)

        // Acceleration scenario: What if user saves an extra Rp 500,000/month?
        val boosterSavings = input.monthlySavings + 500_000L
        var boosterBalance = currentWealth.toDouble()
        var boosterFireMonth: Int? = null
        for (m in 1..maxMonths) {
            boosterBalance = (boosterBalance * (1.0 + monthlyRate)) + boosterSavings
            if (boosterBalance >= standardFireTarget && boosterFireMonth == null) {
                boosterFireMonth = m
                break
            }
        }
        val boosterYears = if (boosterFireMonth != null) boosterFireMonth / 12.0 else yearsToFire
        val accelerationSaved = (yearsToFire - boosterYears).coerceAtLeast(0.0)

        val monthlyPassiveIncome = (standardFireTarget * (input.annualReturnRate / 12.0)).toLong()

        val insightMessage = when {
            isAlreadyFire -> "🎉 Selamat! Aset likuidmu telah melewati batas Financial Independence. Kamu bebas dari keharusan bekerja!"
            yearsToFire <= 5.0 -> "🚀 Super Cepat! Kamu berada di jalur bebas finansial dalam ${yearsToFire.roundToInt()} tahun. Pertahankan konsistensi tabunganmu!"
            isCoastFire -> "🛡️ Hebat! Kamu sudah mencapai status Coast FIRE. Asetmu saat ini akan tumbuh melampaui target FIRE di usia ${input.targetRetirementAge}!"
            else -> "⚡ Potensi Akselerasi: Cukup tambah tabungan Rp 500.000/bln untuk pensiun bebas finansial ${((accelerationSaved * 10).roundToInt() / 10.0)} tahun lebih cepat."
        }

        return FireResult(
            standardFireTarget = standardFireTarget,
            leanFireTarget = leanFireTarget,
            fatFireTarget = fatFireTarget,
            yearsToFire = yearsToFire,
            projectedFireAge = projectedFireAge,
            currentFireProgressPercent = currentProgress,
            isAlreadyFire = isAlreadyFire,
            isCoastFire = isCoastFire,
            coastFireRequiredAmount = coastFireRequired,
            timeline = yearlyTimeline,
            monthlyPassiveIncomeAtFire = monthlyPassiveIncome,
            accelerationBoosterYears = ((accelerationSaved * 10).roundToInt() / 10.0),
            insightMessage = insightMessage
        )
    }
}
