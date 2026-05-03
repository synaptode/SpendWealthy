package com.spendwealthy.app.domain.gateway

import com.spendwealthy.app.domain.model.BillReminder
import com.spendwealthy.app.domain.model.SubscriptionEntitlement
import com.spendwealthy.app.domain.model.SubscriptionPlan

interface NotificationScheduler {
    suspend fun scheduleBillReminder(bill: BillReminder): GatewayResult<Unit>
    suspend fun cancelBillReminder(billId: String): GatewayResult<Unit>
}

interface BillingGateway {
    suspend fun currentEntitlement(): GatewayResult<SubscriptionEntitlement>
    suspend fun purchase(plan: SubscriptionPlan): GatewayResult<SubscriptionEntitlement>
    suspend fun restore(): GatewayResult<SubscriptionEntitlement>
}

interface BiometricGateway {
    suspend fun isAvailable(): Boolean
    suspend fun authenticate(reason: String): GatewayResult<Boolean>
}

interface SecureStorage {
    suspend fun putString(key: String, value: String): GatewayResult<Unit>
    suspend fun getString(key: String): GatewayResult<String?>
    suspend fun remove(key: String): GatewayResult<Unit>
}

interface FileShareGateway {
    suspend fun shareTextFile(fileName: String, mimeType: String, content: String): GatewayResult<Unit>
}

sealed class GatewayResult<out T> {
    data class Success<T>(val value: T) : GatewayResult<T>()
    data class Failure(val message: String, val recoverable: Boolean) : GatewayResult<Nothing>()
}
