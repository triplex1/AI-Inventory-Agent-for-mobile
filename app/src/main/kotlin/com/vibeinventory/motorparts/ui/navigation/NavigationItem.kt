package com.vibeinventory.motorparts.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object InventoryList : NavigationItem(
        route = "inventory_list",
        title = "Inventory",
        icon = Icons.Default.Inventory
    )
    
    object VoiceCommand : NavigationItem(
        route = "voice_command",
        title = "Voice",
        icon = Icons.Default.Mic
    )
    
    object Settings : NavigationItem(
        route = "settings",
        title = "Settings",
        icon = Icons.Default.Settings
    )
}

val bottomNavItems = listOf(
    NavigationItem.InventoryList,
    NavigationItem.VoiceCommand,
    NavigationItem.Settings
)
