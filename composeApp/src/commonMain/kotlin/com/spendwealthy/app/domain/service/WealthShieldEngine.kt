package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.Money

data class WealthShieldStatus(
    val isActive: Boolean,
    val protectionLevel: Int, // 0-100
    val shieldedAssets: Money,
    val activeProtocols: List<String>,
    val triggerReason: String? = null
)

class WealthShieldEngine {

    fun evaluateShieldStatus(
        context: FinancialContext,
        anomalies: List<FinancialAnomaly>,
        totalAssets: Money
    ): WealthShieldStatus {
        val hasAnomaly = anomalies.isNotEmpty()
        val isStressful = context == FinancialContext.MONTH_END_CRUNCH
        
        val shouldActivate = hasAnomaly && isStressful
        val level = if (shouldActivate) 85 else if (isStressful) 40 else 10
        
        val shielded = if (shouldActivate) {
            Money((totalAssets.amount * 0.7).toLong(), totalAssets.currency)
        } else {
            Money(0, totalAssets.currency)
        }

        val protocols = mutableListOf<String>()
        if (shouldActivate) {
            protocols.add("Discretionary Lock")
            protocols.add("Priority Bill Shielding")
            protocols.add("Anti-Impulse Verification")
        } else if (isStressful) {
            protocols.add("Soft Budget Cap")
            protocols.add("Manual Review Only")
        } else {
            protocols.add("Standard Monitoring")
        }

        return WealthShieldStatus(
            isActive = shouldActivate,
            protectionLevel = level,
            shieldedAssets = shielded,
            activeProtocols = protocols,
            triggerReason = if (shouldActivate) "Combined detection of spending anomalies and month-end financial pressure." else null
        )
    }
}
