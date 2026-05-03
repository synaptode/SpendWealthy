package com.spendwealthy.app.core.di

import com.spendwealthy.app.core.db.DriverFactory
import com.spendwealthy.app.data.gateway.AndroidFileShareGateway
import com.spendwealthy.app.data.gateway.AndroidNotificationScheduler
import com.spendwealthy.app.data.gateway.AndroidSecureStorage
import com.spendwealthy.app.data.gateway.NoOpBillingGateway
import com.spendwealthy.app.data.gateway.NoOpBiometricGateway
import com.spendwealthy.app.domain.gateway.BillingGateway
import com.spendwealthy.app.domain.gateway.BiometricGateway
import com.spendwealthy.app.domain.gateway.FileShareGateway
import com.spendwealthy.app.domain.gateway.NotificationScheduler
import com.spendwealthy.app.domain.gateway.SecureStorage
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single { DriverFactory(get()) }
    single<NotificationScheduler> { AndroidNotificationScheduler(get()) }
    single<BillingGateway> { NoOpBillingGateway() }
    single<BiometricGateway> { NoOpBiometricGateway() }
    single<SecureStorage> { AndroidSecureStorage(get()) }
    single<FileShareGateway> { AndroidFileShareGateway(get()) }
}
