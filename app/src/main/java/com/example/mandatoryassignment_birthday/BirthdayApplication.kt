package com.example.mandatoryassignment_birthday

import android.app.Application
import com.example.mandatoryassignment_birthday.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin


class BirthdayApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@BirthdayApplication)
            modules(appModule)
        }
    }
}