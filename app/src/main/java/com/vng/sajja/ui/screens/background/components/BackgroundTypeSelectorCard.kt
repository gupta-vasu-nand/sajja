package com.vng.sajja.ui.screens.background.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vng.sajja.domain.model.BackgroundType
import com.vng.sajja.domain.model.WallpaperSettings
import com.vng.sajja.ui.components.ColorOptionCard
import com.vng.sajja.ui.components.GlassmorphicCard
import com.vng.sajja.ui.theme.getContrastingTextColor
import com.vng.sajja.ui.utils.toComposeColor

@Composable
fun BackgroundTypeSelectorCard(
    settings: WallpaperSettings,
    onSettingsChange: (WallpaperSettings) -> Unit,
    onRequestColorPicker: (String) -> Unit,
    onPickImage: () -> Unit
) {
    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
        Text("Background Type", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BackgroundType.entries.forEach { type ->
                val isSelected = settings.backgroundType == type
                val contrastColor = getContrastingTextColor(MaterialTheme.colorScheme.primary)
                FilterChip(
                    selected = isSelected,
                    onClick = { onSettingsChange(settings.copy(backgroundType = type)) },
                    shape = CircleShape,
                    label = {
                        Text(
                            text = type.name,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) contrastColor else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = contrastColor,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (settings.backgroundType) {
            BackgroundType.SOLID -> {
                ColorOptionCard(
                    label = "Background Color",
                    color = android.graphics.Color.valueOf(settings.backgroundColor).toComposeColor(),
                    onClick = { onRequestColorPicker("bg_color") }
                )
            }
            BackgroundType.GRADIENT -> {
                ColorOptionCard(
                    label = "Start Color",
                    color = android.graphics.Color.valueOf(settings.gradientStartColor).toComposeColor(),
                    onClick = { onRequestColorPicker("grad_start") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                ColorOptionCard(
                    label = "End Color",
                    color = android.graphics.Color.valueOf(settings.gradientEndColor).toComposeColor(),
                    onClick = { onRequestColorPicker("grad_end") }
                )
            }
            BackgroundType.IMAGE -> {
                Text(
                    "Choose an image from your device as the wallpaper background.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onPickImage,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Image, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Select Image")
                }
                val bgUri = settings.backgroundImageUri
                if (bgUri != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Selected: ${bgUri.substringAfterLast("/")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
