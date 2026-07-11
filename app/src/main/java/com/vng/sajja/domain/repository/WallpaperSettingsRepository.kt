package com.vng.sajja.domain.repository

import com.vng.sajja.domain.model.WallpaperSettings

interface WallpaperSettingsRepository {
    fun load(): WallpaperSettings
    fun save(settings: WallpaperSettings)
}
