package com.vng.sajja

import android.app.Application
import com.vng.sajja.data.di.AppContainer
import com.vng.sajja.data.di.AppContainerImpl

class SajjaApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this)
    }
}
