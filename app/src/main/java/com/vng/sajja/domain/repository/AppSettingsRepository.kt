package com.vng.sajja.domain.repository

import com.vng.sajja.domain.model.AppSettings

interface AppSettingsRepository {
    fun load(): AppSettings
    fun save(settings: AppSettings)
}
