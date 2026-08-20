package com.vng.sajja.ui.screens.animation_builder.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vng.sajja.domain.model.WallpaperSettings
import com.vng.sajja.ui.components.ColorOptionCard
import com.vng.sajja.ui.components.GlassmorphicCard
import com.vng.sajja.ui.theme.getContrastingTextColor

@Composable
fun CustomParticleDesignerCard(
    settings: WallpaperSettings,
    onSettingsChange: (WallpaperSettings) -> Unit,
    onRequestColorPicker: () -> Unit
) {
    val contrastColor = getContrastingTextColor(MaterialTheme.colorScheme.primary)
    val chipColors = FilterChipDefaults.filterChipColors(
        selectedContainerColor = MaterialTheme.colorScheme.primary,
        selectedLabelColor = contrastColor,
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
        Text("Custom Particle Designer", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        Text("Particle Shape", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("CIRCLE", "SQUARE", "LINE", "TEXT").forEach { shape ->
                val isSelected = settings.customParticleShape.uppercase() == shape
                FilterChip(
                    selected = isSelected,
                    onClick = { onSettingsChange(settings.copy(customParticleShape = shape)) },
                    shape = CircleShape,
                    label = {
                        Text(
                            text = shape,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) contrastColor else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    colors = chipColors
                )
            }
        }

        if (settings.customParticleShape.uppercase() == "TEXT") {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = settings.customParticleText,
                onValueChange = { onSettingsChange(settings.copy(customParticleText = it.take(5))) },
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            directions.forEach { dir ->
                val isSelected = settings.customParticleDirection.uppercase() == dir
                FilterChip(
                    selected = isSelected,
                    onClick = { onSettingsChange(settings.copy(customParticleDirection = dir)) },
                    shape = CircleShape,
                    label = {
                        Text(
                            text = dir,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) contrastColor else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    colors = chipColors
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(12.dp))

        ColorOptionCard(
            label = "Particle Color tint",
            color = Color(settings.customParticleColor),
            onClick = onRequestColorPicker
        )
    }
}
