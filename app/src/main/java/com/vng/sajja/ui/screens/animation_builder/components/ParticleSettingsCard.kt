package com.vng.sajja.ui.screens.animation_builder.components

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
import com.vng.sajja.ui.components.GlassmorphicBatteryWarningDialog
import com.vng.sajja.ui.components.GlassmorphicCard
import com.vng.sajja.ui.components.SliderOption
import com.vng.sajja.ui.theme.getContrastingTextColor

@Composable
fun ParticleSettingsCard(
    settings: WallpaperSettings,
    onSettingsChange: (WallpaperSettings) -> Unit
) {
    var pendingParticleType by remember { mutableStateOf<ParticleType?>(null) }

    val contrastColor = getContrastingTextColor(MaterialTheme.colorScheme.primary)
    val chipColors = FilterChipDefaults.filterChipColors(
        selectedContainerColor = MaterialTheme.colorScheme.primary,
        selectedLabelColor = contrastColor,
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
        Text("Particle Settings", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        Text("Select Particle Effect Base", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

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
