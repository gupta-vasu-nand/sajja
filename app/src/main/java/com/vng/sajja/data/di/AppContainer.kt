package com.vng.sajja.data.di

import android.content.Context
import com.vng.sajja.data.repository.AppSettingsRepositoryImpl
import com.vng.sajja.data.repository.WallpaperSettingsRepositoryImpl
import com.vng.sajja.domain.repository.AppSettingsRepository
import com.vng.sajja.domain.repository.WallpaperSettingsRepository

interface AppContainer {
    val wallpaperSettingsRepository: WallpaperSettingsRepository
    val appSettingsRepository: AppSettingsRepository
}

class AppContainerImpl(private val context: Context) : AppContainer {
    override val wallpaperSettingsRepository: WallpaperSettingsRepository by lazy {
        WallpaperSettingsRepositoryImpl(context)
    }

    override val appSettingsRepository: AppSettingsRepository by lazy {
        AppSettingsRepositoryImpl(context)
    }
}
