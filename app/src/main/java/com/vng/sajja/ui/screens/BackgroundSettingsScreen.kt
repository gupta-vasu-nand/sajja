package com.vng.sajja.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vng.sajja.domain.model.BackgroundType
import com.vng.sajja.ui.components.AdvancedColorPickerDialog
import com.vng.sajja.ui.components.ColorOptionCard
import com.vng.sajja.ui.components.GlassmorphicCard
import com.vng.sajja.ui.utils.toComposeColor
import com.vng.sajja.ui.viewmodel.SettingsViewModel

@Composable
fun BackgroundSettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settings.collectAsState()
    var showColorPicker by remember { mutableStateOf<String?>(null) }

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.updateSettings(settings.copy(backgroundImageUri = it.toString()))
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Text("Background Type", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BackgroundType.entries.forEach { type ->
                        FilterChip(
                            selected = settings.backgroundType == type,
                            onClick = { viewModel.updateSettings(settings.copy(backgroundType = type)) },
                            label = { Text(type.name) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

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
                        Spacer(modifier = Modifier.height(8.dp))
                        ColorOptionCard(
                            label = "End Color",
                            color = android.graphics.Color.valueOf(settings.gradientEndColor).toComposeColor(),
                            onClick = { showColorPicker = "grad_end" }
                        )
                    }
                    BackgroundType.IMAGE -> {
                        Text(
                            "Choose an image from your device as the wallpaper background.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { imageLauncher.launch("image/*") },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Image, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Select Image")
                        }
                        val bgUri = settings.backgroundImageUri
                        if (bgUri != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Selected: ${bgUri.substringAfterLast("/")}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    BackgroundType.COLLAGE -> {
                        Text(
                            "Use the Collage Designer screen to manage your collage images.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
                "bg_color" -> viewModel.updateSettings(settings.copy(backgroundColor = color.toArgb()))
                "grad_start" -> viewModel.updateSettings(settings.copy(gradientStartColor = color.toArgb()))
                "grad_end" -> viewModel.updateSettings(settings.copy(gradientEndColor = color.toArgb()))
            }
            showColorPicker = null
        }
    }
}
