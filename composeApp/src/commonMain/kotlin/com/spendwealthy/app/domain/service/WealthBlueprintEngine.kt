package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.domain.model.Wallet

data class WealthBlueprint(
    val id: String,
    val name: String,
    val allocations: List<AllocationTarget>,
    val adherenceScore: Int
)

data class AllocationTarget(
    val category: String,
    val percentage: Double,
    val currentAmount: Money,
    val targetAmount: Money
)

class WealthBlueprintEngine {

    fun generateBlueprint(
        wallets: List<Wallet>,
        monthlyIncome: Long
    ): WealthBlueprint {
        // Principal's 50/30/20 Rule + 10% Wealth Building
        val essentialsBalance = wallets.firstOrNull { it.name == "Main" }?.balance?.amount ?: 0L
        val wealthBalance = wallets.filter { it.name != "Main" }.sumOf { wallet -> wallet.balance.amount }
        val targets = listOf(
            AllocationTarget("Essentials", 0.50, Money(essentialsBalance, "IDR"), Money((monthlyIncome * 0.50).toLong(), "IDR")),
            AllocationTarget("Lifestyle", 0.20, Money(0, "IDR"), Money((monthlyIncome * 0.20).toLong(), "IDR")),
            AllocationTarget("Wealth Building", 0.30, Money(wealthBalance, "IDR"), Money((monthlyIncome * 0.30).toLong(), "IDR"))
        )
        
        val totalAdherence = targets.map { 
            val ratio = if (it.targetAmount.amount > 0) it.currentAmount.amount.toDouble() / it.targetAmount.amount.toDouble() else 1.0
            (ratio * 100).coerceAtMost(100.0)
        }.average().toInt()

        return WealthBlueprint(
            id = "bp_standard",
            name = "Principal Wealth Blueprint",
            allocations = targets,
            adherenceScore = totalAdherence
        )
    }
}
