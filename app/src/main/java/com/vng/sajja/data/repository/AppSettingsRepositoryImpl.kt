package com.vng.sajja.data.repository

import android.content.Context
import androidx.core.content.edit
import com.vng.sajja.domain.model.AppColorPreset
import com.vng.sajja.domain.model.AppSettings
import com.vng.sajja.domain.model.AppThemeMode
import com.vng.sajja.domain.repository.AppSettingsRepository

class AppSettingsRepositoryImpl(context: Context) : AppSettingsRepository {

    private val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    private val dao = com.vng.sajja.data.local.AppDatabase.getDatabase(context).appAccentColorDao()

    override fun load(): AppSettings {
        val themeStr = prefs.getString("theme_mode", AppThemeMode.SYSTEM.name)!!
        val colorStr = prefs.getString("primary_color_preset", AppColorPreset.PURPLE.name)!!
        val customColorHex = dao.getAccentColorSync()?.colorHex

        return AppSettings(
            themeMode = AppThemeMode.valueOf(themeStr),
            primaryColor = AppColorPreset.valueOf(colorStr),
            customPrimaryColorHex = customColorHex
        )
    }

    override fun save(settings: AppSettings) {
        prefs.edit {
            putString("theme_mode", settings.themeMode.name)
            putString("primary_color_preset", settings.primaryColor.name)
        }
        dao.saveAccentColorSync(com.vng.sajja.data.local.AppAccentColorEntity(colorHex = settings.customPrimaryColorHex))
    }
}
