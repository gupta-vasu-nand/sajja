package com.vng.sajja.ui.screens.main.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vng.sajja.ui.LivePreview
import com.vng.sajja.ui.viewmodel.SettingsViewModel

private data class CategoryItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun DashboardScreen(
    viewModel: SettingsViewModel,
    isVisible: Boolean,
    onNavigate: (String) -> Unit
) {
    val settings by viewModel.settings.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
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

        Text(
            text = "Wallpaper Customization",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Custom Categories List / Grid
        val categories = listOf(
            CategoryItem("Usage & Battery Analytics", "View charge frequency graphs, screen time activity, and battery health.", Icons.Default.ShowChart, "analytics"),
            CategoryItem("Background Style", "Set colors, gradients, or custom image backgrounds.", Icons.Default.Wallpaper, "background_settings"),
            CategoryItem("Clock Face & Hands", "Configure clock dial numerals, hands, ticks, and day/date display.", Icons.Default.Schedule, "clock_settings"),
            CategoryItem("Animation Manager", "Create, customize, and save live animations and particle effects.", Icons.Default.AutoAwesome, "animation_manager"),
            CategoryItem("Theme Presets", "Load instant styled layouts or color themes.", Icons.Default.Palette, "theme_settings"),
            CategoryItem("Backup & Tools", "Import or export your Sajja settings configurations.", Icons.Default.Settings, "tools_settings"),
            CategoryItem("App Preferences", "Customize dark mode and application accent colors.", Icons.Default.Tune, "app_settings")
        )

        categories.forEach { item ->
            DashboardCategoryCard(
                title = item.title,
                description = item.description,
                icon = item.icon,
                onClick = { onNavigate(item.route) }
            )
        }

        Spacer(modifier = Modifier.height(130.dp))
    }
}
