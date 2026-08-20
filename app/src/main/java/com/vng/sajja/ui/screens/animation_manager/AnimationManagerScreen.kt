package com.vng.sajja.ui.screens.animation_manager

import android.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vng.sajja.SajjaApplication
import com.vng.sajja.data.local.CustomAnimationEntity
import com.vng.sajja.domain.model.DigitalAnimType
import com.vng.sajja.domain.model.ParticleType
import com.vng.sajja.ui.components.GlassmorphicBatteryWarningDialog
import com.vng.sajja.ui.screens.animation_manager.components.*
import com.vng.sajja.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimationManagerScreen(
    viewModel: SettingsViewModel,
    onNavigateToBuilder: (Int?) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settings.collectAsState()
    val context = LocalContext.current
    val appContainer = (context.applicationContext as SajjaApplication).container
    val customAnimationDao = appContainer.customAnimationDao

    var savedPresets by remember { mutableStateOf(emptyList<CustomAnimationEntity>()) }
    var pendingPredefinedPreset by remember { mutableStateOf<PredefinedPreset?>(null) }
    var pendingCustomPreset by remember { mutableStateOf<CustomAnimationEntity?>(null) }

    val refreshPresets = {
        savedPresets = customAnimationDao.getAllAnimations()
    }

    LaunchedEffect(Unit) {
        refreshPresets()
    }

    val predefinedPresets = listOf(
        PredefinedPreset(
            name = "Matrix Rain",
            description = "Falling green rain code with vertical sliding digital numerals",
            particleType = ParticleType.RAIN,
            particleSpeed = 2.0f,
            particleCount = 100,
            digitalAnimType = DigitalAnimType.SLIDE,
            digitalAnimDuration = 250L,
            fontIndex = 0, // Monospace
            fontColor = Color.GREEN
        ),
        PredefinedPreset(
            name = "Winter Flurry",
            description = "Gentle snow flurries over soft fading clock faces",
            particleType = ParticleType.SNOW,
            particleSpeed = 0.8f,
            particleCount = 80,
            digitalAnimType = DigitalAnimType.FADE,
            digitalAnimDuration = 400L,
            fontIndex = 2, // Serif
            fontColor = Color.WHITE
        ),
        PredefinedPreset(
            name = "Magic Fireflies",
            description = "Bouncing digital clock surrounded by neon pulsing fireflies",
            particleType = ParticleType.FIREFLIES,
            particleSpeed = 1.3f,
            particleCount = 45,
            digitalAnimType = DigitalAnimType.BOUNCE,
            digitalAnimDuration = 300L,
            fontIndex = 3, // Default
            fontColor = Color.parseColor("#ADFF2F") // Neon green
        ),
        PredefinedPreset(
            name = "Meteor Shower",
            description = "Shooting stars flying fast right-to-left across the display",
            particleType = ParticleType.STARS,
            particleSpeed = 2.5f,
            particleCount = 30,
            digitalAnimType = DigitalAnimType.BOUNCE,
            digitalAnimDuration = 200L,
            fontIndex = 1, // Sans Serif
            fontColor = Color.parseColor("#FFFDD0") // Gold stars
        ),
        PredefinedPreset(
            name = "Summer Bubbles",
            description = "Calm floating bubbles with smooth cross-fading time updates",
            particleType = ParticleType.BUBBLES,
            particleSpeed = 0.7f,
            particleCount = 35,
            digitalAnimType = DigitalAnimType.FADE,
            digitalAnimDuration = 350L,
            fontIndex = 1, // Sans Serif
            fontColor = Color.WHITE
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Active Configurations Preview Card
        ActiveAnimationConfigCard(
            settings = settings,
            onBuildNew = { onNavigateToBuilder(null) },
            onSaveActive = { onNavigateToBuilder(-2) }
        )

        // Predefined Presets Card Section
        Text("Predefined Animation Presets", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        predefinedPresets.forEach { preset ->
            PresetEffectCard(
                preset = preset,
                onClick = { pendingPredefinedPreset = preset }
            )
        }

        // Custom Presets Card Section
        if (savedPresets.isNotEmpty()) {
            Text("Your Saved Animations", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            savedPresets.forEach { preset ->
                CustomEffectCard(
                    preset = preset,
                    onSelect = { pendingCustomPreset = preset },
                    onEdit = { onNavigateToBuilder(preset.id) },
                    onDelete = {
                        customAnimationDao.deleteAnimation(preset)
                        refreshPresets()
                    }
                )
            }
        }
    }

    pendingPredefinedPreset?.let { preset ->
        GlassmorphicBatteryWarningDialog(
            onConfirm = {
                viewModel.updateSettings(
                    settings.copy(
                        particleType = preset.particleType,
                        particleSpeed = preset.particleSpeed,
                        particleCount = preset.particleCount,
                        digitalAnimType = preset.digitalAnimType,
                        digitalAnimDuration = preset.digitalAnimDuration,
                        digitalClockFontIndex = preset.fontIndex,
                        numeralColor = preset.fontColor
                    )
                )
                pendingPredefinedPreset = null
            },
            onDismiss = { pendingPredefinedPreset = null }
        )
    }

    pendingCustomPreset?.let { preset ->
        GlassmorphicBatteryWarningDialog(
            onConfirm = {
                val type = try { ParticleType.valueOf(preset.particleType) } catch (e: Exception) { ParticleType.NONE }
                val animType = try { DigitalAnimType.valueOf(preset.digitalAnimType) } catch (e: Exception) { DigitalAnimType.NONE }
                viewModel.updateSettings(
                    settings.copy(
                        particleType = type,
                        particleSpeed = preset.particleSpeed,
                        particleCount = preset.particleCount,
                        digitalAnimType = animType,
                        digitalAnimDuration = preset.digitalAnimDuration,
                        customParticleShape = preset.customParticleShape,
                        customParticleText = preset.customParticleText,
                        customParticleDirection = preset.customParticleDirection,
                        customParticleColor = preset.customParticleColor
                    )
                )
                pendingCustomPreset = null
            },
            onDismiss = { pendingCustomPreset = null }
        )
    }
}
