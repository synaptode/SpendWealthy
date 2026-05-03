package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.SubscriptionEntitlement
import com.spendwealthy.app.domain.model.SubscriptionPlan
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SubscriptionPolicyTest {

    private val policy = SubscriptionPolicy()

    @Test
    fun freePlanBlocksWalletsAtLimit() {
        val access = policy.canAddWallet(
            currentWalletCount = 2,
            entitlement = SubscriptionEntitlement(SubscriptionPlan.FREE, true)
        )

        assertFalse(access.allowed)
        assertTrue(access.reason?.contains("dompet") == true)
    }

    @Test
    fun premiumPlanAllowsWalletsBeyondFreeLimit() {
        val access = policy.canAddWallet(
            currentWalletCount = 99,
            entitlement = SubscriptionEntitlement(SubscriptionPlan.PREMIUM_PERSONAL, true)
        )

        assertTrue(access.allowed)
    }

    @Test
    fun freePlanAllowsTransactionsBelowMonthlyLimit() {
        val access = policy.canAddTransaction(
            currentMonthTransactionCount = 29,
            entitlement = SubscriptionEntitlement(SubscriptionPlan.FREE, true)
        )

        assertTrue(access.allowed)
    }

    @Test
    fun freePlanBlocksTransactionsAtMonthlyLimit() {
        val access = policy.canAddTransaction(
            currentMonthTransactionCount = 30,
            entitlement = SubscriptionEntitlement(SubscriptionPlan.FREE, true)
        )

        assertFalse(access.allowed)
    }
}
