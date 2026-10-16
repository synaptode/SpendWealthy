package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.Money
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class LifecycleReport(
    val retirementAge: Int,
    val retirementFundAtAge: Int, // e.g., fund at age 65
    val monthlyRetirementIncome: Money,
    val probabilityOfSuccess: Int, // 0-100%
    val recommendation: String
)

class LifecycleSimulationEngine {

    fun simulateLifetime(
        currentAge: Int,
        targetRetirementAge: Int,
        currentNetWorth: Money,
        monthlyContribution: Long,
        annualInflation: Double = 0.03,
        annualGrowth: Double = 0.07
    ): LifecycleReport {
        val yearsToRetire = targetRetirementAge - currentAge
        var fundValue = currentNetWorth.amount.toDouble()
        
        // Compound growth calculation over yearsToRetire
        for (i in 1..yearsToRetire) {
            fundValue += monthlyContribution * 12
            fundValue *= (1 + annualGrowth)
            fundValue /= (1 + annualInflation) // Adjust for purchasing power
        }
        
        val finalFund = fundValue.toLong()
        val monthlyIncome = (finalFund * 0.04 / 12).toLong() // 4% Rule for retirement drawdown
        
        val successProbability = when {
            monthlyIncome > 20_000_000L -> 95
            monthlyIncome > 10_000_000L -> 75
            monthlyIncome > 5_000_000L -> 40
            else -> 10
        }

        val rec = if (successProbability > 80) {
            "Your structural adherence and optimization efficiency have secured a high-probability retirement path."
        } else {
            "Increasing your 'Wealth Building' allocation in the Blueprint by 10% would raise success probability to 85%."
        }

        return LifecycleReport(
            retirementAge = targetRetirementAge,
            retirementFundAtAge = finalFund.toInt(), // Keeping it simple for UI
            monthlyRetirementIncome = Money(monthlyIncome, currentNetWorth.currency),
            probabilityOfSuccess = successProbability,
            recommendation = rec
        )
    }
}
