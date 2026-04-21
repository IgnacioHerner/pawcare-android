package com.ignaherner.pawcare.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ignaherner.pawcare.presentation.auth.AuthViewModel
import com.ignaherner.pawcare.presentation.components.BottomNavItem
import com.ignaherner.pawcare.presentation.home.HomeScreen
import com.ignaherner.pawcare.presentation.owners.OwnerViewModel
import com.ignaherner.pawcare.presentation.settings.SettingsScreen

@Composable
fun MainScreen(
    navController: NavHostController,
    onNavigateToPetDetail: (Long) -> Unit,
    onNavigateToAddPet: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onNavigateToOwnerDetail: () -> Unit,
    onNavigateToSettings: () -> Unit,
    authViewModel: AuthViewModel,
    ownerViewModel: OwnerViewModel = hiltViewModel()
){
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Settings
    )

    var selectedIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (selectedIndex == index)
                                    item.selectedIcon else item.icon,
                                contentDescription = item.label
                            )
                        },
                        label = { Text(item.label)},
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index}
                    )
                }
            }
        },
        floatingActionButton = {
            if (selectedIndex == 0) {
                FloatingActionButton(onClick = onNavigateToAddPet) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar mascota")
                }
            }
        }
    ) { innerPadding ->
        when (selectedIndex){
            0 -> HomeScreen(
                onNavigateToPetDetail = onNavigateToPetDetail,
                onNavigateToAddPet = onNavigateToAddPet,
                onNavigateToEdit = onNavigateToEdit,
                onNavigateToOwnerDetail = onNavigateToOwnerDetail,
                onNavigateToSettings = { selectedIndex = 1},
                bottomPadding = innerPadding.calculateBottomPadding()
            )
            1 -> SettingsScreen(
                onNavigateBack = { selectedIndex = 0},
                onNavigateToLogin = {
                    navController.navigate(PawCareDestinations.LOGIN) {
                        popUpTo(0) { inclusive = true}
                    }
                },
                onNavigateToOwnerDetail = onNavigateToOwnerDetail,
                authViewModel = authViewModel
            )
        }
    }
}