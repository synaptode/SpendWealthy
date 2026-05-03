package com.spendwealthy.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class SubscriptionPlan {
    FREE,
    PREMIUM_PERSONAL,
    PREMIUM_FAMILY
}

@Serializable
data class SubscriptionEntitlement(
    val plan: SubscriptionPlan,
    val isActive: Boolean
) {
    val isPremium: Boolean
        get() = isActive && plan != SubscriptionPlan.FREE
}

data class FreePlanLimits(
    val maxWallets: Int = 2,
    val maxTransactionsPerMonth: Int = 30,
    val maxBillReminders: Int = 3,
    val maxBudgetCategories: Int = 5
)

data class FeatureAccess(
    val allowed: Boolean,
    val reason: String? = null
)
