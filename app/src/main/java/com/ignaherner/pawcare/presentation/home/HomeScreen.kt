package com.ignaherner.pawcare.presentation.home

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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ignaherner.pawcare.domain.model.Pet
import com.ignaherner.pawcare.presentation.components.ConfirmDeleteDialog
import com.ignaherner.pawcare.presentation.components.PetSummaryCard
import com.ignaherner.pawcare.presentation.components.SwipeRevealCard
import com.ignaherner.pawcare.presentation.owners.OwnerState
import com.ignaherner.pawcare.presentation.owners.OwnerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToPetDetail: (Long) -> Unit,
    onNavigateToAddPet: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToOwnerDetail: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
    ownerViewModel: OwnerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val ownerState by ownerViewModel.ownerState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        ownerViewModel.loadOwner()
        viewModel.loadHome()
    }

    val nombreUsuario = when (val state = ownerState) {
        is OwnerState.Success -> state.owner.nombre
        else -> "Usuario"
    }

    val mensajeMascota = when (val state = uiState) {
        is HomeUiState.Success -> {
            if(state.summaries.size == 1)
                "¿Cómo está ${state.summaries.first().pet.nombre} hoy?"
            else
                "¿Cómo están tus ${state.summaries.size} mascotas hoy?"
        }
        else -> "¿Cómo están tus mascotas hoy?"
    }

    var petToDelete by remember { mutableStateOf<Pet?>(null) }

    petToDelete?.let { pet ->
        ConfirmDeleteDialog(
            titulo = "¿Eliminar a ${pet.nombre}?",
            mensaje = "Se eliminarán también todas sus vacunas, medicamentos y registros. Esta acción no se puede deshacer.",
            onConfirm = {
                viewModel.deletePet(pet)
                petToDelete = null
            },
            onDismiss = { petToDelete = null }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    IconButton(onClick = onNavigateToOwnerDetail) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Mi perfil"
                        )
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Configuración"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddPet) {
                Icon(Icons.Default.Add, contentDescription = "Agregar mascota")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(top = 8.dp)
        ) {
            // Saludo
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Hola, $nombreUsuario \uD83D\uDC4B",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = mensajeMascota,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            // Estados
            Box(modifier = Modifier.fillMaxSize()) {
                when (val state = uiState) {
                    is HomeUiState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    is HomeUiState.Empty -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("🐾", style = MaterialTheme.typography.displayLarge)
                            Text(
                                text = "Todavia no tenés mascotas",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Toca el + para agregar la primera",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    is HomeUiState.Success -> {
                        LazyColumn(
                            contentPadding = PaddingValues(bottom = 80.dp)
                        ) {
                            items(
                                items = state.summaries,
                                key = { it.pet.id }
                            ) { summary ->
                                SwipeRevealCard(
                                    onDelete = { petToDelete = summary.pet },
                                    onEdit = { onNavigateToEdit(summary.pet.id) }
                                ) {
                                    PetSummaryCard(
                                        summary = summary,
                                        onClick = { onNavigateToPetDetail(summary.pet.id)}
                                    )
                                }
                            }
                        }
                    }
                    is HomeUiState.Error -> {
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