package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.BillReminder
import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.domain.model.Wallet
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

data class LiquidityReport(
    val safetyScore: Int, // 0-100
    val daysUntilLiquidityGap: Int?,
    val requiredBuffer: Money,
    val currentBuffer: Money,
    val riskLevel: String,
    val criticalDate: String? = null
)

class LiquiditySimulationEngine {

    fun simulateLiquidity(
        wallets: List<Wallet>,
        bills: List<BillReminder>,
        projectedIncome: Long,
        next30DaysSpending: Long
    ): LiquidityReport {
        val currentCash = wallets.sumOf { if (it.name == "Main") it.balance.amount else 0L }
        val currency = wallets.firstOrNull()?.balance?.currency ?: "IDR"
        
        val totalBills = bills.sumOf { it.amount.amount }
        val totalOutflow = totalBills + next30DaysSpending
        
        val netPosition = currentCash + projectedIncome - totalOutflow
        val safetyScore = if (totalOutflow > 0) {
            ((currentCash + projectedIncome).toDouble() / totalOutflow.toDouble() * 100).toInt().coerceIn(0, 100)
        } else 100
        
        val risk = when {
            safetyScore > 120 -> "Fortress Liquidity"
            safetyScore > 100 -> "Stable"
            safetyScore > 80 -> "Caution"
            else -> "Critical Gap"
        }
        
        val gapDays = if (safetyScore < 100) 15 else null // Simple mock logic for gap
        
        val criticalDate = if (gapDays != null) {
            Clock.System.now().plus(gapDays, DateTimeUnit.DAY, TimeZone.currentSystemDefault()).toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
        } else null

        return LiquidityReport(
            safetyScore = safetyScore,
            daysUntilLiquidityGap = gapDays,
            requiredBuffer = Money(totalOutflow, currency),
            currentBuffer = Money(currentCash + projectedIncome, currency),
            riskLevel = risk,
            criticalDate = criticalDate
        )
    }
}
