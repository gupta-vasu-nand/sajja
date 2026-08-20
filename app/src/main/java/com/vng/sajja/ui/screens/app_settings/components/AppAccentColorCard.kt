package com.vng.sajja.ui.screens.app_settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vng.sajja.domain.model.AppColorPreset
import com.vng.sajja.domain.model.AppSettings
import com.vng.sajja.ui.components.GlassmorphicCard

@Composable
fun AppAccentColorCard(
    appSettings: AppSettings,
    onSelectPresetColor: (AppColorPreset) -> Unit,
    onRequestCustomColor: () -> Unit
) {
    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
        Text("App Accent Color", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Preset Colors
            AppColorPreset.entries.forEach { colorPreset ->
                val presetColor = Color(android.graphics.Color.parseColor(colorPreset.colorHex))
                val isSelected = appSettings.customPrimaryColorHex == null && appSettings.primaryColor == colorPreset

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CircleShape)
                        .clickable { onSelectPresetColor(colorPreset) }
                        .padding(vertical = 8.dp, horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(presetColor)
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.outline,
                                    CircleShape
                                )
                        )
                        Text(
                            text = colorPreset.nameStr,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    RadioButton(
                        selected = isSelected,
                        onClick = { onSelectPresetColor(colorPreset) }
                    )
                }
            }

            // Custom Color Selector
            val isCustomSelected = appSettings.customPrimaryColorHex != null
            val customColor = if (isCustomSelected) {
                Color(android.graphics.Color.parseColor(appSettings.customPrimaryColorHex))
            } else {
                MaterialTheme.colorScheme.primary
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CircleShape)
                    .clickable { onRequestCustomColor() }
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(customColor)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outline,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ColorLens,
                            contentDescription = "Custom Color",
                            tint = if (isCustomSelected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = if (isCustomSelected) "Custom (${appSettings.customPrimaryColorHex})" else "Choose Custom Color...",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = if (isCustomSelected) FontWeight.Medium else FontWeight.Normal
                    )
                }

                RadioButton(
                    selected = isCustomSelected,
                    onClick = {
                        if (!isCustomSelected) {
                            onRequestCustomColor()
                        }
                    }
                )
            }
        }
    }
}
