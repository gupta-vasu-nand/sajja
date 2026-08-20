package com.vng.sajja.ui.screens.clock

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.vng.sajja.domain.model.ClockType
import com.vng.sajja.ui.components.AdvancedColorPickerDialog
import com.vng.sajja.ui.screens.clock.components.*
import com.vng.sajja.ui.utils.toComposeColor
import com.vng.sajja.ui.viewmodel.SettingsViewModel

@Composable
fun ClockSettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier,
    isVisible: Boolean = true
) {
    val settings by viewModel.settings.collectAsState()
    var showColorPicker by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Clock Type Picker & Size Card
        ClockTypeSelectorCard(
            settings = settings,
            onClockTypeSelected = { viewModel.updateSettings(settings.copy(clockType = it)) },
            onClockSizeChanged = { viewModel.updateSettings(settings.copy(clockSize = it)) }
        )

        if (settings.clockType != ClockType.DIGITAL) {
            // Analog Clock Face & Numerals Card
            ClockFaceLayoutCard(
                settings = settings,
                onSettingsChange = { viewModel.updateSettings(it) },
                onRequestColorPicker = { showColorPicker = it }
            )

            // Analog Clock Hands Card
            ClockHandStyleCard(
                settings = settings,
                onSettingsChange = { viewModel.updateSettings(it) },
                onRequestColorPicker = { showColorPicker = it }
            )

            // Center Knob Card
            CenterKnobCard(
                settings = settings,
                onSettingsChange = { viewModel.updateSettings(it) },
                onRequestColorPicker = { showColorPicker = it }
            )
        } else {
            // Digital Clock Style, Font, Animations & Positions
            DigitalClockStyleCard(
                settings = settings,
                onSettingsChange = { viewModel.updateSettings(it) },
                onRequestColorPicker = { showColorPicker = it }
            )
        }

        // Date & Day Card
        DateAndDayCard(
            settings = settings,
            onSettingsChange = { viewModel.updateSettings(it) },
            onRequestColorPicker = { showColorPicker = it }
        )

        // Particle Effects Card
        ParticleEffectsCard(
            settings = settings,
            onSettingsChange = { viewModel.updateSettings(it) }
        )

        Spacer(modifier = Modifier.height(130.dp))
    }

    showColorPicker?.let { target ->
        AdvancedColorPickerDialog(
            initial = when (target) {
                "border_color" -> android.graphics.Color.valueOf(settings.borderColor).toComposeColor()
                "numeral_color" -> android.graphics.Color.valueOf(settings.numeralColor).toComposeColor()
                "hour_color" -> android.graphics.Color.valueOf(settings.hourHandColor).toComposeColor()
                "minute_color" -> android.graphics.Color.valueOf(settings.minuteHandColor).toComposeColor()
                "second_color" -> android.graphics.Color.valueOf(settings.secondHandColor).toComposeColor()
                "knob_color" -> android.graphics.Color.valueOf(settings.centerKnobColor).toComposeColor()
                "ring_color" -> android.graphics.Color.valueOf(settings.centerRingColor).toComposeColor()
                "date_color" -> android.graphics.Color.valueOf(settings.dateColor).toComposeColor()
                "day_color" -> android.graphics.Color.valueOf(settings.dayColor).toComposeColor()
                else -> Color.White
            },
            onDismiss = { showColorPicker = null }
        ) { color ->
            when (target) {
                "border_color" -> viewModel.updateSettings(settings.copy(borderColor = color.toArgb()))
                "numeral_color" -> viewModel.updateSettings(settings.copy(numeralColor = color.toArgb()))
                "hour_color" -> viewModel.updateSettings(settings.copy(hourHandColor = color.toArgb()))
                "minute_color" -> viewModel.updateSettings(settings.copy(minuteHandColor = color.toArgb()))
                "second_color" -> viewModel.updateSettings(settings.copy(secondHandColor = color.toArgb()))
                "knob_color" -> viewModel.updateSettings(settings.copy(centerKnobColor = color.toArgb()))
                "ring_color" -> viewModel.updateSettings(settings.copy(centerRingColor = color.toArgb()))
                "date_color" -> viewModel.updateSettings(settings.copy(dateColor = color.toArgb()))
                "day_color" -> viewModel.updateSettings(settings.copy(dayColor = color.toArgb()))
            }
            showColorPicker = null
        }
    }
}
