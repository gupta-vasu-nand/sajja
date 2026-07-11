package com.vng.sajja.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.vng.sajja.ui.CollageScreen
import com.vng.sajja.ui.viewmodel.SettingsViewModel

@Composable
fun CollageSettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settings.collectAsState()

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        CollageScreen(
            settings = settings,
            onUpdateSettings = { viewModel.updateSettings(it) }
        )
    }
}
