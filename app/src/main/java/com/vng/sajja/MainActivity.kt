package com.vng.sajja

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
