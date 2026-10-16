package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.*
import kotlinx.datetime.*

class AnalyticsService {
    
    fun calculateMonthlyAutomationImpact(logs: List<AutoMoveLog>): Money {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val currentMonthLogs = logs.filter { 
            val logDate = it.executedAt.toLocalDateTime(TimeZone.currentSystemDefault())
            logDate.month == now.month && logDate.year == now.year && it.status == AutoMoveStatus.SUCCESS
        }
        
        val totalAmount = currentMonthLogs.sumOf { it.amount.amount }
        return Money(totalAmount, "IDR")
    }

    fun calculateAnnualGrowthProjection(autoMoves: List<AutoMove>): Money {
        val annualTotal = autoMoves.filter { it.isEnabled }.sumOf { move ->
            val runsPerYear = when (move.frequency) {
                RecurrenceType.DAILY -> 365
                RecurrenceType.WEEKLY -> 52
                RecurrenceType.MONTHLY -> 12
                RecurrenceType.YEARLY -> 1
                else -> 0
            }
            move.amount.amount * runsPerYear
        }
        return Money(annualTotal, "IDR")
    }

    fun generateMonthlyWrapup(
        logs: List<AutoMoveLog>,
        optimizationLogs: List<OptimizationLog>,
        goalsReached: Int
    ): String {
        val totalMoved = logs.filter { it.status == AutoMoveStatus.SUCCESS }.sumOf { it.amount.amount }
        val totalImpact = optimizationLogs.sumOf { it.impactScore }
        
        return "This month, you've moved Rp ${totalMoved/1000}k toward your future! You achieved $goalsReached goals and boosted your wealth by $totalImpact% through AI optimizations. Keep it up! 🚀"
    }
}
