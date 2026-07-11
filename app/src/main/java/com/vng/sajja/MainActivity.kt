package com.vng.sajja

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vng.sajja.ui.components.SajjaSplashScreen
import com.vng.sajja.ui.screens.MainScreen
import com.vng.sajja.ui.theme.SajjaTheme
import com.vng.sajja.ui.viewmodel.SettingsViewModel
import com.vng.sajja.ui.viewmodel.SettingsViewModelFactory

class MainActivity : ComponentActivity() {

    companion object {
        private var isFirstLaunch = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Hide the bottom navigation pill/buttons (System Navigation Bar)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        val container = (application as SajjaApplication).container
        val wallpaperRepo = container.wallpaperSettingsRepository
        val appRepo = container.appSettingsRepository

        setContent {
            val viewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModelFactory(wallpaperRepo, appRepo)
            )
            val appSettings by viewModel.appSettings.collectAsState()

            var showSplash by remember { mutableStateOf(isFirstLaunch) }

            SajjaTheme(appSettings = appSettings) {
                if (showSplash) {
                    SajjaSplashScreen(
                        onDismiss = {
                            isFirstLaunch = false
                            showSplash = false
                        }
                    )
                } else {
                    MainScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}
