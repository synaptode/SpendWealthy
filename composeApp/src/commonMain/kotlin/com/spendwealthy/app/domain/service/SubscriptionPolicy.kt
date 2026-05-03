package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.FeatureAccess
import com.spendwealthy.app.domain.model.FreePlanLimits
import com.spendwealthy.app.domain.model.SubscriptionEntitlement

class SubscriptionPolicy(
    private val limits: FreePlanLimits = FreePlanLimits()
) {
    fun canAddWallet(currentWalletCount: Int, entitlement: SubscriptionEntitlement): FeatureAccess {
        return checkLimit(
            entitlement = entitlement,
            currentCount = currentWalletCount,
            limit = limits.maxWallets,
            reason = "Free plan mendukung maksimal ${limits.maxWallets} dompet. Upgrade Premium untuk unlimited wallet."
        )
    }

    fun canAddTransaction(currentMonthTransactionCount: Int, entitlement: SubscriptionEntitlement): FeatureAccess {
        return checkLimit(
            entitlement = entitlement,
            currentCount = currentMonthTransactionCount,
            limit = limits.maxTransactionsPerMonth,
            reason = "Free plan mendukung ${limits.maxTransactionsPerMonth} transaksi per bulan. Upgrade Premium untuk unlimited transaksi."
        )
    }

    fun canAddBillReminder(currentReminderCount: Int, entitlement: SubscriptionEntitlement): FeatureAccess {
        return checkLimit(
            entitlement = entitlement,
            currentCount = currentReminderCount,
            limit = limits.maxBillReminders,
            reason = "Free plan mendukung maksimal ${limits.maxBillReminders} pengingat tagihan. Upgrade Premium untuk unlimited reminder."
        )
    }

    fun canAddBudgetCategory(currentBudgetCount: Int, entitlement: SubscriptionEntitlement): FeatureAccess {
        return checkLimit(
            entitlement = entitlement,
            currentCount = currentBudgetCount,
            limit = limits.maxBudgetCategories,
            reason = "Free plan mendukung maksimal ${limits.maxBudgetCategories} kategori budget. Upgrade Premium untuk unlimited budget."
        )
    }

    private fun checkLimit(
        entitlement: SubscriptionEntitlement,
        currentCount: Int,
        limit: Int,
        reason: String
    ): FeatureAccess {
        if (entitlement.isPremium) return FeatureAccess(allowed = true)
        return FeatureAccess(
            allowed = currentCount < limit,
            reason = reason.takeIf { currentCount >= limit }
        )
    }
}
