package com.vng.sajja.ui.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.vng.sajja.ui.components.GlassmorphicCard
import com.vng.sajja.ui.viewmodel.SettingsViewModel

@Composable
fun ToolsSettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settings.collectAsState()
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
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
            // Import / Export Card
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
                        onClick = {
                            val json = viewModel.exportTheme()
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, json)
                                putExtra(Intent.EXTRA_SUBJECT, "Sajja Theme Backup")
                            }
                            ContextCompat.startActivity(
                                context,
                                Intent.createChooser(shareIntent, "Export Theme"),
                                null
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = buttonColors
                    ) {
                        Text("Export to Share")
                    }

                    FilledTonalButton(
                        onClick = { filePicker.launch("application/json") },
                        modifier = Modifier.weight(1f),
                        colors = buttonColors
                    ) {
                        Text("Import from File")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = jsonInput,
                    onValueChange = { jsonInput = it },
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
                        onClick = {
                            if (jsonInput.isNotEmpty()) {
                                viewModel.importTheme(jsonInput)
                                jsonInput = ""
                            }
                        },
                        enabled = jsonInput.isNotEmpty()
                    ) {
                        Text("Apply JSON")
                    }
                }
            }



            // Quick Preset Backup Card
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
                        onClick = { viewModel.savePreset(context) },
                        modifier = Modifier.weight(1f),
                        colors = buttonColors
                    ) {
                        Text("Backup Preset")
                    }

                    FilledTonalButton(
                        onClick = { viewModel.loadLastPreset(context) },
                        modifier = Modifier.weight(1f),
                        colors = buttonColors
                    ) {
                        Text("Restore Backup")
                    }
                }
            }
        }

    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text("Import Theme Settings") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Confirm restoring Sajja wallpaper configurations from this JSON file?")
                    OutlinedTextField(
                        value = jsonInput,
                        onValueChange = {},
                        label = { Text("JSON Payload") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        readOnly = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.importTheme(jsonInput)
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
