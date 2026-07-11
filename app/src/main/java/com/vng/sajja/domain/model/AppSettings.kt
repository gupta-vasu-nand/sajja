package com.vng.sajja.domain.model

enum class AppThemeMode {
    LIGHT, DARK, SYSTEM
}

enum class AppColorPreset(val colorHex: String, val nameStr: String) {
    PURPLE("#D0BCFF", "Purple"),
    BLUE("#2196F3", "Blue"),
    GREEN("#4CAF50", "Green"),
    ORANGE("#FF9800", "Orange"),
    PINK("#E91E63", "Pink")
}

data class AppSettings(
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val primaryColor: AppColorPreset = AppColorPreset.PURPLE,
    val customPrimaryColorHex: String? = null
) {
    val activeColorHex: String
        get() = customPrimaryColorHex ?: primaryColor.colorHex
}
