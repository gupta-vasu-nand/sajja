package com.vng.sajja.ui.screens.clock.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vng.sajja.domain.model.ClockType
import com.vng.sajja.domain.model.WallpaperSettings
import com.vng.sajja.ui.components.GlassmorphicCard
import com.vng.sajja.ui.theme.getContrastingTextColor

@Composable
fun ClockTypeSelectorCard(
    settings: WallpaperSettings,
    onClockTypeSelected: (ClockType) -> Unit,
    onClockSizeChanged: (Float) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Clock Type Picker
        GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
            Text("Clock Face Type", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ClockType.entries.forEach { type ->
                    val isSelected = settings.clockType == type
                    val contrastColor = getContrastingTextColor(MaterialTheme.colorScheme.primary)
                    FilterChip(
                        selected = isSelected,
                        onClick = { onClockTypeSelected(type) },
                        shape = androidx.compose.foundation.shape.CircleShape,
                        label = {
                            Text(
                                text = type.name,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
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
        }

        // Clock Size Card
        GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Clock Size", style = MaterialTheme.typography.bodyLarge)
                Text("${(settings.clockSize * 100).toInt()}%", style = MaterialTheme.typography.bodyMedium)
            }
            Slider(
                value = settings.clockSize,
                onValueChange = onClockSizeChanged,
                valueRange = 0.5f..1.0f,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                )
            )
        }
    }
}
