package com.vng.sajja.ui.navigation

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object BackgroundSettings : Screen("background_settings")
    object ClockSettings : Screen("clock_settings")
    object AnimationManager : Screen("animation_manager")
    object AnimationBuilder : Screen("animation_builder?presetId={presetId}") {
        fun createRoute(presetId: Int? = null): String {
            return if (presetId != null) "animation_builder?presetId=$presetId" else "animation_builder"
        }
    }
    object ThemeSettings : Screen("theme_settings")
    object ToolsSettings : Screen("tools_settings")
    object AppSettings : Screen("app_settings")
    object Analytics : Screen("analytics")
}
