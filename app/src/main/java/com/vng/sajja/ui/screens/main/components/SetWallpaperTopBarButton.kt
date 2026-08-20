package com.vng.sajja.ui.screens.main.components

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vng.sajja.domain.model.ClockType
import com.vng.sajja.domain.model.WallpaperSettings
import com.vng.sajja.domain.services.ArabicClockWallpaperService
import com.vng.sajja.domain.services.DigitalClockWallpaperService
import com.vng.sajja.domain.services.MinimalClockWallpaperService
import com.vng.sajja.domain.services.RomanClockWallpaperService

@Composable
fun SetWallpaperTopBarButton(
    context: Context,
    settings: WallpaperSettings
) {
    Button(
        onClick = {
            val serviceClass = when (settings.clockType) {
                ClockType.ROMAN -> RomanClockWallpaperService::class.java
                ClockType.ARABIC -> ArabicClockWallpaperService::class.java
                ClockType.DIGITAL -> DigitalClockWallpaperService::class.java
                ClockType.MINIMALIST -> MinimalClockWallpaperService::class.java
            }
            val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
                putExtra(
                    WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    ComponentName(context, serviceClass)
                )
            }
            context.startActivity(intent)
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text("Set Live Wallpaper", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}
