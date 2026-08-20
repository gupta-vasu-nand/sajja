package com.vng.sajja.ui.screens.clock.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vng.sajja.domain.model.WallpaperSettings
import com.vng.sajja.ui.components.ColorOptionCard
import com.vng.sajja.ui.components.GlassmorphicCard
import com.vng.sajja.ui.components.SliderOption
import com.vng.sajja.ui.components.ToggleOption
import com.vng.sajja.ui.utils.toComposeColor

@Composable
fun ClockHandStyleCard(
    settings: WallpaperSettings,
    onSettingsChange: (WallpaperSettings) -> Unit,
    onRequestColorPicker: (String) -> Unit
) {
    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
        Text("Clock Hands", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        ColorOptionCard(
            label = "Hour Hand Color",
            color = android.graphics.Color.valueOf(settings.hourHandColor).toComposeColor(),
            onClick = { onRequestColorPicker("hour_color") }
        )
        SliderOption(
            label = "Hour Hand Width",
            value = settings.hourHandWidth,
            onValueChange = { onSettingsChange(settings.copy(hourHandWidth = it)) },
            valueRange = 4f..20f,
            unit = "px"
        )

        Spacer(modifier = Modifier.height(8.dp))

        ColorOptionCard(
            label = "Minute Hand Color",
            color = android.graphics.Color.valueOf(settings.minuteHandColor).toComposeColor(),
            onClick = { onRequestColorPicker("minute_color") }
        )
        SliderOption(
            label = "Minute Hand Width",
            value = settings.minuteHandWidth,
            onValueChange = { onSettingsChange(settings.copy(minuteHandWidth = it)) },
            valueRange = 3f..15f,
            unit = "px"
        )

        Spacer(modifier = Modifier.height(8.dp))

        ToggleOption(
            label = "Show Second Hand",
            checked = settings.showSecondHand,
            onCheckedChange = { onSettingsChange(settings.copy(showSecondHand = it)) }
        )

        if (settings.showSecondHand) {
            ColorOptionCard(
                label = "Second Hand Color",
                color = android.graphics.Color.valueOf(settings.secondHandColor).toComposeColor(),
                onClick = { onRequestColorPicker("second_color") }
            )
            SliderOption(
                label = "Second Hand Width",
                value = settings.secondHandWidth,
                onValueChange = { onSettingsChange(settings.copy(secondHandWidth = it)) },
                valueRange = 2f..10f,
                unit = "px"
            )
            ToggleOption(
                label = "Smooth Sweeping Hand",
                checked = settings.smoothSecondHand,
                onCheckedChange = { onSettingsChange(settings.copy(smoothSecondHand = it)) }
            )
        }
    }
}
