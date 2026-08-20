package com.vng.sajja.ui.screens.background

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vng.sajja.ui.LivePreview
import com.vng.sajja.ui.components.AdvancedColorPickerDialog
import com.vng.sajja.ui.screens.background.components.BackgroundTypeSelectorCard
import com.vng.sajja.ui.utils.toComposeColor
import com.vng.sajja.ui.viewmodel.SettingsViewModel
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

@Composable
fun BackgroundSettingsScreen(
    viewModel: SettingsViewModel,
    isVisible: Boolean = true,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settings.collectAsState()
    var showColorPicker by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val internalUri = copyUriToInternalStorage(context, it, "background")
            if (internalUri != null) {
                viewModel.updateSettings(settings.copy(backgroundImageUri = internalUri))
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
        // Live Clock Preview Card
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Live Clock Preview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            LivePreview(
                settings = settings,
                isVisible = isVisible,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )
        }

        // Background Type & Fills Card
        BackgroundTypeSelectorCard(
            settings = settings,
            onSettingsChange = { viewModel.updateSettings(it) },
            onRequestColorPicker = { showColorPicker = it },
            onPickImage = { imageLauncher.launch("image/*") }
        )

        Spacer(modifier = Modifier.height(130.dp))
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
                "bg_color" -> viewModel.updateSettings(settings.copy(backgroundColor = color.toArgb()))
                "grad_start" -> viewModel.updateSettings(settings.copy(gradientStartColor = color.toArgb()))
                "grad_end" -> viewModel.updateSettings(settings.copy(gradientEndColor = color.toArgb()))
            }
            showColorPicker = null
        }
    }
}

private fun copyUriToInternalStorage(context: Context, uri: Uri, prefix: String = "background"): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val folder = File(context.filesDir, prefix)
        if (!folder.exists()) {
            folder.mkdirs()
        }
        val fileName = "${prefix}_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}.png"
        val file = File(folder, fileName)
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
        Uri.fromFile(file).toString()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
