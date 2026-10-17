package com.spendwealthy.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class PurchaseVerdict {
    SAFE_TO_BUY,
    PROCEED_WITH_CAUTION,
    HIGH_RISK_DEFICIT
}

@Serializable
data class PurchaseSimulationRequest(
    val itemName: String,
    val price: Money,
    val selectedWalletId: String? = null,
    val categoryId: String? = null
)

@Serializable
data class PurchaseSimulationResult(
    val verdict: PurchaseVerdict,
    val verdictTitle: String,
    val verdictMessage: String,
    val currentDailyAllowance: Money,
    val simulatedDailyAllowance: Money,
    val emergencyRunwayMonthsCurrent: Double,
    val emergencyRunwayMonthsSimulated: Double,
    val impactOnFamilyGoalDaysDelayed: Int,
    val recommendation: String,
    val canAffordDirectly: Boolean
)
