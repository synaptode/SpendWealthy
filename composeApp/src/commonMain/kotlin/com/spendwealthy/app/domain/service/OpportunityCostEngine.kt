package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.domain.model.Wallet

data class WealthOpportunity(
    val id: String,
    val title: String,
    val potentialGain: Money,
    val costOfWaiting30Days: Money,
    val difficulty: String, // Easy, Medium, High
    val impactYears: Int
)

class OpportunityCostEngine {

    fun discoverOpportunities(
        wallets: List<Wallet>,
        idleThreshold: Long = 5_000_000L
    ): List<WealthOpportunity> {
        val opportunities = mutableListOf<WealthOpportunity>()
        
        val idleMain = wallets.find { it.name == "Main" }?.balance?.amount ?: 0L
        
        if (idleMain > idleThreshold) {
            val amountToInvest = idleMain - 2_000_000L // Keep 2M buffer
            // Mocking a 7% annual return over 20 years
            val potentialGain = (amountToInvest * 3.87).toLong() // Simple multiplier for 20 years at 7%
            val monthlyLoss = (potentialGain / (20 * 12)).toLong()
            
            opportunities.add(WealthOpportunity(
                id = "opp_idle_cash",
                title = "Compound Growth Accelerator",
                potentialGain = Money(potentialGain, "IDR"),
                costOfWaiting30Days = Money(monthlyLoss, "IDR"),
                difficulty = "Easy",
                impactYears = 20
            ))
        }

        return opportunities
    }
}
