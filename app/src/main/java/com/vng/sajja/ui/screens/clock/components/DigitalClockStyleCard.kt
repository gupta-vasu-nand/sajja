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
import com.vng.sajja.domain.model.ClockPosition
import com.vng.sajja.domain.model.DigitalAnimType
import com.vng.sajja.domain.model.WallpaperSettings
import com.vng.sajja.ui.components.ColorOptionCard
import com.vng.sajja.ui.components.GlassmorphicBatteryWarningDialog
import com.vng.sajja.ui.components.GlassmorphicCard
import com.vng.sajja.ui.components.SliderOption
import com.vng.sajja.ui.components.ToggleOption
import com.vng.sajja.ui.theme.getContrastingTextColor
import com.vng.sajja.ui.utils.toComposeColor

@Composable
fun DigitalClockStyleCard(
    settings: WallpaperSettings,
    onSettingsChange: (WallpaperSettings) -> Unit,
    onRequestColorPicker: (String) -> Unit
) {
    var pendingAnimType by remember { mutableStateOf<DigitalAnimType?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Digital clock numeral configuration
        GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
            Text("Digital Clock Style & Font", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))

            ColorOptionCard(
                label = "Time Color",
                color = android.graphics.Color.valueOf(settings.numeralColor).toComposeColor(),
                onClick = { onRequestColorPicker("numeral_color") }
            )
            SliderOption(
                label = "Time Font Size Scale",
                value = settings.numeralSize,
                onValueChange = { onSettingsChange(settings.copy(numeralSize = it)) },
                valueRange = 20f..80f,
                unit = "sp"
            )

            Spacer(modifier = Modifier.height(8.dp))

            val contrastColor = getContrastingTextColor(MaterialTheme.colorScheme.primary)
            val chipColors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = contrastColor,
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text("Clock Font Style", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Monospace", "Sans Serif", "Serif", "Default").forEachIndexed { index, fontName ->
                    val isSelected = settings.digitalClockFontIndex == index
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSettingsChange(settings.copy(digitalClockFontIndex = index)) },
                        shape = CircleShape,
                        label = {
                            Text(
                                text = fontName,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) contrastColor else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        colors = chipColors
                    )
                }
            }
        }

        GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
            Text("Display Formats", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))

            ToggleOption(
                label = "Show Seconds",
                checked = settings.showSecondHand,
                onCheckedChange = { onSettingsChange(settings.copy(showSecondHand = it)) }
            )
            ToggleOption(
                label = "Use 24-Hour Format",
                checked = settings.use24HourFormat,
                onCheckedChange = { onSettingsChange(settings.copy(use24HourFormat = it)) }
            )
            ToggleOption(
                label = "Show Bottom Info (Date & Day)",
                checked = settings.showBottomText,
                onCheckedChange = { onSettingsChange(settings.copy(showBottomText = it)) }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            Text("Number Switching Animation", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

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
                listOf(DigitalAnimType.NONE, DigitalAnimType.SLIDE, DigitalAnimType.FADE, DigitalAnimType.BOUNCE).forEach { anim ->
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
                SliderOption(
                    label = "Animation Duration",
                    value = settings.digitalAnimDuration.toFloat(),
                    onValueChange = { onSettingsChange(settings.copy(digitalAnimDuration = it.toLong())) },
                    valueRange = 100f..800f,
                    unit = "ms"
                )
            }
        }

        GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
            Text("Position & Fine Tuning", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))

            Text("Default Alignment", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

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
                ClockPosition.entries.forEach { pos ->
                    val isSelected = settings.clockPosition == pos
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSettingsChange(settings.copy(clockPosition = pos)) },
                        shape = CircleShape,
                        label = {
                            Text(
                                text = pos.name.replace("_", " "),
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

            SliderOption(
                label = "Horizontal Fine Tuning (X Offset)",
                value = settings.clockOffsetX,
                onValueChange = { onSettingsChange(settings.copy(clockOffsetX = it)) },
                valueRange = -300f..300f,
                unit = "px"
            )

            SliderOption(
                label = "Vertical Fine Tuning (Y Offset)",
                value = settings.clockOffsetY,
                onValueChange = { onSettingsChange(settings.copy(clockOffsetY = it)) },
                valueRange = -600f..600f,
                unit = "px"
            )
        }

        GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
            Text("Glass Panel Backing", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))

            ToggleOption(
                label = "Enable Clock Glass Plate",
                checked = settings.showDigitalClockPlate,
                onCheckedChange = { onSettingsChange(settings.copy(showDigitalClockPlate = it)) }
            )

            if (settings.showDigitalClockPlate) {
                SliderOption(
                    label = "Plate Opacity",
                    value = settings.digitalClockPlateOpacity,
                    onValueChange = { onSettingsChange(settings.copy(digitalClockPlateOpacity = it)) },
                    valueRange = 0.05f..0.85f,
                    unit = "%"
                )
            }
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
