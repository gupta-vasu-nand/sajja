package com.vng.sajja.ui.screens.tools

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.vng.sajja.ui.screens.tools.components.ImportExportCard
import com.vng.sajja.ui.screens.tools.components.LocalPresetsBackupCard
import com.vng.sajja.ui.viewmodel.SettingsViewModel

@Composable
fun ToolsSettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
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
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Import / Export Card
        ImportExportCard(
            jsonInput = jsonInput,
            onJsonInputChange = { jsonInput = it },
            onExportShare = {
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
            onImportFile = { filePicker.launch("application/json") },
            onApplyJson = {
                if (jsonInput.isNotEmpty()) {
                    viewModel.importTheme(jsonInput)
                    jsonInput = ""
                }
            }
        )

        // Quick Preset Backup Card
        LocalPresetsBackupCard(
            onBackupPreset = { viewModel.savePreset(context) },
            onRestoreBackup = { viewModel.loadLastPreset(context) }
        )
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
