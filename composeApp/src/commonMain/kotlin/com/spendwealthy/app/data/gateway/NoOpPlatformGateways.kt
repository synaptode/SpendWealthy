package com.spendwealthy.app.data.gateway

import com.spendwealthy.app.domain.gateway.BillingGateway
import com.spendwealthy.app.domain.gateway.BiometricGateway
import com.spendwealthy.app.domain.gateway.FileShareGateway
import com.spendwealthy.app.domain.gateway.GatewayResult
import com.spendwealthy.app.domain.gateway.NotificationScheduler
import com.spendwealthy.app.domain.gateway.SecureStorage
import com.spendwealthy.app.domain.model.BillReminder
import com.spendwealthy.app.domain.model.SubscriptionEntitlement
import com.spendwealthy.app.domain.model.SubscriptionPlan

class NoOpNotificationScheduler : NotificationScheduler {
    override suspend fun scheduleBillReminder(bill: BillReminder): GatewayResult<Unit> {
        return GatewayResult.Failure("Notification adapter belum tersedia di platform ini.", true)
    }

    override suspend fun cancelBillReminder(billId: String): GatewayResult<Unit> {
        return GatewayResult.Success(Unit)
    }
}

class NoOpBillingGateway : BillingGateway {
    override suspend fun currentEntitlement(): GatewayResult<SubscriptionEntitlement> {
        return GatewayResult.Success(SubscriptionEntitlement(SubscriptionPlan.FREE, true))
    }

    override suspend fun purchase(plan: SubscriptionPlan): GatewayResult<SubscriptionEntitlement> {
        return GatewayResult.Failure("Billing native belum dihubungkan.", true)
    }

    override suspend fun restore(): GatewayResult<SubscriptionEntitlement> {
        return GatewayResult.Failure("Restore purchase belum dihubungkan.", true)
    }
}

class NoOpBiometricGateway : BiometricGateway {
    override suspend fun isAvailable(): Boolean = false

    override suspend fun authenticate(reason: String): GatewayResult<Boolean> {
        return GatewayResult.Failure("Biometric adapter belum tersedia.", true)
    }
}

class InMemorySecureStorage : SecureStorage {
    private val values = mutableMapOf<String, String>()

    override suspend fun putString(key: String, value: String): GatewayResult<Unit> {
        values[key] = value
        return GatewayResult.Success(Unit)
    }

    override suspend fun getString(key: String): GatewayResult<String?> {
        return GatewayResult.Success(values[key])
    }

    override suspend fun remove(key: String): GatewayResult<Unit> {
        values.remove(key)
        return GatewayResult.Success(Unit)
    }
}

class NoOpFileShareGateway : FileShareGateway {
    override suspend fun shareTextFile(fileName: String, mimeType: String, content: String): GatewayResult<Unit> {
        return GatewayResult.Failure("File sharing belum tersedia di platform ini.", true)
    }
}
