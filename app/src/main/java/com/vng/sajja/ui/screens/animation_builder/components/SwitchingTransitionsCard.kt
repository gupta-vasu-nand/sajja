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
import com.vng.sajja.domain.model.DigitalAnimType
import com.vng.sajja.domain.model.WallpaperSettings
import com.vng.sajja.ui.components.GlassmorphicBatteryWarningDialog
import com.vng.sajja.ui.components.GlassmorphicCard
import com.vng.sajja.ui.components.SliderOption
import com.vng.sajja.ui.theme.getContrastingTextColor

@Composable
fun SwitchingTransitionsCard(
    settings: WallpaperSettings,
    onSettingsChange: (WallpaperSettings) -> Unit
) {
    var pendingAnimType by remember { mutableStateOf<DigitalAnimType?>(null) }

    val contrastColor = getContrastingTextColor(MaterialTheme.colorScheme.primary)
    val chipColors = FilterChipDefaults.filterChipColors(
        selectedContainerColor = MaterialTheme.colorScheme.primary,
        selectedLabelColor = contrastColor,
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
        Text("Switching Transitions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        Text("Select Switching Effect", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DigitalAnimType.entries.forEach { anim ->
                val isSelected = settings.digitalAnimType == anim
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        if (anim != DigitalAnimType.NONE && anim != settings.digitalAnimType) {
                            pendingAnimType = anim
                        } else {
                            onSettingsChange(settings.copy(digitalAnimType = anim))
                        }
                    },
                    shape = CircleShape,
                    label = {
                        Text(
                            text = anim.name,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) contrastColor else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    colors = chipColors
                )
            }
        }

        if (settings.digitalAnimType != DigitalAnimType.NONE) {
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            SliderOption(
                label = "Animation Duration",
                value = settings.digitalAnimDuration.toFloat(),
                onValueChange = { onSettingsChange(settings.copy(digitalAnimDuration = it.toLong())) },
                valueRange = 100f..800f,
                unit = "ms"
            )
        }
    }

    pendingAnimType?.let { anim ->
        GlassmorphicBatteryWarningDialog(
            onConfirm = {
                onSettingsChange(settings.copy(digitalAnimType = anim))
                pendingAnimType = null
            },
            onDismiss = { pendingAnimType = null }
        )
    }
}
