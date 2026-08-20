package com.vng.sajja.ui.screens.clock.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vng.sajja.domain.model.ClockType
import com.vng.sajja.domain.model.WallpaperSettings
import com.vng.sajja.ui.components.ColorOptionCard
import com.vng.sajja.ui.components.GlassmorphicCard
import com.vng.sajja.ui.components.SliderOption
import com.vng.sajja.ui.components.ToggleOption
import com.vng.sajja.ui.utils.toComposeColor

@Composable
fun ClockFaceLayoutCard(
    settings: WallpaperSettings,
    onSettingsChange: (WallpaperSettings) -> Unit,
    onRequestColorPicker: (String) -> Unit
) {
    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
        Text("Clock Face Layout", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        ToggleOption(
            label = "Show Border",
            checked = settings.showBorder,
            onCheckedChange = { onSettingsChange(settings.copy(showBorder = it)) }
        )

        if (settings.showBorder) {
            ColorOptionCard(
                label = "Border Color",
                color = android.graphics.Color.valueOf(settings.borderColor).toComposeColor(),
                onClick = { onRequestColorPicker("border_color") }
            )
            SliderOption(
                label = "Border Width",
                value = settings.borderWidth,
                onValueChange = { onSettingsChange(settings.copy(borderWidth = it)) },
                valueRange = 2f..30f,
                unit = "px"
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        ToggleOption(
            label = "Show Clock Numerals/Ticks",
            checked = settings.showNumerals,
            onCheckedChange = { onSettingsChange(settings.copy(showNumerals = it)) }
        )

        if (settings.showNumerals) {
            ColorOptionCard(
                label = "Numeral/Tick Color",
                color = android.graphics.Color.valueOf(settings.numeralColor).toComposeColor(),
                onClick = { onRequestColorPicker("numeral_color") }
            )
            if (settings.clockType != ClockType.MINIMALIST) {
                SliderOption(
                    label = "Numeral Size",
                    value = settings.numeralSize,
                    onValueChange = { onSettingsChange(settings.copy(numeralSize = it)) },
                    valueRange = 20f..80f,
                    unit = "px"
                )
            }
        }
    }
}
