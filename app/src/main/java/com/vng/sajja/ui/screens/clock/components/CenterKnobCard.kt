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
import com.vng.sajja.ui.utils.toComposeColor

@Composable
fun CenterKnobCard(
    settings: WallpaperSettings,
    onSettingsChange: (WallpaperSettings) -> Unit,
    onRequestColorPicker: (String) -> Unit
) {
    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
        Text("Center Knob", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        ColorOptionCard(
            label = "Knob Color",
            color = android.graphics.Color.valueOf(settings.centerKnobColor).toComposeColor(),
            onClick = { onRequestColorPicker("knob_color") }
        )
        SliderOption(
            label = "Knob Radius",
            value = settings.centerKnobRadius,
            onValueChange = { onSettingsChange(settings.copy(centerKnobRadius = it)) },
            valueRange = 5f..30f,
            unit = "px"
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        ColorOptionCard(
            label = "Center Ring Color",
            color = android.graphics.Color.valueOf(settings.centerRingColor).toComposeColor(),
            onClick = { onRequestColorPicker("ring_color") }
        )
        SliderOption(
            label = "Center Ring Width",
            value = settings.centerRingWidth,
            onValueChange = { onSettingsChange(settings.copy(centerRingWidth = it)) },
            valueRange = 1f..10f,
            unit = "px"
        )
    }
}
