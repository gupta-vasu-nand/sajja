package com.vng.sajja.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.vng.sajja.SajjaApplication
import com.vng.sajja.data.local.CustomAnimationEntity
import com.vng.sajja.domain.model.ClockPosition
import com.vng.sajja.domain.model.ClockType
import com.vng.sajja.domain.model.DigitalAnimType
import com.vng.sajja.domain.model.ParticleType
import com.vng.sajja.domain.model.WallpaperSettings
import com.vng.sajja.ui.LivePreview
import com.vng.sajja.ui.components.AdvancedColorPickerDialog
import com.vng.sajja.ui.components.ColorOptionCard
import com.vng.sajja.ui.components.GlassmorphicCard
import com.vng.sajja.ui.components.SliderOption
import com.vng.sajja.ui.theme.getContrastingTextColor
import com.vng.sajja.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimationBuilderScreen(
    viewModel: SettingsViewModel,
    navController: NavController,
    presetId: Int,
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                // Render live preview using local builder settings!
                LivePreview(settings = builderSettings)
            }
        }

        val contrastColor = getContrastingTextColor(MaterialTheme.colorScheme.primary)
        val chipColors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = contrastColor,
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Particle configuration card
        GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
            Text("Particle Settings", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            Text("Select Particle Effect Base", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(ParticleType.NONE, ParticleType.SNOW, ParticleType.BUBBLES, ParticleType.STARS).forEach { type ->
                        val isSelected = builderSettings.particleType == type
                        FilterChip(
                            selected = isSelected,
                            onClick = { builderSettings = builderSettings.copy(particleType = type) },
                            label = { Text(type.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                            colors = chipColors,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(ParticleType.FIREFLIES, ParticleType.RAIN, ParticleType.CUSTOM).forEach { type ->
                        val isSelected = builderSettings.particleType == type
                        FilterChip(
                            selected = isSelected,
                            onClick = { builderSettings = builderSettings.copy(particleType = type) },
                            label = { Text(type.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                            colors = chipColors,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            if (builderSettings.particleType != ParticleType.NONE) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                SliderOption(
                    label = "Particle Speed",
                    value = builderSettings.particleSpeed,
                    onValueChange = { builderSettings = builderSettings.copy(particleSpeed = it) },
                    valueRange = 0.2f..3.0f,
                    unit = "x"
                )

                SliderOption(
                    label = "Particle Density (Count)",
                    value = builderSettings.particleCount.toFloat(),
                    onValueChange = { builderSettings = builderSettings.copy(particleCount = it.toInt()) },
                    valueRange = 10f..150f,
                    unit = " particles"
                )
            }
        }

        // Custom Particle Designer Card
        if (builderSettings.particleType == ParticleType.CUSTOM) {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Text("Custom Particle Designer", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                Text("Particle Shape", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("CIRCLE", "SQUARE", "LINE", "TEXT").forEach { shape ->
                        val isSelected = builderSettings.customParticleShape.uppercase() == shape
                        FilterChip(
                            selected = isSelected,
                            onClick = { builderSettings = builderSettings.copy(customParticleShape = shape) },
                            label = { Text(shape, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                            colors = chipColors,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                if (builderSettings.customParticleShape.uppercase() == "TEXT") {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = builderSettings.customParticleText,
                        onValueChange = { builderSettings = builderSettings.copy(customParticleText = it.take(5)) },
                        label = { Text("Particle Glyphs / Emoji") },
                        placeholder = { Text("e.g. ❄ or ❤️ or 01") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                Text("Movement Trajectory", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                
                val directions = listOf("FLOAT", "UP", "DOWN", "LEFT", "RIGHT", "WAVE", "EXPLODE")
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        directions.take(4).forEach { dir ->
                            val isSelected = builderSettings.customParticleDirection.uppercase() == dir
                            FilterChip(
                                selected = isSelected,
                                onClick = { builderSettings = builderSettings.copy(customParticleDirection = dir) },
                                label = { Text(dir, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                                colors = chipColors,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        directions.drop(4).forEach { dir ->
                            val isSelected = builderSettings.customParticleDirection.uppercase() == dir
                            FilterChip(
                                selected = isSelected,
                                onClick = { builderSettings = builderSettings.copy(customParticleDirection = dir) },
                                label = { Text(dir, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                                colors = chipColors,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                ColorOptionCard(
                    label = "Particle Color tint",
                    color = Color(builderSettings.customParticleColor),
                    onClick = { showColorPicker = true }
                )
            }
        }

        // Time Switching Animations card
        GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
            Text("Switching Transitions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            Text("Select Switching Effect", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(DigitalAnimType.NONE, DigitalAnimType.SLIDE, DigitalAnimType.FADE, DigitalAnimType.BOUNCE).forEach { anim ->
                    val isSelected = builderSettings.digitalAnimType == anim
                    FilterChip(
                        selected = isSelected,
                        onClick = { builderSettings = builderSettings.copy(digitalAnimType = anim) },
                        label = { Text(anim.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        colors = chipColors,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (builderSettings.digitalAnimType != DigitalAnimType.NONE) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                SliderOption(
                    label = "Animation Duration",
                    value = builderSettings.digitalAnimDuration.toFloat(),
                    onValueChange = { builderSettings = builderSettings.copy(digitalAnimDuration = it.toLong()) },
                    valueRange = 100f..800f,
                    unit = "ms"
                )
            }
        }

        // Action Buttons Sticky Row
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
                            // Update database record
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
                            // Create database record
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

                        // Also apply it to active live wallpaper settings
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

                        // Go back to presets lists screen
                        navController.popBackStack()
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
