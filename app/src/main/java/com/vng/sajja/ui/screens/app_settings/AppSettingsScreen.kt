package com.vng.sajja.ui.screens.app_settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.vng.sajja.ui.components.AdvancedColorPickerDialog
import com.vng.sajja.ui.screens.app_settings.components.AppAccentColorCard
import com.vng.sajja.ui.screens.app_settings.components.AppThemeModeCard
import com.vng.sajja.ui.viewmodel.SettingsViewModel

@Composable
fun AppSettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val appSettings by viewModel.appSettings.collectAsState()
    var showColorPicker by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Theme Mode Card (Light, Dark, System)
        AppThemeModeCard(
            currentMode = appSettings.themeMode,
            onThemeModeSelected = { viewModel.updateAppThemeMode(it) }
        )

        // Accent Color Palette Card
        AppAccentColorCard(
            appSettings = appSettings,
            onSelectPresetColor = { viewModel.updateAppPrimaryColor(it) },
            onRequestCustomColor = { showColorPicker = true }
        )
    }

    if (showColorPicker) {
        val initialColor = if (appSettings.customPrimaryColorHex != null) {
            Color(android.graphics.Color.parseColor(appSettings.customPrimaryColorHex))
        } else {
            MaterialTheme.colorScheme.primary
        }

        AdvancedColorPickerDialog(
            initial = initialColor,
            onDismiss = { showColorPicker = false }
        ) { color ->
            val hex = String.format("#%06X", 0xFFFFFF and color.toArgb())
            viewModel.updateAppCustomPrimaryColor(hex)
            showColorPicker = false
        }
    }
}
