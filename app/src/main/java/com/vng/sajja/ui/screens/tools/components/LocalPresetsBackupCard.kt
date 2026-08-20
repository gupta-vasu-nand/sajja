package com.vng.sajja.ui.screens.tools.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vng.sajja.ui.components.GlassmorphicCard

@Composable
fun LocalPresetsBackupCard(
    onBackupPreset: () -> Unit,
    onRestoreBackup: () -> Unit
) {
    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            "Local Presets",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val buttonColors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                contentColor = MaterialTheme.colorScheme.primary
            )
            FilledTonalButton(
                onClick = onBackupPreset,
                modifier = Modifier.weight(1f),
                colors = buttonColors
            ) {
                Text("Backup Preset")
            }

            FilledTonalButton(
                onClick = onRestoreBackup,
                modifier = Modifier.weight(1f),
                colors = buttonColors
            ) {
                Text("Restore Backup")
            }
        }
    }
}
