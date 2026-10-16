package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.domain.model.OptimizerRecommendation
import kotlinx.datetime.Clock

data class WealthNarrative(
    val headline: String,
    val summary: String,
    val shadowImpact: Money, // Potential savings if all advice followed
    val nextBigMilestone: String
)

class WealthNarrativeEngine {

    fun generateNarrative(
        telemetry: FinancialHealthTelemetry,
        resilience: ResilienceReport,
        recommendations: List<OptimizerRecommendation>,
        userName: String
    ): WealthNarrative {
        val totalPotentialImpact = recommendations.fold(0L) { recommendationAcc, recommendation ->
            recommendationAcc + recommendation.actions.fold(0L) { actionAcc, action ->
                actionAcc + action.amount.amount
            }
        }
        
        val headline = when {
            telemetry.healthTrend == HealthTrend.IMPROVING && resilience.resilienceScore > 70 -> 
                "Financial Momentum is Strong, $userName! 🚀"
            telemetry.healthTrend == HealthTrend.DECLINING -> 
                "Course Correction Recommended, $userName 🧭"
            else -> "Steady as She Goes, $userName ⛵"
        }
        
        val summary = buildString {
            append("You currently have ${resilience.runwayMonths.toInt()} months of runway. ")
            if (telemetry.savingsRate > 0.2) {
                append("Your savings rate of ${(telemetry.savingsRate * 100).toInt()}% is exceptional. ")
            } else if (telemetry.savingsRate < 0.1) {
                append("We've detected a dip in your savings rate; let's look at your optimizations. ")
            }
            
            if (recommendations.isNotEmpty()) {
                append("There are ${recommendations.size} optimizations that could boost your wealth by Rp ${totalPotentialImpact / 1000}k.")
            }
        }
        
        val nextMilestone = if (resilience.runwayMonths < 6) {
            "Achieving 6 Months Runway (Need Rp ${(6 - resilience.runwayMonths).toLong() * resilience.averageMonthlyExpense.amount / 1000}k more)"
        } else {
            "Reaching 'Invincible' Status (12 Months Runway)"
        }

        return WealthNarrative(
            headline = headline,
            summary = summary,
            shadowImpact = Money(totalPotentialImpact, "IDR"),
            nextBigMilestone = nextMilestone
        )
    }
}
