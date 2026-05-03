package com.spendwealthy.app.core.di

import com.spendwealthy.app.core.db.DriverFactory
import com.spendwealthy.app.data.gateway.InMemorySecureStorage
import com.spendwealthy.app.data.gateway.NoOpBillingGateway
import com.spendwealthy.app.data.gateway.NoOpBiometricGateway
import com.spendwealthy.app.data.gateway.NoOpFileShareGateway
import com.spendwealthy.app.data.gateway.NoOpNotificationScheduler
import com.spendwealthy.app.domain.gateway.BillingGateway
import com.spendwealthy.app.domain.gateway.BiometricGateway
import com.spendwealthy.app.domain.gateway.FileShareGateway
import com.spendwealthy.app.domain.gateway.NotificationScheduler
import com.spendwealthy.app.domain.gateway.SecureStorage
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single { DriverFactory() }
    single<NotificationScheduler> { NoOpNotificationScheduler() }
    single<BillingGateway> { NoOpBillingGateway() }
    single<BiometricGateway> { NoOpBiometricGateway() }
    single<SecureStorage> { InMemorySecureStorage() }
    single<FileShareGateway> { NoOpFileShareGateway() }
}
