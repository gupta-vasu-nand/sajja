package com.vng.sajja.ui.screens.clock.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vng.sajja.domain.model.ParticleType
import com.vng.sajja.domain.model.WallpaperSettings
import com.vng.sajja.ui.components.ColorOptionCard
import com.vng.sajja.ui.components.GlassmorphicBatteryWarningDialog
import com.vng.sajja.ui.components.GlassmorphicCard
import com.vng.sajja.ui.components.SliderOption
import com.vng.sajja.ui.components.ToggleOption
import com.vng.sajja.ui.theme.getContrastingTextColor
import com.vng.sajja.ui.utils.toComposeColor

@Composable
fun DateAndDayCard(
    settings: WallpaperSettings,
    onSettingsChange: (WallpaperSettings) -> Unit,
    onRequestColorPicker: (String) -> Unit
) {
    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
        Text("Date & Day Options", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        ToggleOption(
            label = "Show Date",
            checked = settings.showDate,
            onCheckedChange = { onSettingsChange(settings.copy(showDate = it)) }
        )

        if (settings.showDate) {
            ColorOptionCard(
                label = "Date Color",
                color = android.graphics.Color.valueOf(settings.dateColor).toComposeColor(),
                onClick = { onRequestColorPicker("date_color") }
            )
            SliderOption(
                label = "Date Size",
                value = settings.dateSize,
                onValueChange = { onSettingsChange(settings.copy(dateSize = it)) },
                valueRange = 20f..60f,
                unit = "px"
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        ToggleOption(
            label = "Show Day",
            checked = settings.showDay,
            onCheckedChange = { onSettingsChange(settings.copy(showDay = it)) }
        )

        if (settings.showDay) {
            ColorOptionCard(
                label = "Day Color",
                color = android.graphics.Color.valueOf(settings.dayColor).toComposeColor(),
                onClick = { onRequestColorPicker("day_color") }
            )
            SliderOption(
                label = "Day Size",
                value = settings.daySize,
                onValueChange = { onSettingsChange(settings.copy(daySize = it)) },
                valueRange = 20f..60f,
                unit = "px"
            )
        }
    }
}

@Composable
fun ParticleEffectsCard(
    settings: WallpaperSettings,
    onSettingsChange: (WallpaperSettings) -> Unit
) {
    var pendingParticleType by remember { mutableStateOf<ParticleType?>(null) }

    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
        Text("Background Particle Effects", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "Add live floating animation effects to your wallpaper background",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))

        val contrastColor = getContrastingTextColor(MaterialTheme.colorScheme.primary)
        val chipColors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = contrastColor,
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ParticleType.entries.forEach { type ->
                val isSelected = settings.particleType == type
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        if (type != ParticleType.NONE && type != settings.particleType) {
                            pendingParticleType = type
                        } else {
                            onSettingsChange(settings.copy(particleType = type))
                        }
                    },
                    shape = CircleShape,
                    label = {
                        Text(
                            text = type.name,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) contrastColor else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    colors = chipColors
                )
            }
        }

        if (settings.particleType != ParticleType.NONE) {
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            SliderOption(
                label = "Particle Speed",
                value = settings.particleSpeed,
                onValueChange = { onSettingsChange(settings.copy(particleSpeed = it)) },
                valueRange = 0.2f..3.0f,
                unit = "x"
            )

            SliderOption(
                label = "Particle Density (Count)",
                value = settings.particleCount.toFloat(),
                onValueChange = { onSettingsChange(settings.copy(particleCount = it.toInt())) },
                valueRange = 10f..150f,
                unit = " particles"
            )
        }
    }

    pendingParticleType?.let { type ->
        GlassmorphicBatteryWarningDialog(
            onConfirm = {
                onSettingsChange(settings.copy(particleType = type))
                pendingParticleType = null
            },
            onDismiss = { pendingParticleType = null }
        )
    }
}
