package com.spendwealthy.app.core.di

import com.spendwealthy.app.core.db.DriverFactory
import com.spendwealthy.app.data.gateway.IosBiometricGateway
import com.spendwealthy.app.data.gateway.IosFileShareGateway
import com.spendwealthy.app.data.gateway.IosNotificationScheduler
import com.spendwealthy.app.data.gateway.IosSecureStorage
import com.spendwealthy.app.data.gateway.NoOpBillingGateway
import com.spendwealthy.app.domain.gateway.BillingGateway
import com.spendwealthy.app.domain.gateway.BiometricGateway
import com.spendwealthy.app.domain.gateway.FileShareGateway
import com.spendwealthy.app.domain.gateway.NotificationScheduler
import com.spendwealthy.app.domain.gateway.SecureStorage
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single { DriverFactory() }
    single<NotificationScheduler> { IosNotificationScheduler() }
    single<BillingGateway> { NoOpBillingGateway() }
    single<BiometricGateway> { IosBiometricGateway() }
    single<SecureStorage> { IosSecureStorage() }
    single<FileShareGateway> { IosFileShareGateway() }
}

