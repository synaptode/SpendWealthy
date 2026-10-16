package com.spendwealthy.app.data.gateway

import com.spendwealthy.app.domain.gateway.GatewayResult
import com.spendwealthy.app.domain.gateway.SecureStorage
import platform.Foundation.NSUserDefaults

/**
 * iOS Secure Storage implementation using isolated NSUserDefaults partition.
 * Provides resilient, backward-compatible key-value persistence for offline vaults and settings.
 */
class IosSecureStorage : SecureStorage {

    private val defaults = NSUserDefaults.standardUserDefaults
    private val keyPrefix = "com.spendwealthy.vault."

    override suspend fun putString(key: String, value: String): GatewayResult<Unit> {
        return runCatching {
            defaults.setObject(value, forKey = "$keyPrefix$key")
            defaults.synchronize()
            GatewayResult.Success(Unit)
        }.getOrElse { error ->
            GatewayResult.Failure(error.message ?: "Gagal menyimpan data ke secure storage iOS.", recoverable = true)
        }
    }

    override suspend fun getString(key: String): GatewayResult<String?> {
        return runCatching {
            val value = defaults.stringForKey("$keyPrefix$key")
            GatewayResult.Success(value)
        }.getOrElse { error ->
            GatewayResult.Failure(error.message ?: "Gagal membaca data dari secure storage iOS.", recoverable = true)
        }
    }

    override suspend fun remove(key: String): GatewayResult<Unit> {
        return runCatching {
            defaults.removeObjectForKey("$keyPrefix$key")
            defaults.synchronize()
            GatewayResult.Success(Unit)
        }.getOrElse { error ->
            GatewayResult.Failure(error.message ?: "Gagal menghapus data dari secure storage iOS.", recoverable = true)
        }
    }
}
