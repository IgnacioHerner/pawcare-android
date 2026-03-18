package com.ignaherner.pawcare.presentation.pets

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ignaherner.pawcare.presentation.components.PetCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetListScreen(
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToForm: () -> Unit,
    viewModel: PetViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val mensajeMascotas = when (val currentState = uiState) {
        is PetUiState.Success -> {
            if (currentState.pets.size == 1) {
                "¿Cómo está ${currentState.pets.first().nombre} hoy? 🐾"
            } else {
                "¿Cómo están tus ${currentState.pets.size} mascotas hoy? 🐾"
            }
        }
        else -> "¿Cómo están tus mascotas hoy? 🐾"
    }

    Scaffold(
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
                    text = "Hola, Ignacio 👋",
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
                        Text(
                            text = "Agregar tu primera mascota \uD83D\uDC3E",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    is PetUiState.Success -> {
                        LazyColumn(contentPadding = PaddingValues(bottom = 80.dp)) {
                            items(
                                items = state.pets,
                                key = { it.id }
                            ) { pet ->
                                PetCard(
                                    pet = pet,
                                    onClick = { onNavigateToDetail(pet.id) },
                                    onDeleteClick = { viewModel.deletePet(pet) }
                                )
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