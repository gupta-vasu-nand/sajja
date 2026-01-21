package com.vng.sajja

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.vng.sajja.settings.WallpaperSettingsRepository
import com.vng.sajja.ui.SettingsScreen
import com.vng.sajja.ui.theme.SajjaTheme

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SajjaTheme {
                AppContent(this)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContent(activity: MainActivity) {
    val repo = WallpaperSettingsRepository(activity)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Roman Clock Wallpaper") },
                actions = {
                    TextButton(onClick = {
                        val intent =
                            Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
                                .putExtra(
                                    WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                                    ComponentName(
                                        activity,
                                        RomanClockWallpaperService::class.java
                                    )
                                )
                        activity.startActivity(intent)
                    }) {
                        Text("Apply Wallpaper")
                    }
                }
            )
        }
    ) { paddingValues ->
        SettingsScreen(
            repo = repo,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}
