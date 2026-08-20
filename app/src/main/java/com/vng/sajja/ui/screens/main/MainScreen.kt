package com.vng.sajja.ui.screens.main

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.vng.sajja.ui.components.AppNavBar
import com.vng.sajja.ui.components.CircularFabMenu
import com.vng.sajja.ui.components.FabMenuItem
import com.vng.sajja.ui.navigation.SajjaNavGraph
import com.vng.sajja.ui.navigation.Screen
import com.vng.sajja.ui.screens.main.components.SetWallpaperTopBarButton
import com.vng.sajja.ui.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    viewModel: SettingsViewModel
) {
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsState()

    var fabExpanded by remember { mutableStateOf(false) }
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    // Stack to track bottom nav bar tab history for back navigation
    var bottomNavHistory by remember { mutableStateOf(listOf(0)) }

    val pagerState = rememberPagerState(pageCount = { 4 })
    val coroutineScope = rememberCoroutineScope()

    // Track horizontal pager swipe changes in history stack
    LaunchedEffect(pagerState.currentPage) {
        if (bottomNavHistory.lastOrNull() != pagerState.currentPage) {
            bottomNavHistory = bottomNavHistory + pagerState.currentPage
        }
    }

    // Custom System BackHandler
    BackHandler(enabled = true) {
        if (fabExpanded) {
            fabExpanded = false
        } else if (currentRoute != Screen.Dashboard.route && currentRoute != null) {
            val popped = navController.popBackStack()
            if (!popped || navController.currentBackStackEntry?.destination?.route == null) {
                navController.navigate(Screen.Dashboard.route) {
                    popUpTo(Screen.Dashboard.route) { inclusive = false }
                }
            }
        } else if (bottomNavHistory.size > 1) {
            val updatedHistory = bottomNavHistory.dropLast(1)
            bottomNavHistory = updatedHistory
            val previousPage = updatedHistory.last()
            coroutineScope.launch {
                pagerState.animateScrollToPage(previousPage)
            }
        } else if (pagerState.currentPage != 0) {
            coroutineScope.launch {
                pagerState.animateScrollToPage(0)
            }
        } else {
            (context as? Activity)?.finish()
        }
    }

    val title = if (currentRoute == Screen.Dashboard.route) {
        when (pagerState.currentPage) {
            0 -> "Sajja Clock Customizer"
            1 -> "Clock Face Settings"
            2 -> "Background Settings"
            3 -> "Usage & Battery Analytics"
            else -> "Sajja Customizer"
        }
    } else {
        when {
            currentRoute?.startsWith("analytics") == true -> "Usage & Battery Analytics"
            currentRoute?.startsWith("theme_settings") == true -> "Color & Presets"
            currentRoute?.startsWith("tools_settings") == true -> "Backup & Tools"
            currentRoute?.startsWith("app_settings") == true -> "App Preferences"
            currentRoute?.startsWith("animation_manager") == true -> "Animation Manager"
            currentRoute?.startsWith("animation_builder") == true -> "Animation Builder"
            else -> "Sajja Customizer"
        }
    }

    val menuItems = listOf(
        FabMenuItem(Icons.Default.Palette, "Themes", Screen.ThemeSettings.route),
        FabMenuItem(Icons.Default.AutoAwesome, "Animations", Screen.AnimationManager.route),
        FabMenuItem(Icons.Default.Handyman, "Tools", Screen.ToolsSettings.route),
        FabMenuItem(Icons.Default.Settings, "Settings", Screen.AppSettings.route)
    )

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    if (currentRoute != Screen.Dashboard.route) {
                        IconButton(onClick = {
                            val popped = navController.popBackStack()
                            if (!popped || navController.currentBackStackEntry?.destination?.route == null) {
                                navController.navigate(Screen.Dashboard.route) {
                                    popUpTo(Screen.Dashboard.route) { inclusive = false }
                                }
                            }
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    if (currentRoute == Screen.Dashboard.route) {
                        SetWallpaperTopBarButton(context = context, settings = settings)
                    }
                }
            )
        },
        bottomBar = {
            if (currentRoute == Screen.Dashboard.route) {
                AppNavBar(
                    selectedTab = pagerState.currentPage,
                    onTabSelected = { index ->
                        if (bottomNavHistory.lastOrNull() != index) {
                            bottomNavHistory = bottomNavHistory + index
                        }
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            CircularFabMenu(
                expanded = fabExpanded,
                onToggle = { fabExpanded = !fabExpanded },
                items = menuItems,
                onItemClick = { item ->
                    fabExpanded = false
                    navController.navigate(item.route) {
                        popUpTo(Screen.Dashboard.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        SajjaNavGraph(
            navController = navController,
            viewModel = viewModel,
            pagerState = pagerState,
            modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
        )
    }
}
