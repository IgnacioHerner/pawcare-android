package com.ignaherner.pawcare.presentation.pets

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ignaherner.pawcare.domain.model.Pet
import com.ignaherner.pawcare.presentation.components.ConfirmDeleteDialog
import com.ignaherner.pawcare.presentation.components.PetCard
import com.ignaherner.pawcare.presentation.components.SwipeRevealCard
import com.ignaherner.pawcare.presentation.settings.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetListScreen(
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToForm: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: PetViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val nombreUsuario by settingsViewModel.nombreUsuario.collectAsStateWithLifecycle()
    var petToDelete by remember { mutableStateOf<Pet?>(null) }

    petToDelete?.let { pet ->
        ConfirmDeleteDialog(
            titulo = "¿Eliminar a ${pet.nombre}?",
            mensaje = "Esta accion no se puede deshacer. ",
            onConfirm = {
                viewModel.deletePet(pet)
                petToDelete = null
            },
            onDismiss = {petToDelete = null}
        )
    }

    val mensajeMascotas = when (val currentState = uiState) {
        is PetUiState.Success -> {
            if (currentState.pets.size == 1) {
                "¿Cómo está ${currentState.pets.first().nombre} hoy?"
            } else {
                "¿Cómo están tus ${currentState.pets.size} mascotas hoy?"
            }
        }
        else -> "¿Cómo están tus mascotas hoy?"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis mascotas \uD83D\uDC3E")},
                actions = {
                    IconButton(
                        onClick = { onNavigateToSettings()}
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Configuracion"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToForm) {
                Icon(Icons.Default.Add, contentDescription = "Agregar mascota")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Saludo
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Hola, $nombreUsuario 👋",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = mensajeMascotas,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Estados
            Box(
                modifier = Modifier.fillMaxSize())
            {
                when(val state = uiState) {
                    is PetUiState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    is PetUiState.Empty -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "🐾",
                                style = MaterialTheme.typography.displayLarge
                            )
                            Text(
                                text = "Todavía no tenés mascotas",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Tocá el + para agregar la primera",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    is PetUiState.Success -> {
                        LazyColumn(contentPadding = PaddingValues(bottom = 80.dp)) {
                            items(
                                items = state.pets,
                                key = { it.id }
                            ) { pet ->
                                SwipeRevealCard(
                                    onDelete = { petToDelete = pet},
                                    onEdit = { onNavigateToEdit(pet.id)}
                                ) {
                                    PetCard(
                                        pet = pet,
                                        onClick = { onNavigateToDetail(pet.id)}
                                    ) { }
                                }
                            }
                        }
                    }
                    is PetUiState.Error -> {
                        Text(
                            text = state.mensaje,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }

        }
    }
}