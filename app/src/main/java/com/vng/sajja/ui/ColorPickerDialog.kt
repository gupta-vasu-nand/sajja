// AdvancedColorPickerDialog.kt
package com.vng.sajja.ui

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedColorPickerDialog(
    initial: Color,
    onDismiss: () -> Unit,
    onPick: (Color) -> Unit
) {
    var r by remember { mutableStateOf((initial.red * 255).roundToInt()) }
    var g by remember { mutableStateOf((initial.green * 255).roundToInt()) }
    var b by remember { mutableStateOf((initial.blue * 255).roundToInt()) }

    val selectedColor = Color(r, g, b)
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequesterR = remember { FocusRequester() }
    val focusRequesterG = remember { FocusRequester() }
    val focusRequesterB = remember { FocusRequester() }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(horizontal = 16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ColorLens,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "Color Picker",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Color preview with hex code
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(selectedColor)
                        .border(
                            2.dp,
                            MaterialTheme.colorScheme.outline,
                            RoundedCornerShape(16.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Selected Color",
                            color = if (selectedColor.luminance() > 0.5) Color.Black else Color.White,
                            fontWeight = FontWeight.Medium,
                            fontSize = 18.sp
                        )
                        Column {
                            Text(
                                text = "HEX: #${r.toString(16).padStart(2, '0').uppercase()}${g.toString(16).padStart(2, '0').uppercase()}${b.toString(16).padStart(2, '0').uppercase()}",
                                color = if (selectedColor.luminance() > 0.5) Color.Black.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.8f),
                                fontSize = 14.sp
                            )
                            Text(
                                text = "RGB: $r, $g, $b",
                                color = if (selectedColor.luminance() > 0.5) Color.Black.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.8f),
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // Color sliders with numeric input
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    ColorSliderWithInput(
                        label = "Red",
                        value = r,
                        onValueChange = { r = it },
                        color = Color.Red,
                        focusRequester = focusRequesterR,
                        nextFocusRequester = focusRequesterG
                    )

                    ColorSliderWithInput(
                        label = "Green",
                        value = g,
                        onValueChange = { g = it },
                        color = Color.Green,
                        focusRequester = focusRequesterG,
                        nextFocusRequester = focusRequesterB
                    )

                    ColorSliderWithInput(
                        label = "Blue",
                        value = b,
                        onValueChange = { b = it },
                        color = Color.Blue,
                        focusRequester = focusRequesterB,
                        nextFocusRequester = null
                    )
                }

                // Color palette for quick picks
                QuickColorPalette { color ->
                    r = (color.red * 255).roundToInt()
                    g = (color.green * 255).roundToInt()
                    b = (color.blue * 255).roundToInt()
                    keyboardController?.hide()
                }

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            keyboardController?.hide()
                            onDismiss()
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            keyboardController?.hide()
                            onPick(selectedColor)
                            onDismiss()
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Apply Color")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColorSliderWithInput(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    color: Color,
    focusRequester: FocusRequester,
    nextFocusRequester: FocusRequester?
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                )
                Text(
                    text = label,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            }

            OutlinedTextField(
                value = value.toString(),
                onValueChange = {
                    val newValue = it.toIntOrNull() ?: 0
                    if (newValue in 0..255) onValueChange(newValue)
                },
                modifier = Modifier
                    .width(80.dp)
                    .focusRequester(focusRequester),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = if (nextFocusRequester != null) ImeAction.Next else ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onNext = { nextFocusRequester?.requestFocus() },
                    onDone = { keyboardController?.hide() }
                ),
                shape = RoundedCornerShape(8.dp)
            )
        }

        Slider(
            value = value / 255f,
            onValueChange = { onValueChange((it * 255).roundToInt()) },
            valueRange = 0f..1f,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = color,
                activeTrackColor = color,
                inactiveTrackColor = color.copy(alpha = 0.3f)
            )
        )
    }
}


@Composable
private fun QuickColorPalette(onColorSelected: (Color) -> Unit) {
    val quickColors = listOf(
        Pair(Color.Red, "Red"),
        Pair(Color.Green, "Green"),
        Pair(Color.Blue, "Blue"),
        Pair(Color.White, "White"),
        Pair(Color.Black, "Black"),
        Pair(Color.Yellow, "Yellow"),
        Pair(Color.Magenta, "Magenta"),
        Pair(Color.Cyan, "Cyan"),
        Pair(MaterialTheme.colorScheme.primary, "Primary"),
        Pair(MaterialTheme.colorScheme.secondary, "Secondary"),
        Pair(MaterialTheme.colorScheme.tertiary, "Tertiary"),
        Pair(Color(0xFF9C27B0), "Purple"),
        Pair(Color(0xFF3F51B5), "Indigo"),
        Pair(Color(0xFF2196F3), "Blue"),
        Pair(Color(0xFF00BCD4), "Cyan"),
        Pair(Color(0xFF4CAF50), "Green"),
        Pair(Color(0xFFFF9800), "Orange"),
        Pair(Color(0xFF795548), "Brown"),
        Pair(Color(0xFF607D8B), "Gray")
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Palette,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Quick Colors",
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
        ) {
            androidx.compose.foundation.lazy.LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(quickColors.size) { index ->
                    val (color, label) = quickColors[index]
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    2.dp,
                                    MaterialTheme.colorScheme.outline,
                                    CircleShape
                                )
                                .clickable { onColorSelected(color) },
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.1f))
                                    .padding(4.dp)
                            )
                        }
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}