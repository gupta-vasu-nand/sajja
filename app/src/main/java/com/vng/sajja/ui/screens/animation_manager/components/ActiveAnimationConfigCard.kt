package com.vng.sajja.ui.screens.animation_manager.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vng.sajja.domain.model.WallpaperSettings
import com.vng.sajja.ui.components.GlassmorphicCard

@Composable
fun ActiveAnimationConfigCard(
    settings: WallpaperSettings,
    onBuildNew: () -> Unit,
    onSaveActive: () -> Unit
) {
    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
        Text("Active Animation Config", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Particle Effect: ${settings.particleType.name}", fontSize = 14.sp)
            Text("Particle Speed: ${settings.particleSpeed}x", fontSize = 14.sp)
            Text("Particle Count: ${settings.particleCount}", fontSize = 14.sp)
            Text("Digit Switch Animation: ${settings.digitalAnimType.name}", fontSize = 14.sp)
            Text("Transition Speed: ${settings.digitalAnimDuration}ms", fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onBuildNew,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create")
                Spacer(modifier = Modifier.width(6.dp))
                Text("Build New", fontSize = 13.sp)
            }

            OutlinedButton(
                onClick = onSaveActive,
                modifier = Modifier.weight(1.2f)
            ) {
                Icon(Icons.Default.Save, contentDescription = "Save Current")
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save Active", fontSize = 13.sp)
            }
        }
    }
}
