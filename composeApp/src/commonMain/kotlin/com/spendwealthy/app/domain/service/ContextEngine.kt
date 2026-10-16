package com.spendwealthy.app.domain.service

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

enum class FinancialContext {
    STANDARD,
    PAYDAY_WEEK,
    MONTH_END_CRUNCH,
    HIGH_VELOCITY_SPENDING,
    FINANCIAL_ZEN
}

class ContextEngine {

    fun determineContext(
        telemetry: FinancialHealthTelemetry,
        resilience: ResilienceReport
    ): FinancialContext {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val dayOfMonth = now.dayOfMonth
        
        return when {
            // Payday usually 25th-30th or 1st-5th
            dayOfMonth in 25..31 || dayOfMonth in 1..5 -> FinancialContext.PAYDAY_WEEK
            
            // End of month (if not payday week, usually tight)
            dayOfMonth in 20..24 -> FinancialContext.MONTH_END_CRUNCH
            
            // High health and stability
            telemetry.healthTrend == HealthTrend.IMPROVING && resilience.resilienceScore > 85 -> 
                FinancialContext.FINANCIAL_ZEN
                
            // If spending rate is high relative to income
            telemetry.savingsRate < 0.05 -> FinancialContext.HIGH_VELOCITY_SPENDING
            
            else -> FinancialContext.STANDARD
        }
    }
}
