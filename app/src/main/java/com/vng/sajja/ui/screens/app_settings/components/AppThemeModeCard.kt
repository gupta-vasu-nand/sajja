package com.vng.sajja.ui.screens.app_settings.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vng.sajja.domain.model.AppThemeMode
import com.vng.sajja.ui.components.GlassmorphicCard
import com.vng.sajja.ui.theme.getContrastingTextColor

@Composable
fun AppThemeModeCard(
    currentMode: AppThemeMode,
    onThemeModeSelected: (AppThemeMode) -> Unit
) {
    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
        Text("App Theme Mode", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AppThemeMode.entries.forEach { mode ->
                val isSelected = currentMode == mode
                val contrastColor = getContrastingTextColor(MaterialTheme.colorScheme.primary)
                FilterChip(
                    selected = isSelected,
                    onClick = { onThemeModeSelected(mode) },
                    shape = CircleShape,
                    label = {
                        Text(
                            text = mode.name,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) contrastColor else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = contrastColor,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}
