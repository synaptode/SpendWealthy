package com.spendwealthy.app.domain.repository

import com.spendwealthy.app.domain.model.SubscriptionEntitlement
import com.spendwealthy.app.domain.model.SubscriptionPlan
import kotlinx.coroutines.flow.Flow

interface SubscriptionRepository {
    fun getEntitlement(): Flow<SubscriptionEntitlement>
    suspend fun setLocalPlan(plan: SubscriptionPlan)
}
