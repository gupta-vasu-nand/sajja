package com.vng.sajja.ui.screens

import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vng.sajja.SajjaApplication
import com.vng.sajja.data.local.CustomAnimationEntity
import com.vng.sajja.domain.model.DigitalAnimType
import com.vng.sajja.domain.model.ParticleType
import com.vng.sajja.domain.model.WallpaperSettings
import com.vng.sajja.ui.components.GlassmorphicCard
import com.vng.sajja.ui.components.SliderOption
import com.vng.sajja.ui.components.ToggleOption
import com.vng.sajja.ui.theme.getContrastingTextColor
import com.vng.sajja.ui.viewmodel.SettingsViewModel

data class PredefinedPreset(
    val name: String,
    val description: String,
    val particleType: ParticleType,
    val particleSpeed: Float,
    val particleCount: Int,
    val digitalAnimType: DigitalAnimType,
    val digitalAnimDuration: Long,
    val fontIndex: Int,
    val fontColor: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimationManagerScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settings.collectAsState()
    val context = LocalContext.current
    val appContainer = (context.applicationContext as SajjaApplication).container
    val customAnimationDao = appContainer.customAnimationDao

    var savedPresets by remember { mutableStateOf(emptyList<CustomAnimationEntity>()) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var presetName by remember { mutableStateOf("") }
    var presetToEdit by remember { mutableStateOf<CustomAnimationEntity?>(null) }

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
        GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
            Text("Active Animation Config", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Particle Effect: ${settings.particleType.name}", fontSize = 14.sp)
                Text("Particle Speed: ${settings.particleSpeed}x", fontSize = 14.sp)
                Text("Particle Count: ${settings.particleCount}", fontSize = 14.sp)
                Text("Digit Switch Animation: ${settings.digitalAnimType.name}", fontSize = 14.sp)
                Text("Transition Speed: ${settings.digitalAnimDuration}ms", fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    presetName = ""
                    presetToEdit = null
                    showSaveDialog = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Save, contentDescription = "Save")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Current Configuration")
            }
        }

        // Predefined Presets Card
        Text("Predefined Animation Presets", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        predefinedPresets.forEach { preset ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
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
                    },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(preset.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        AssistChip(
                            onClick = {},
                            label = { Text("Predefined") },
                            colors = AssistChipDefaults.assistChipColors(
                                labelColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(preset.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        // Saved Custom Animation presets Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Your Saved Animations", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Badge { Text("${savedPresets.size}") }
        }

        if (savedPresets.isEmpty()) {
            Text(
                "No custom animations saved yet. Customize the wallpaper and tap 'Save Current Configuration' above to create one!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            savedPresets.forEach { preset ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(preset.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(
                                    onClick = {
                                        viewModel.updateSettings(
                                            settings.copy(
                                                particleType = ParticleType.valueOf(preset.particleType),
                                                particleSpeed = preset.particleSpeed,
                                                particleCount = preset.particleCount,
                                                digitalAnimType = DigitalAnimType.valueOf(preset.digitalAnimType),
                                                digitalAnimDuration = preset.digitalAnimDuration
                                            )
                                        )
                                    }
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = "Apply Preset", tint = MaterialTheme.colorScheme.primary)
                                }
                                IconButton(
                                    onClick = {
                                        presetToEdit = preset
                                        presetName = preset.name
                                        showSaveDialog = true
                                    }
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit Name", tint = MaterialTheme.colorScheme.secondary)
                                }
                                IconButton(
                                    onClick = {
                                        customAnimationDao.deleteAnimation(preset)
                                        refreshPresets()
                                    }
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Effect: ${preset.particleType} | Speed: ${preset.particleSpeed}x | Count: ${preset.particleCount}\nSwitch Anim: ${preset.digitalAnimType} | Speed: ${preset.digitalAnimDuration}ms",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text(if (presetToEdit == null) "Save Custom Animation" else "Rename Preset") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enter a unique name to store this animation profile:")
                    TextField(
                        value = presetName,
                        onValueChange = { presetName = it },
                        placeholder = { Text("e.g. Neon Firefly Matrix") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (presetName.isNotBlank()) {
                            val preset = presetToEdit
                            if (preset != null) {
                                // Rename/Update operation
                                customAnimationDao.updateAnimation(preset.copy(name = presetName))
                            } else {
                                // Create operation
                                customAnimationDao.insertAnimation(
                                    CustomAnimationEntity(
                                        name = presetName,
                                        particleType = settings.particleType.name,
                                        particleSpeed = settings.particleSpeed,
                                        particleCount = settings.particleCount,
                                        digitalAnimType = settings.digitalAnimType.name,
                                        digitalAnimDuration = settings.digitalAnimDuration
                                    )
                                )
                            }
                            refreshPresets()
                            showSaveDialog = false
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
