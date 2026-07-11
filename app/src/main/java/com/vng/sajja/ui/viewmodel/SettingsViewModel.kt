package com.vng.sajja.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vng.sajja.domain.model.*
import com.vng.sajja.domain.repository.AppSettingsRepository
import com.vng.sajja.domain.repository.WallpaperSettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(
    private val wallpaperSettingsRepository: WallpaperSettingsRepository,
    private val appSettingsRepository: AppSettingsRepository
) : ViewModel() {

    private val _settings = MutableStateFlow(wallpaperSettingsRepository.load())
    val settings: StateFlow<WallpaperSettings> = _settings.asStateFlow()

    private val _appSettings = MutableStateFlow(appSettingsRepository.load())
    val appSettings: StateFlow<AppSettings> = _appSettings.asStateFlow()

    fun updateSettings(newSettings: WallpaperSettings) {
        _settings.value = newSettings
        wallpaperSettingsRepository.save(newSettings)
    }

    fun importTheme(json: String): Boolean {
        return try {
            val imported = ThemeSerializer.import(json)
            updateSettings(imported)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun exportTheme(): String {
        return ThemeSerializer.export(_settings.value)
    }

    fun savePreset(context: android.content.Context) {
        val preset = ThemeSerializer.export(_settings.value)
        val prefs = context.getSharedPreferences("presets", android.content.Context.MODE_PRIVATE)
        prefs.edit().putString("last_preset", preset).apply()
    }

    fun loadLastPreset(context: android.content.Context): Boolean {
        val prefs = context.getSharedPreferences("presets", android.content.Context.MODE_PRIVATE)
        val preset = prefs.getString("last_preset", null)
        return if (preset != null) {
            importTheme(preset)
        } else {
            false
        }
    }

    fun updateAppThemeMode(themeMode: AppThemeMode) {
        val current = _appSettings.value.copy(themeMode = themeMode)
        _appSettings.value = current
        appSettingsRepository.save(current)
    }

    fun updateAppPrimaryColor(preset: AppColorPreset) {
        val current = _appSettings.value.copy(primaryColor = preset, customPrimaryColorHex = null)
        _appSettings.value = current
        appSettingsRepository.save(current)
    }

    fun updateAppCustomPrimaryColor(colorHex: String?) {
        val current = _appSettings.value.copy(customPrimaryColorHex = colorHex)
        _appSettings.value = current
        appSettingsRepository.save(current)
    }
}

class SettingsViewModelFactory(
    private val wallpaperSettingsRepository: WallpaperSettingsRepository,
    private val appSettingsRepository: AppSettingsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(wallpaperSettingsRepository, appSettingsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
