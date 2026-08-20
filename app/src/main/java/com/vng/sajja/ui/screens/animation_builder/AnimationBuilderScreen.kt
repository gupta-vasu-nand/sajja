package com.vng.sajja.ui.screens.animation_builder

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vng.sajja.SajjaApplication
import com.vng.sajja.data.local.CustomAnimationEntity
import com.vng.sajja.domain.model.ClockPosition
import com.vng.sajja.domain.model.ClockType
import com.vng.sajja.domain.model.DigitalAnimType
import com.vng.sajja.domain.model.ParticleType
import com.vng.sajja.domain.model.WallpaperSettings
import com.vng.sajja.ui.LivePreview
import com.vng.sajja.ui.components.AdvancedColorPickerDialog
import com.vng.sajja.ui.screens.animation_builder.components.*
import com.vng.sajja.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimationBuilderScreen(
    viewModel: SettingsViewModel,
    presetId: Int = -1,
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val appContainer = (context.applicationContext as SajjaApplication).container
    val customAnimationDao = appContainer.customAnimationDao
    val activeSettings by viewModel.settings.collectAsState()

    var presetName by remember { mutableStateOf("New Animation") }
    var builderSettings by remember { mutableStateOf(WallpaperSettings(clockType = ClockType.DIGITAL)) }
    var showColorPicker by remember { mutableStateOf(false) }

    LaunchedEffect(presetId) {
        if (presetId >= 0) {
            val preset = customAnimationDao.getAllAnimations().find { it.id == presetId }
            if (preset != null) {
                presetName = preset.name
                builderSettings = WallpaperSettings(
                    clockType = ClockType.DIGITAL,
                    clockPosition = ClockPosition.CENTER,
                    particleType = ParticleType.valueOf(preset.particleType),
                    particleSpeed = preset.particleSpeed,
                    particleCount = preset.particleCount,
                    digitalAnimType = DigitalAnimType.valueOf(preset.digitalAnimType),
                    digitalAnimDuration = preset.digitalAnimDuration,
                    customParticleShape = preset.customParticleShape,
                    customParticleText = preset.customParticleText,
                    customParticleDirection = preset.customParticleDirection,
                    customParticleColor = preset.customParticleColor
                )
            }
        } else if (presetId == -2) {
            presetName = "My Clock Animation"
            builderSettings = activeSettings.copy(clockType = ClockType.DIGITAL)
        } else {
            presetName = "New Animation Profile"
            builderSettings = WallpaperSettings(
                clockType = ClockType.DIGITAL,
                clockPosition = ClockPosition.CENTER,
                particleType = ParticleType.NONE,
                particleSpeed = 1.0f,
                particleCount = 40,
                digitalAnimType = DigitalAnimType.NONE,
                digitalAnimDuration = 300L,
                customParticleShape = "CIRCLE",
                customParticleText = "✨",
                customParticleDirection = "UP",
                customParticleColor = android.graphics.Color.WHITE
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Dynamic Title
        Text(
            text = if (presetId >= 0) "Edit Animation Preset" else "Build Custom Animation",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Outlined Text Field for Preset Name
        OutlinedTextField(
            value = presetName,
            onValueChange = { presetName = it },
            label = { Text("Profile/Preset Name") },
            placeholder = { Text("e.g. Midnight Cosmic Storm") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Live Mini Preview Box
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Live Builder Preview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            LivePreview(
                settings = builderSettings,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
            )
        }

        // Base Particle settings card
        ParticleSettingsCard(
            settings = builderSettings,
            onSettingsChange = { builderSettings = it }
        )

        // Custom Particle Designer Card
        if (builderSettings.particleType == ParticleType.CUSTOM) {
            CustomParticleDesignerCard(
                settings = builderSettings,
                onSettingsChange = { builderSettings = it },
                onRequestColorPicker = { showColorPicker = true }
            )
        }

        // Time Switching Animations card
        SwitchingTransitionsCard(
            settings = builderSettings,
            onSettingsChange = { builderSettings = it }
        )

        // Action Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    viewModel.updateSettings(
                        activeSettings.copy(
                            particleType = builderSettings.particleType,
                            particleSpeed = builderSettings.particleSpeed,
                            particleCount = builderSettings.particleCount,
                            digitalAnimType = builderSettings.digitalAnimType,
                            digitalAnimDuration = builderSettings.digitalAnimDuration,
                            customParticleShape = builderSettings.customParticleShape,
                            customParticleText = builderSettings.customParticleText,
                            customParticleDirection = builderSettings.customParticleDirection,
                            customParticleColor = builderSettings.customParticleColor
                        )
                    )
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Icon(Icons.Default.FlashOn, contentDescription = "Test Apply")
                Spacer(modifier = Modifier.width(6.dp))
                Text("Test Apply", fontSize = 14.sp)
            }

            Button(
                onClick = {
                    if (presetName.isNotBlank()) {
                        if (presetId >= 0) {
                            customAnimationDao.updateAnimation(
                                CustomAnimationEntity(
                                    id = presetId,
                                    name = presetName,
                                    particleType = builderSettings.particleType.name,
                                    particleSpeed = builderSettings.particleSpeed,
                                    particleCount = builderSettings.particleCount,
                                    digitalAnimType = builderSettings.digitalAnimType.name,
                                    digitalAnimDuration = builderSettings.digitalAnimDuration,
                                    customParticleShape = builderSettings.customParticleShape,
                                    customParticleText = builderSettings.customParticleText,
                                    customParticleDirection = builderSettings.customParticleDirection,
                                    customParticleColor = builderSettings.customParticleColor
                                )
                            )
                        } else {
                            customAnimationDao.insertAnimation(
                                CustomAnimationEntity(
                                    name = presetName,
                                    particleType = builderSettings.particleType.name,
                                    particleSpeed = builderSettings.particleSpeed,
                                    particleCount = builderSettings.particleCount,
                                    digitalAnimType = builderSettings.digitalAnimType.name,
                                    digitalAnimDuration = builderSettings.digitalAnimDuration,
                                    customParticleShape = builderSettings.customParticleShape,
                                    customParticleText = builderSettings.customParticleText,
                                    customParticleDirection = builderSettings.customParticleDirection,
                                    customParticleColor = builderSettings.customParticleColor
                                )
                            )
                        }

                        viewModel.updateSettings(
                            activeSettings.copy(
                                particleType = builderSettings.particleType,
                                particleSpeed = builderSettings.particleSpeed,
                                particleCount = builderSettings.particleCount,
                                digitalAnimType = builderSettings.digitalAnimType,
                                digitalAnimDuration = builderSettings.digitalAnimDuration,
                                customParticleShape = builderSettings.customParticleShape,
                                customParticleText = builderSettings.customParticleText,
                                customParticleDirection = builderSettings.customParticleDirection,
                                customParticleColor = builderSettings.customParticleColor
                            )
                        )

                        onNavigateBack()
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Save, contentDescription = "Save Profile")
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save Profile", fontSize = 14.sp)
            }
        }
    }

    if (showColorPicker) {
        AdvancedColorPickerDialog(
            initial = Color(builderSettings.customParticleColor),
            onDismiss = { showColorPicker = false }
        ) { color ->
            builderSettings = builderSettings.copy(customParticleColor = color.toArgb())
            showColorPicker = false
        }
    }
}
