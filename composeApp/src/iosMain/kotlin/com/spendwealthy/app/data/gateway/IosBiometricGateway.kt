package com.spendwealthy.app.data.gateway

import com.spendwealthy.app.domain.gateway.BiometricGateway
import com.spendwealthy.app.domain.gateway.GatewayResult
import kotlinx.cinterop.ExperimentalForeignApi
import platform.LocalAuthentication.LAContext
import platform.LocalAuthentication.LAPolicyDeviceOwnerAuthenticationWithBiometrics
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * iOS Biometric Gateway integrating Face ID and Touch ID via LocalAuthentication framework.
 * Supports iOS 11.0+ with graceful fallback handling.
 */
class IosBiometricGateway : BiometricGateway {

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun isAvailable(): Boolean {
        val context = LAContext()
        return context.canEvaluatePolicy(LAPolicyDeviceOwnerAuthenticationWithBiometrics, error = null)
    }

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun authenticate(reason: String): GatewayResult<Boolean> {
        val context = LAContext()
        if (!context.canEvaluatePolicy(LAPolicyDeviceOwnerAuthenticationWithBiometrics, error = null)) {
            return GatewayResult.Failure("Biometrik (Face ID / Touch ID) tidak tersedia atau belum dikonfigurasi di perangkat ini.", recoverable = false)
        }

        return suspendCoroutine { continuation ->
            context.evaluatePolicy(
                policy = LAPolicyDeviceOwnerAuthenticationWithBiometrics,
                localizedReason = reason
            ) { success, error ->
                if (success) {
                    continuation.resume(GatewayResult.Success(true))
                } else {
                    val message = error?.localizedDescription ?: "Autentikasi biometrik dibatalkan."
                    continuation.resume(GatewayResult.Failure(message, recoverable = true))
                }
            }
        }
    }
}
