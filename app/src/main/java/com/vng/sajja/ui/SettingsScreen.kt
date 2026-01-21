package com.vng.sajja.ui

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.vng.sajja.settings.BackgroundType
import com.vng.sajja.settings.ThemePresets
import com.vng.sajja.settings.WallpaperSettings
import com.vng.sajja.settings.WallpaperSettingsRepository

@Composable
fun SettingsScreen(repo: WallpaperSettingsRepository, modifier: Modifier = Modifier) {
    var settings by remember { mutableStateOf(repo.load()) }
    var selectedTab by remember { mutableStateOf(0) }
    var showImportDialog by remember { mutableStateOf(false) }
    var jsonInput by remember { mutableStateOf("") }

    LaunchedEffect(settings) {
        repo.save(settings)
    }

    Column(modifier = modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            listOf("Background", "Clock", "Collage", "Theme", "Tools").forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(
                        title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    ) }
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            when (selectedTab) {
                0 -> BackgroundTab(settings, onUpdateSettings = { settings = it })
                1 -> ClockTab(settings, onUpdateSettings = { settings = it })
                2 -> CollageTab(settings, onUpdateSettings = { settings = it })
                3 -> ThemeTab(settings, onUpdateSettings = { settings = it })
                4 -> ToolsTab(
                    settings = settings,
                    onExport = {
                        val gson = Gson()
                        val json = gson.toJson(settings)
                        json
                    },
                    onImport = { json ->
                        try {
                            val gson = Gson()
                            val imported = gson.fromJson(json, WallpaperSettings::class.java)
                            settings = imported
                        } catch (_: Exception) {
                            // Handle error
                        }
                    }
                )
            }
        }
    }
}
@Composable
fun BackgroundTab(settings: WallpaperSettings, onUpdateSettings: (WallpaperSettings) -> Unit) {
    var showColorPicker by remember { mutableStateOf<String?>(null) }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Background Type", style = MaterialTheme.typography.titleMedium)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BackgroundType.entries.forEach { type ->
                        FilterChip(
                            selected = settings.backgroundType == type,
                            onClick = { onUpdateSettings(settings.copy(backgroundType = type)) },
                            label = { Text(type.name) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                when (settings.backgroundType) {
                    BackgroundType.SOLID -> {
                        ColorOptionCard(
                            label = "Background Color",
                            color = android.graphics.Color.valueOf(settings.backgroundColor).toComposeColor(),
                            onClick = { showColorPicker = "bg_color" }
                        )
                    }
                    BackgroundType.GRADIENT -> {
                        ColorOptionCard(
                            label = "Start Color",
                            color = android.graphics.Color.valueOf(settings.gradientStartColor).toComposeColor(),
                            onClick = { showColorPicker = "grad_start" }
                        )
                        ColorOptionCard(
                            label = "End Color",
                            color = android.graphics.Color.valueOf(settings.gradientEndColor).toComposeColor(),
                            onClick = { showColorPicker = "grad_end" }
                        )
                    }
                    BackgroundType.COLLAGE -> {
                        Text(
                            "Use the Collage tab to manage your image collage",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    showColorPicker?.let { target ->
        AdvancedColorPickerDialog(
            initial = when (target) {
                "bg_color" -> android.graphics.Color.valueOf(settings.backgroundColor).toComposeColor()
                "grad_start" -> android.graphics.Color.valueOf(settings.gradientStartColor).toComposeColor()
                "grad_end" -> android.graphics.Color.valueOf(settings.gradientEndColor).toComposeColor()
                else -> Color.White
            },
            onDismiss = { showColorPicker = null }
        ) { color ->
            when (target) {
                "bg_color" -> onUpdateSettings(settings.copy(backgroundColor = color.toArgb()))
                "grad_start" -> onUpdateSettings(settings.copy(gradientStartColor = color.toArgb()))
                "grad_end" -> onUpdateSettings(settings.copy(gradientEndColor = color.toArgb()))
            }
            showColorPicker = null
        }
    }
}

@Composable
fun ClockTab(settings: WallpaperSettings, onUpdateSettings: (WallpaperSettings) -> Unit) {
    var showColorPicker by remember { mutableStateOf<String?>(null) }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Clock Size")
                    Text("${(settings.clockSize * 100).toInt()}%")
                }
                Slider(
                    value = settings.clockSize,
                    onValueChange = { onUpdateSettings(settings.copy(clockSize = it)) },
                    valueRange = 0.5f..1.0f,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Clock Face", style = MaterialTheme.typography.titleMedium)

                ToggleOption(
                    label = "Show Border",
                    checked = settings.showBorder,
                    onCheckedChange = { onUpdateSettings(settings.copy(showBorder = it)) }
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
                        onValueChange = { onUpdateSettings(settings.copy(borderWidth = it)) },
                        valueRange = 2f..30f,
                        unit = "px"
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                ToggleOption(
                    label = "Show Roman Numerals",
                    checked = settings.showNumerals,
                    onCheckedChange = { onUpdateSettings(settings.copy(showNumerals = it)) }
                )

                if (settings.showNumerals) {
                    ColorOptionCard(
                        label = "Numeral Color",
                        color = android.graphics.Color.valueOf(settings.numeralColor).toComposeColor(),
                        onClick = { showColorPicker = "numeral_color" }
                    )
                    SliderOption(
                        label = "Numeral Size",
                        value = settings.numeralSize,
                        onValueChange = { onUpdateSettings(settings.copy(numeralSize = it)) },
                        valueRange = 20f..80f,
                        unit = "px"
                    )
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Clock Hands", style = MaterialTheme.typography.titleMedium)

                ColorOptionCard(
                    label = "Hour Hand Color",
                    color = android.graphics.Color.valueOf(settings.hourHandColor).toComposeColor(),
                    onClick = { showColorPicker = "hour_color" }
                )
                SliderOption(
                    label = "Hour Hand Width",
                    value = settings.hourHandWidth,
                    onValueChange = { onUpdateSettings(settings.copy(hourHandWidth = it)) },
                    valueRange = 4f..20f,
                    unit = "px"
                )

                ColorOptionCard(
                    label = "Minute Hand Color",
                    color = android.graphics.Color.valueOf(settings.minuteHandColor).toComposeColor(),
                    onClick = { showColorPicker = "minute_color" }
                )
                SliderOption(
                    label = "Minute Hand Width",
                    value = settings.minuteHandWidth,
                    onValueChange = { onUpdateSettings(settings.copy(minuteHandWidth = it)) },
                    valueRange = 3f..15f,
                    unit = "px"
                )

                ToggleOption(
                    label = "Show Second Hand",
                    checked = settings.showSecondHand,
                    onCheckedChange = { onUpdateSettings(settings.copy(showSecondHand = it)) }
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
                        onValueChange = { onUpdateSettings(settings.copy(secondHandWidth = it)) },
                        valueRange = 2f..10f,
                        unit = "px"
                    )
                    ToggleOption(
                        label = "Smooth Second Hand",
                        checked = settings.smoothSecondHand,
                        onCheckedChange = { onUpdateSettings(settings.copy(smoothSecondHand = it)) }
                    )
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Center Knob", style = MaterialTheme.typography.titleMedium)

                ColorOptionCard(
                    label = "Knob Color",
                    color = android.graphics.Color.valueOf(settings.centerKnobColor).toComposeColor(),
                    onClick = { showColorPicker = "knob_color" }
                )
                SliderOption(
                    label = "Knob Radius",
                    value = settings.centerKnobRadius,
                    onValueChange = { onUpdateSettings(settings.copy(centerKnobRadius = it)) },
                    valueRange = 5f..30f,
                    unit = "px"
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                ColorOptionCard(
                    label = "Ring Color",
                    color = android.graphics.Color.valueOf(settings.centerRingColor).toComposeColor(),
                    onClick = { showColorPicker = "ring_color" }
                )
                SliderOption(
                    label = "Ring Width",
                    value = settings.centerRingWidth,
                    onValueChange = { onUpdateSettings(settings.copy(centerRingWidth = it)) },
                    valueRange = 1f..10f,
                    unit = "px"
                )
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Date & Day", style = MaterialTheme.typography.titleMedium)

                ToggleOption(
                    label = "Show Date",
                    checked = settings.showDate,
                    onCheckedChange = { onUpdateSettings(settings.copy(showDate = it)) }
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
                        onValueChange = { onUpdateSettings(settings.copy(dateSize = it)) },
                        valueRange = 20f..60f,
                        unit = "px"
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                ToggleOption(
                    label = "Show Day",
                    checked = settings.showDay,
                    onCheckedChange = { onUpdateSettings(settings.copy(showDay = it)) }
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
                        onValueChange = { onUpdateSettings(settings.copy(daySize = it)) },
                        valueRange = 20f..60f,
                        unit = "px"
                    )
                }
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
                "border_color" -> onUpdateSettings(settings.copy(borderColor = color.toArgb()))
                "numeral_color" -> onUpdateSettings(settings.copy(numeralColor = color.toArgb()))
                "hour_color" -> onUpdateSettings(settings.copy(hourHandColor = color.toArgb()))
                "minute_color" -> onUpdateSettings(settings.copy(minuteHandColor = color.toArgb()))
                "second_color" -> onUpdateSettings(settings.copy(secondHandColor = color.toArgb()))
                "knob_color" -> onUpdateSettings(settings.copy(centerKnobColor = color.toArgb()))
                "ring_color" -> onUpdateSettings(settings.copy(centerRingColor = color.toArgb()))
                "date_color" -> onUpdateSettings(settings.copy(dateColor = color.toArgb()))
                "day_color" -> onUpdateSettings(settings.copy(dayColor = color.toArgb()))
            }
            showColorPicker = null
        }
    }
}

@Composable
fun CollageTab(settings: WallpaperSettings, onUpdateSettings: (WallpaperSettings) -> Unit) {
    CollageScreen(settings, onUpdateSettings)
}

@Composable
fun ThemeTab(settings: WallpaperSettings, onUpdateSettings: (WallpaperSettings) -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Color Themes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PresetButton(
                        label = "Classic",
                        onClick = { onUpdateSettings(ThemePresets.Classic) },
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                    PresetButton(
                        label = "AMOLED",
                        onClick = { onUpdateSettings(ThemePresets.AMOLED) },
                        color = Color.Black,
                        textColor = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PresetButton(
                        label = "Sunset",
                        onClick = { onUpdateSettings(ThemePresets.Sunset) },
                        color = Color(0xFFDD2476),
                        textColor = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    PresetButton(
                        label = "Ocean",
                        onClick = { onUpdateSettings(ThemePresets.Ocean) },
                        color = androidx.compose.ui.graphics.Color(0xFF0083B0),
                        textColor = androidx.compose.ui.graphics.Color.White,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Collage Templates", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    "Start with a pre-designed collage template",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PresetButton(
                        label = "Memories",
                        onClick = { onUpdateSettings(ThemePresets.MemoriesCollage) },
                        color = Color(0xFF1A1A2E),
                        textColor = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    PresetButton(
                        label = "Travel",
                        onClick = { onUpdateSettings(ThemePresets.TravelCollage) },
                        color = Color(0xFF0F3460),
                        textColor = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PresetButton(
                        label = "Minimal",
                        onClick = { onUpdateSettings(ThemePresets.MinimalCollage) },
                        color = Color.Black,
                        textColor = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    FilledTonalButton(
                        onClick = { onUpdateSettings(WallpaperSettings()) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reset All")
                    }
                }
            }
        }
    }
}
@Composable
fun ColorOptionCard(label: String, color: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(
                        2.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        CircleShape
                    )
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Pick color",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun ToggleOption(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}

@Composable
fun SliderOption(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    unit: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "${value.toInt()}$unit",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun PresetButton(
    label: String,
    onClick: () -> Unit,
    color: Color,
    textColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    modifier: Modifier = Modifier
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = color,
            contentColor = textColor
        )
    ) {
        Text(label, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ToolsTab(
    settings: WallpaperSettings,
    onExport: () -> String,
    onImport: (String) -> Unit
) {
    val context = LocalContext.current
    var showImportDialog by remember { mutableStateOf(false) }
    var jsonInput by remember { mutableStateOf("") }

    val filePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            context.contentResolver.openInputStream(it)?.use { stream ->
                jsonInput = stream.bufferedReader().use { reader -> reader.readText() }
                showImportDialog = true
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Import & Export",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FilledTonalButton(
                        onClick = {
                            val json = onExport()
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, json)
                                putExtra(Intent.EXTRA_SUBJECT, "Roman Clock Theme")
                            }
                            ContextCompat.startActivity(
                                context,
                                Intent.createChooser(shareIntent, "Export Theme"),
                                null
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Export Theme")
                    }

                    FilledTonalButton(
                        onClick = { filePicker.launch("application/json") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Import from File")
                    }
                }

                OutlinedTextField(
                    value = jsonInput,
                    onValueChange = { jsonInput = it },
                    label = { Text("Or paste JSON here") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 5
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            if (jsonInput.isNotEmpty()) {
                                onImport(jsonInput)
                                jsonInput = ""
                            }
                        },
                        enabled = jsonInput.isNotEmpty()
                    ) {
                        Text("Import from Text")
                    }
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Collage Tools",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilledTonalButton(
                        onClick = {
                            // Auto arrange images in grid
                            val images = settings.collageImages.mapIndexed { index, image ->
                                val row = index / 3
                                val col = index % 3
                                image.copy(
                                    x = 0.05f + col * 0.3f,
                                    y = 0.05f + row * 0.3f,
                                    width = 0.25f,
                                    height = 0.25f
                                )
                            }
                            onImport(Gson().toJson(settings.copy(collageImages = images)))
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Auto Arrange")
                    }

                    FilledTonalButton(
                        onClick = {
                            // Clear all images
                            onImport(Gson().toJson(settings.copy(collageImages = emptyList())))
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Clear All")
                    }
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Quick Actions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilledTonalButton(
                        onClick = {
                            // Save current as preset
                            val preset = Gson().toJson(settings)
                            // Save to shared preferences
                            val prefs = context.getSharedPreferences("presets", Context.MODE_PRIVATE)
                            prefs.edit().putString("last_preset", preset).apply()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save Preset")
                    }

                    FilledTonalButton(
                        onClick = {
                            // Load last preset
                            val prefs = context.getSharedPreferences("presets", Context.MODE_PRIVATE)
                            val preset = prefs.getString("last_preset", null)
                            preset?.let { onImport(it) }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Load Last")
                    }
                }
            }
        }
    }

    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text("Import Theme") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Do you want to import this theme?")
                    OutlinedTextField(
                        value = jsonInput,
                        onValueChange = { jsonInput = it },
                        label = { Text("JSON Preview") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        readOnly = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onImport(jsonInput)
                        jsonInput = ""
                        showImportDialog = false
                    }
                ) {
                    Text("Import")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        jsonInput = ""
                        showImportDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
fun android.graphics.Color.toComposeColor(): Color {
    return Color(
        red(), green(), blue(), alpha()
    )
}