package com.aquasafe.monitor.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aquasafe.monitor.ui.screens.HistoryScreen
import com.aquasafe.monitor.ui.screens.MapScreen
import com.aquasafe.monitor.ui.screens.OverviewScreen
import com.aquasafe.monitor.ui.screens.SensorScreen
import com.aquasafe.monitor.ui.screens.SettingsScreen
import com.aquasafe.monitor.ui.theme.AccentCyan
import com.aquasafe.monitor.ui.theme.AccentCyanSoft
import com.aquasafe.monitor.ui.theme.AppBackgroundGradient
import com.aquasafe.monitor.ui.theme.CardBorder
import com.aquasafe.monitor.ui.theme.Panel
import com.aquasafe.monitor.ui.theme.TextMuted
import com.aquasafe.monitor.ui.theme.TextSecondary
import com.aquasafe.monitor.viewmodel.DashboardUiState
import com.aquasafe.monitor.viewmodel.DashboardViewModel

/** Destinasi bottom navigation */
sealed class AppDestination(val route: String, val label: String, val icon: ImageVector) {
    data object Beranda : AppDestination("beranda", "Beranda", Icons.Rounded.Home)
    data object Sensor : AppDestination("sensor", "Sensor", Icons.Rounded.Info)
    data object Riwayat : AppDestination("riwayat", "Riwayat", Icons.AutoMirrored.Rounded.List)
    data object Lokasi : AppDestination("lokasi", "Lokasi", Icons.Rounded.LocationOn)
    data object Pengaturan : AppDestination("pengaturan", "Pengaturan", Icons.Rounded.Settings)

    companion object {
        val All = listOf(Beranda, Sensor, Riwayat, Lokasi, Pengaturan)
    }
}

/** Root aplikasi: scaffolding + bottom nav (pill bar) + NavHost */
@Composable
fun AppRoot(viewModel: DashboardViewModel = viewModel()) {
    val state: DashboardUiState by viewModel.uiState.collectAsState()
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Box(
        Modifier
            .fillMaxSize()
            .background(AppBackgroundGradient),
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                PillNavBar(
                    currentRoute = currentRoute,
                    onSelect = { destination ->
                        navController.navigate(destination.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            },
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = AppDestination.Beranda.route,
                modifier = Modifier.padding(innerPadding),
                enterTransition = {
                    fadeIn(animationSpec = tween(240)) +
                        scaleIn(initialScale = 0.97f, animationSpec = tween(240))
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(180)) +
                        scaleOut(targetScale = 0.99f, animationSpec = tween(180))
                },
            ) {
            composable(AppDestination.Beranda.route) {
                OverviewScreen(
                    state = state,
                    onRefresh = { viewModel.refresh() },
                    onNavigate = { route -> navController.navigate(route) },
                    onSetTimeRange = viewModel::setTimeRange,
                )
            }
            composable(AppDestination.Sensor.route) {
                SensorScreen(
                    state = state,
                    onSetTimeRange = viewModel::setTimeRange,
                )
            }
            composable(AppDestination.Riwayat.route) {
                HistoryScreen(state = state)
            }
            composable(AppDestination.Lokasi.route) {
                MapScreen(
                    state = state,
                    onAdd = viewModel::addLocation,
                    onRemove = viewModel::removeLocation,
                    onSync = viewModel::syncLocation,
                )
            }
            composable(AppDestination.Pengaturan.route) {
                SettingsScreen(state = state)
            }
            }
        }
    }
}

/** Bottom navigation berbentuk pill mengambang â€” item aktif "melompat" dengan spring */
@Composable
private fun PillNavBar(
    currentRoute: String?,
    onSelect: (AppDestination) -> Unit,
) {
    val floatSpring = spring<Float>(dampingRatio = Spring.DampingRatioMediumBouncy)
    val colorSpring = spring<Color>(dampingRatio = Spring.DampingRatioMediumBouncy)
    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Panel.copy(alpha = 0.96f))
                .border(1.dp, CardBorder, RoundedCornerShape(24.dp))
                .padding(horizontal = 8.dp, vertical = 8.dp),
        ) {
            AppDestination.All.forEach { destination ->
                val selected = currentRoute == destination.route
                val tint by animateColorAsState(
                    targetValue = if (selected) AccentCyan else TextMuted,
                    animationSpec = colorSpring,
                    label = "navTint",
                )
                val iconScale by animateFloatAsState(
                    targetValue = if (selected) 1.12f else 1f,
                    animationSpec = floatSpring,
                    label = "navScale",
                )
                val glow by animateColorAsState(
                    targetValue = if (selected) AccentCyanSoft else Color.Transparent,
                    animationSpec = colorSpring,
                    label = "navGlow",
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(glow)
                        .clickable { onSelect(destination) }
                        .padding(vertical = 7.dp),
                ) {
                    Icon(
                        destination.icon,
                        contentDescription = destination.label,
                        tint = tint,
                        modifier = Modifier
                            .size(22.dp)
                            .scale(iconScale),
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        destination.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        color = if (selected) AccentCyan else TextSecondary,
                    )
                }
            }
        }
    }
}
