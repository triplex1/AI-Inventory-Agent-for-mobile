package com.vibeinventory.motorparts.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
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

    // Not shown in bottom navigation; used for add/edit screen navigation
    object InventoryAddEdit : NavigationItem(
        route = "inventory_add_edit",
        title = "Inventory Form",
        icon = Icons.Default.Inventory
    )
    
    object VoiceCommand : NavigationItem(
        route = "voice_command",
        title = "Voice",
        icon = Icons.Default.Mic
    )
    
    object Analytics : NavigationItem(
        route = "analytics",
        title = "Analytics",
        icon = Icons.Default.Analytics
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
    NavigationItem.Analytics,
    NavigationItem.Settings
)
