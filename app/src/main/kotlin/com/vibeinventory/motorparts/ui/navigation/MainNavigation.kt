package com.vibeinventory.motorparts.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.vibeinventory.motorparts.ui.screens.analytics.AnalyticsScreen
import com.vibeinventory.motorparts.ui.screens.inventory.InventoryAddEditScreen
import com.vibeinventory.motorparts.ui.screens.inventory.InventoryListScreen
import com.vibeinventory.motorparts.ui.screens.settings.SettingsScreen
import com.vibeinventory.motorparts.ui.screens.voice.VoiceCommandScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title
                            )
                        },
                        label = { Text(item.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavigationItem.InventoryList.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavigationItem.InventoryList.route) {
                InventoryListScreen(
                    onAddItem = {
                        navController.navigate(NavigationItem.InventoryAddEdit.route)
                    },
                    onEditItem = { itemId ->
                        navController.navigate(
                            NavigationItem.InventoryAddEdit.route + "?itemId=$itemId"
                        )
                    }
                )
            }
            composable(
                route = NavigationItem.InventoryAddEdit.route + "?itemId={itemId}",
                arguments = listOf(
                    navArgument("itemId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val itemId = backStackEntry.arguments?.getString("itemId")
                InventoryAddEditScreen(
                    itemId = itemId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(NavigationItem.VoiceCommand.route) {
                VoiceCommandScreen()
            }
            composable(NavigationItem.Analytics.route) {
                AnalyticsScreen()
            }
            composable(NavigationItem.Settings.route) {
                SettingsScreen()
            }
        }
    }
}
