package com.vng.sajja.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vng.sajja.domain.model.ThemePresets
import com.vng.sajja.domain.model.WallpaperSettings
import com.vng.sajja.ui.components.GlassmorphicCard
import com.vng.sajja.ui.components.PresetButton
import com.vng.sajja.ui.viewmodel.SettingsViewModel

@Composable
fun ThemeSettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settings.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Text("Color Themes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PresetButton(
                        label = "Classic",
                        onClick = { viewModel.updateSettings(ThemePresets.Classic) },
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                    PresetButton(
                        label = "AMOLED",
                        onClick = { viewModel.updateSettings(ThemePresets.AMOLED) },
                        color = Color.Black,
                        textColor = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PresetButton(
                        label = "Sunset",
                        onClick = { viewModel.updateSettings(ThemePresets.Sunset) },
                        color = Color(0xFFDD2476),
                        textColor = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    PresetButton(
                        label = "Ocean",
                        onClick = { viewModel.updateSettings(ThemePresets.Ocean) },
                        color = Color(0xFF0083B0),
                        textColor = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Button(
                onClick = { viewModel.updateSettings(WallpaperSettings()) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Reset All Settings", fontWeight = FontWeight.Bold)
            }
        }
    }
