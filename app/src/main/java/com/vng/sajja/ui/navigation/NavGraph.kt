package com.vng.sajja.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.vng.sajja.ui.screens.analytics.AnalyticsScreen
import com.vng.sajja.ui.screens.animation_builder.AnimationBuilderScreen
import com.vng.sajja.ui.screens.animation_manager.AnimationManagerScreen
import com.vng.sajja.ui.screens.app_settings.AppSettingsScreen
import com.vng.sajja.ui.screens.background.BackgroundSettingsScreen
import com.vng.sajja.ui.screens.clock.ClockSettingsScreen
import com.vng.sajja.ui.screens.main.components.DashboardScreen
import com.vng.sajja.ui.screens.theme.ThemeSettingsScreen
import com.vng.sajja.ui.screens.tools.ToolsSettingsScreen
import com.vng.sajja.ui.viewmodel.AnalyticsViewModel
import com.vng.sajja.ui.viewmodel.SettingsViewModel

@Composable
fun SajjaNavGraph(
    navController: NavHostController,
    viewModel: SettingsViewModel,
    pagerState: PagerState,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = modifier
    ) {
        composable(
            route = Screen.Dashboard.route,
            enterTransition = { fadeIn(tween(300)) },
            exitTransition = { fadeOut(tween(300)) }
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> DashboardScreen(
                        viewModel = viewModel,
                        isVisible = true,
                        onNavigate = { route -> navController.navigate(route) }
                    )
                    1 -> ClockSettingsScreen(
                        viewModel = viewModel,
                        isVisible = (pagerState.currentPage == 1)
                    )
                    2 -> BackgroundSettingsScreen(
                        viewModel = viewModel,
                        isVisible = (pagerState.currentPage == 2)
                    )
                    3 -> {
                        val analyticsViewModel: AnalyticsViewModel = viewModel()
                        AnalyticsScreen(viewModel = analyticsViewModel)
                    }
                }
            }
        }
        composable(Screen.BackgroundSettings.route) {
            BackgroundSettingsScreen(viewModel = viewModel, isVisible = true)
        }
        composable(Screen.ClockSettings.route) {
            ClockSettingsScreen(viewModel = viewModel, isVisible = true)
        }
        composable(Screen.AnimationManager.route) {
            AnimationManagerScreen(
                viewModel = viewModel,
                onNavigateToBuilder = { id ->
                    navController.navigate(Screen.AnimationBuilder.createRoute(id))
                }
            )
        }
        composable(
            route = Screen.AnimationBuilder.route,
            arguments = listOf(navArgument("presetId") { defaultValue = -1; type = NavType.IntType })
        ) { backStackEntry ->
            val presetId = backStackEntry.arguments?.getInt("presetId") ?: -1
            AnimationBuilderScreen(
                viewModel = viewModel,
                presetId = presetId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.ThemeSettings.route) {
            ThemeSettingsScreen(viewModel = viewModel)
        }
        composable(Screen.ToolsSettings.route) {
            ToolsSettingsScreen(viewModel = viewModel)
        }
        composable(Screen.AppSettings.route) {
            AppSettingsScreen(viewModel = viewModel)
        }
        composable(Screen.Analytics.route) {
            val analyticsViewModel: AnalyticsViewModel = viewModel()
            AnalyticsScreen(viewModel = analyticsViewModel)
        }
    }
}
