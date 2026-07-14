package com.vng.sajja.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlin.math.roundToInt

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

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight()
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ColorLens,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Select Color",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Selected Color Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(selectedColor)
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant,
                            RoundedCornerShape(16.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val textColor = if (selectedColor.luminance() > 0.5) Color.Black else Color.White
                        Text(
                            text = "Hex Code",
                            color = textColor.copy(alpha = 0.8f),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "#${r.toString(16).padStart(2, '0').uppercase()}${g.toString(16).padStart(2, '0').uppercase()}${b.toString(16).padStart(2, '0').uppercase()}",
                            color = textColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                // RGB sliders stacked horizontally
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ColorSliderRow(
                        label = "R",
                        value = r,
                        onValueChange = { r = it },
                        color = Color(0xFFE57373)
                    )
                    ColorSliderRow(
                        label = "G",
                        value = g,
                        onValueChange = { g = it },
                        color = Color(0xFF81C784)
                    )
                    ColorSliderRow(
                        label = "B",
                        value = b,
                        onValueChange = { b = it },
                        color = Color(0xFF64B5F6)
                    )
                }

                // Divider
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Color Palette
                QuickColorPalette { color ->
                    r = (color.red * 255).roundToInt()
                    g = (color.green * 255).roundToInt()
                    b = (color.blue * 255).roundToInt()
                }

                // Dialog Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onPick(selectedColor)
                            onDismiss()
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Apply")
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorSliderRow(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Label with color dot
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.width(36.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Text(
                text = label,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Slider takes up the rest
        Slider(
            value = value / 255f,
            onValueChange = { onValueChange((it * 255).roundToInt()) },
            valueRange = 0f..1f,
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(
                thumbColor = color,
                activeTrackColor = color,
                inactiveTrackColor = color.copy(alpha = 0.25f)
            )
        )

        // Value text chip
        Text(
            text = value.toString().padStart(3, '0'),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(28.dp)
        )
    }
}

@Composable
private fun QuickColorPalette(onColorSelected: (Color) -> Unit) {
    val quickColors = listOf(
        Pair(Color.Red, "Red"),
        Pair(Color.Green, "Green"),
        Pair(Color.Blue, "Blue"),
        Pair(Color.Yellow, "Yellow"),
        Pair(Color.Magenta, "Magenta"),
        Pair(Color.Cyan, "Cyan"),
        Pair(Color.White, "White"),
        Pair(Color.Black, "Black"),
        Pair(Color(0xFF9C27B0), "Purple"),
        Pair(Color(0xFF3F51B5), "Indigo"),
        Pair(Color(0xFFFF9800), "Orange"),
        Pair(Color(0xFF795548), "Brown")
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Palette,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = "Preset Colors",
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        ) {
            androidx.compose.foundation.lazy.LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(quickColors.size) { index ->
                    val (color, _) = quickColors[index]
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outlineVariant,
                                CircleShape
                            )
                            .clickable { onColorSelected(color) }
                    )
                }
            }
        }
    }
}
