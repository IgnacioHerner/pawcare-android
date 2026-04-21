package com.ignaherner.pawcare.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val label: String
) {
    object Home : BottomNavItem(
        icon = Icons.Outlined.Home,
        selectedIcon = Icons.Filled.Home,
        label = "Inicio"
    )
    object Settings : BottomNavItem(
        icon = Icons.Outlined.Settings,
        selectedIcon = Icons.Filled.Settings,
        label = "Configuración"
    )
}