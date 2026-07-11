package com.vng.sajja.ui.screens

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.vng.sajja.domain.model.ClockType
import com.vng.sajja.domain.services.RomanClockWallpaperService
import com.vng.sajja.domain.services.ArabicClockWallpaperService
import com.vng.sajja.domain.services.DigitalClockWallpaperService
import com.vng.sajja.domain.services.MinimalClockWallpaperService
import com.vng.sajja.ui.LivePreview
import com.vng.sajja.ui.components.CircularFabMenu
import com.vng.sajja.ui.components.CommonBottomNavBar
import com.vng.sajja.ui.components.ExpandableFab
import com.vng.sajja.ui.components.FabMenuItem
import com.vng.sajja.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsState()

    var fabExpanded by remember { mutableStateOf(false) }

    val title = when (currentRoute) {
        "dashboard" -> "Sajja Clock Customizer"
        "background_settings" -> "Background Settings"
        "clock_settings" -> "Clock Face Settings"
        "collage_settings" -> "Collage Designer"
        "theme_settings" -> "Color & Presets"
        "tools_settings" -> "Backup & Tools"
        "app_settings" -> "App Preferences"
        else -> "Sajja Customizer"
    }

    val menuItems = listOf(
        FabMenuItem(Icons.Default.Wallpaper, "Background", "background_settings"),
        FabMenuItem(Icons.Default.Schedule, "Clock Face", "clock_settings"),
        FabMenuItem(Icons.Default.PhotoLibrary, "Collage", "collage_settings"),
        FabMenuItem(Icons.Default.Palette, "Themes", "theme_settings")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    if (currentRoute != "dashboard") {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    if (currentRoute == "dashboard") {
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
                }
            )
        },
        bottomBar = {
            CommonBottomNavBar(navController = navController)
        },
        floatingActionButton = {
            ExpandableFab(
                expanded = fabExpanded,
                onClick = { fabExpanded = !fabExpanded }
            )
        }
    ) { paddingValues ->
        val primaryColor = MaterialTheme.colorScheme.primary
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .drawBehind {
                    val brush1 = Brush.radialGradient(
                        colors = listOf(primaryColor.copy(alpha = 0.12f), Color.Transparent),
                        center = Offset(0f, 0f),
                        radius = size.width * 0.7f
                    )
                    drawRect(brush = brush1)

                    val brush2 = Brush.radialGradient(
                        colors = listOf(primaryColor.copy(alpha = 0.08f), Color.Transparent),
                        center = Offset(size.width, size.height * 0.8f),
                        radius = size.width * 0.8f
                    )
                    drawRect(brush = brush2)
                }
        ) {
            NavHost(
                navController = navController,
                startDestination = "dashboard",
                modifier = Modifier.fillMaxSize(),
                enterTransition = { fadeIn(animationSpec = tween(300)) + slideInHorizontally(animationSpec = tween(300)) { it } },
                exitTransition = { fadeOut(animationSpec = tween(300)) + slideOutHorizontally(animationSpec = tween(300)) { -it } },
                popEnterTransition = { fadeIn(animationSpec = tween(300)) + slideInHorizontally(animationSpec = tween(300)) { -it } },
                popExitTransition = { fadeOut(animationSpec = tween(300)) + slideOutHorizontally(animationSpec = tween(300)) { it } }
            ) {
                composable("dashboard") {
                    DashboardScreen(viewModel = viewModel, navController = navController)
                }
                composable("background_settings") {
                    BackgroundSettingsScreen(viewModel = viewModel)
                }
                composable("clock_settings") {
                    ClockSettingsScreen(viewModel = viewModel)
                }
                composable("collage_settings") {
                    CollageSettingsScreen(viewModel = viewModel)
                }
                composable("theme_settings") {
                    ThemeSettingsScreen(viewModel = viewModel)
                }
                composable("tools_settings") {
                    ToolsSettingsScreen(viewModel = viewModel)
                }
                composable("app_settings") {
                    AppSettingsScreen(viewModel = viewModel)
                }
            }

            // Radial circular menu overlay concentric to the bottom-right corner FAB
            CircularFabMenu(
                expanded = fabExpanded,
                items = menuItems,
                onItemClick = { item ->
                    fabExpanded = false
                    navController.navigate(item.route) {
                        popUpTo("dashboard") { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 16.dp, end = 16.dp)
            )
        }
    }
}

data class CategoryItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun DashboardScreen(
    viewModel: SettingsViewModel,
    navController: NavController
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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(20.dp),
                        spotColor = MaterialTheme.colorScheme.primary
                    )
            ) {
                LivePreview(settings = settings)
            }

            // Quick Status bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Style: ${settings.clockType.name}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Background: ${settings.backgroundType.name}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Text(
            text = "Wallpaper Customization",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Custom Categories List / Grid (styled beautiful)
        val categories = listOf(
            CategoryItem("Background Style", "Set colors, gradients, or custom collage backgrounds.", Icons.Default.Wallpaper, "background_settings"),
            CategoryItem("Clock Face & Hands", "Configure clock dial numerals, hands, ticks, and day/date display.", Icons.Default.Schedule, "clock_settings"),
            CategoryItem("Collage Designer", "Manage collage layouts and arrange photos.", Icons.Default.PhotoLibrary, "collage_settings"),
            CategoryItem("Theme Presets", "Load instant styled layouts or color themes.", Icons.Default.Palette, "theme_settings"),
            CategoryItem("Backup & Tools", "Import or export your Sajja settings configurations.", Icons.Default.Settings, "tools_settings"),
            CategoryItem("App Preferences", "Customize dark mode and application accent colors.", Icons.Default.Tune, "app_settings")
        )

        categories.forEach { item ->
            DashboardCard(
                title = item.title,
                description = item.description,
                icon = item.icon,
                onClick = { navController.navigate(item.route) }
            )
        }
    }
}

@Composable
fun DashboardCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background == Color.Black

    val backgroundColor = if (isDark) {
        Color.White.copy(alpha = 0.05f)
    } else {
        Color.Black.copy(alpha = 0.03f)
    }

    val borderColor = if (isDark) {
        Color.White.copy(alpha = 0.12f)
    } else {
        Color.Black.copy(alpha = 0.08f)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(20.dp)
        )
    }
}
