package com.spendwealthy.app.data.repository

import com.spendwealthy.app.domain.model.SubscriptionEntitlement
import com.spendwealthy.app.domain.model.SubscriptionPlan
import com.spendwealthy.app.domain.repository.PreferenceRepository
import com.spendwealthy.app.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalSubscriptionRepository(
    private val preferenceRepository: PreferenceRepository
) : SubscriptionRepository {

    override fun getEntitlement(): Flow<SubscriptionEntitlement> {
        return preferenceRepository.getString(KEY_PLAN).map { value ->
            val plan = value
                ?.let { runCatching { SubscriptionPlan.valueOf(it) }.getOrNull() }
                ?: SubscriptionPlan.FREE
            SubscriptionEntitlement(plan = plan, isActive = true)
        }
    }

    override suspend fun setLocalPlan(plan: SubscriptionPlan) {
        preferenceRepository.setString(KEY_PLAN, plan.name)
    }

    private companion object {
        const val KEY_PLAN = "subscription_plan"
    }
}
