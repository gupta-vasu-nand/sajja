package com.vng.sajja.ui.screens.tools.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vng.sajja.ui.components.GlassmorphicCard

@Composable
fun ImportExportCard(
    jsonInput: String,
    onJsonInputChange: (String) -> Unit,
    onExportShare: () -> Unit,
    onImportFile: () -> Unit,
    onApplyJson: () -> Unit
) {
    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            "Import & Export Theme",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val buttonColors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                contentColor = MaterialTheme.colorScheme.primary
            )
            FilledTonalButton(
                onClick = onExportShare,
                modifier = Modifier.weight(1f),
                colors = buttonColors
            ) {
                Text("Export to Share")
            }

            FilledTonalButton(
                onClick = onImportFile,
                modifier = Modifier.weight(1f),
                colors = buttonColors
            ) {
                Text("Import from File")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = jsonInput,
            onValueChange = onJsonInputChange,
            label = { Text("Or paste JSON text here") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 5
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = onApplyJson,
                enabled = jsonInput.isNotEmpty()
            ) {
                Text("Apply JSON")
            }
        }
    }
}
