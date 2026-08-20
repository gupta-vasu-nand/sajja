package com.vng.sajja.ui.screens.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vng.sajja.domain.model.WallpaperSettings
import com.vng.sajja.ui.screens.theme.components.ThemePresetCard
import com.vng.sajja.ui.viewmodel.SettingsViewModel

@Composable
fun ThemeSettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ThemePresetCard(
            onApplyTheme = { viewModel.updateSettings(it) },
            onResetAll = { viewModel.updateSettings(WallpaperSettings()) }
        )
    }
}
