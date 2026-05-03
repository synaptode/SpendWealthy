package com.spendwealthy.app

import android.app.Application
import com.spendwealthy.app.core.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class SpendWealthyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@SpendWealthyApp)
        }
    }
}
