package com.ignaherner.mispatitas.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.ignaherner.mispatitas.presentation.auth.AuthViewModel
import com.ignaherner.mispatitas.presentation.components.BottomNavItem
import com.ignaherner.mispatitas.presentation.home.HomeScreen
import com.ignaherner.mispatitas.presentation.owners.OwnerViewModel
import com.ignaherner.mispatitas.presentation.settings.SettingsScreen

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
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddPet,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar mascota")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        HomeScreen(
            onNavigateToPetDetail = onNavigateToPetDetail,
            onNavigateToAddPet = onNavigateToAddPet,
            onNavigateToEdit = onNavigateToEdit,
            onNavigateToOwnerDetail = onNavigateToOwnerDetail,
            onNavigateToSettings = onNavigateToSettings,
            bottomPadding = innerPadding.calculateBottomPadding()
        )
    }
}
