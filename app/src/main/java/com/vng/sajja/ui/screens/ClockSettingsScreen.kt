package com.vng.sajja.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vng.sajja.domain.model.ClockType
import com.vng.sajja.ui.components.*
import com.vng.sajja.ui.utils.toComposeColor
import com.vng.sajja.ui.viewmodel.SettingsViewModel

@Composable
fun ClockSettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
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
            // Clock Type Picker
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Text("Clock Face Type", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    ClockType.entries.forEach { type ->
                        FilterChip(
                            selected = settings.clockType == type,
                            onClick = { viewModel.updateSettings(settings.copy(clockType = type)) },
                            label = { Text(type.name, style = MaterialTheme.typography.bodySmall) },
                            modifier = Modifier.weight(1f)
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
                    onValueChange = { viewModel.updateSettings(settings.copy(clockSize = it)) },
                    valueRange = 0.5f..1.0f,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (settings.clockType != ClockType.DIGITAL) {
                // Clock Face Border & Numerals options
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Text("Clock Face Layout", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    ToggleOption(
                        label = "Show Border",
                        checked = settings.showBorder,
                        onCheckedChange = { viewModel.updateSettings(settings.copy(showBorder = it)) }
                    )

                    if (settings.showBorder) {
                        ColorOptionCard(
                            label = "Border Color",
                            color = android.graphics.Color.valueOf(settings.borderColor).toComposeColor(),
                            onClick = { showColorPicker = "border_color" }
                        )
                        SliderOption(
                            label = "Border Width",
                            value = settings.borderWidth,
                            onValueChange = { viewModel.updateSettings(settings.copy(borderWidth = it)) },
                            valueRange = 2f..30f,
                            unit = "px"
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    ToggleOption(
                        label = "Show Clock Numerals/Ticks",
                        checked = settings.showNumerals,
                        onCheckedChange = { viewModel.updateSettings(settings.copy(showNumerals = it)) }
                    )

                    if (settings.showNumerals) {
                        ColorOptionCard(
                            label = "Numeral/Tick Color",
                            color = android.graphics.Color.valueOf(settings.numeralColor).toComposeColor(),
                            onClick = { showColorPicker = "numeral_color" }
                        )
                        if (settings.clockType != ClockType.MINIMALIST) {
                            SliderOption(
                                label = "Numeral Size",
                                value = settings.numeralSize,
                                onValueChange = { viewModel.updateSettings(settings.copy(numeralSize = it)) },
                                valueRange = 20f..80f,
                                unit = "px"
                            )
                        }
                    }
                }

                // Hands options
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Text("Clock Hands", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    ColorOptionCard(
                        label = "Hour Hand Color",
                        color = android.graphics.Color.valueOf(settings.hourHandColor).toComposeColor(),
                        onClick = { showColorPicker = "hour_color" }
                    )
                    SliderOption(
                        label = "Hour Hand Width",
                        value = settings.hourHandWidth,
                        onValueChange = { viewModel.updateSettings(settings.copy(hourHandWidth = it)) },
                        valueRange = 4f..20f,
                        unit = "px"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ColorOptionCard(
                        label = "Minute Hand Color",
                        color = android.graphics.Color.valueOf(settings.minuteHandColor).toComposeColor(),
                        onClick = { showColorPicker = "minute_color" }
                    )
                    SliderOption(
                        label = "Minute Hand Width",
                        value = settings.minuteHandWidth,
                        onValueChange = { viewModel.updateSettings(settings.copy(minuteHandWidth = it)) },
                        valueRange = 3f..15f,
                        unit = "px"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ToggleOption(
                        label = "Show Second Hand",
                        checked = settings.showSecondHand,
                        onCheckedChange = { viewModel.updateSettings(settings.copy(showSecondHand = it)) }
                    )

                    if (settings.showSecondHand) {
                        ColorOptionCard(
                            label = "Second Hand Color",
                            color = android.graphics.Color.valueOf(settings.secondHandColor).toComposeColor(),
                            onClick = { showColorPicker = "second_color" }
                        )
                        SliderOption(
                            label = "Second Hand Width",
                            value = settings.secondHandWidth,
                            onValueChange = { viewModel.updateSettings(settings.copy(secondHandWidth = it)) },
                            valueRange = 2f..10f,
                            unit = "px"
                        )
                        ToggleOption(
                            label = "Smooth Sweeping Hand",
                            checked = settings.smoothSecondHand,
                            onCheckedChange = { viewModel.updateSettings(settings.copy(smoothSecondHand = it)) }
                        )
                    }
                }

                // Knob options
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Text("Center Knob", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    ColorOptionCard(
                        label = "Knob Color",
                        color = android.graphics.Color.valueOf(settings.centerKnobColor).toComposeColor(),
                        onClick = { showColorPicker = "knob_color" }
                    )
                    SliderOption(
                        label = "Knob Radius",
                        value = settings.centerKnobRadius,
                        onValueChange = { viewModel.updateSettings(settings.copy(centerKnobRadius = it)) },
                        valueRange = 5f..30f,
                        unit = "px"
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    ColorOptionCard(
                        label = "Center Ring Color",
                        color = android.graphics.Color.valueOf(settings.centerRingColor).toComposeColor(),
                        onClick = { showColorPicker = "ring_color" }
                    )
                    SliderOption(
                        label = "Center Ring Width",
                        value = settings.centerRingWidth,
                        onValueChange = { viewModel.updateSettings(settings.copy(centerRingWidth = it)) },
                        valueRange = 1f..10f,
                        unit = "px"
                    )
                }
            } else {
                // Digital clock numeral configuration
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Text("Digital Numerals", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    ColorOptionCard(
                        label = "Time Color",
                        color = android.graphics.Color.valueOf(settings.numeralColor).toComposeColor(),
                        onClick = { showColorPicker = "numeral_color" }
                    )
                    SliderOption(
                        label = "Time Font Size",
                        value = settings.numeralSize,
                        onValueChange = { viewModel.updateSettings(settings.copy(numeralSize = it)) },
                        valueRange = 20f..80f,
                        unit = "sp"
                    )
                }
            }

            // Date & Day Card
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Text("Date & Day Options", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                ToggleOption(
                    label = "Show Date",
                    checked = settings.showDate,
                    onCheckedChange = { viewModel.updateSettings(settings.copy(showDate = it)) }
                )

                if (settings.showDate) {
                    ColorOptionCard(
                        label = "Date Color",
                        color = android.graphics.Color.valueOf(settings.dateColor).toComposeColor(),
                        onClick = { showColorPicker = "date_color" }
                    )
                    SliderOption(
                        label = "Date Size",
                        value = settings.dateSize,
                        onValueChange = { viewModel.updateSettings(settings.copy(dateSize = it)) },
                        valueRange = 20f..60f,
                        unit = "px"
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                ToggleOption(
                    label = "Show Day",
                    checked = settings.showDay,
                    onCheckedChange = { viewModel.updateSettings(settings.copy(showDay = it)) }
                )

                if (settings.showDay) {
                    ColorOptionCard(
                        label = "Day Color",
                        color = android.graphics.Color.valueOf(settings.dayColor).toComposeColor(),
                        onClick = { showColorPicker = "day_color" }
                    )
                    SliderOption(
                        label = "Day Size",
                        value = settings.daySize,
                        onValueChange = { viewModel.updateSettings(settings.copy(daySize = it)) },
                        valueRange = 20f..60f,
                        unit = "px"
                    )
                }
            }
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
