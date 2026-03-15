package com.ignaherner.pawcare.presentation.pets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ignaherner.pawcare.domain.model.Pet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetDetailScreen(
    petId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onNavigateToVaccines: (Long) -> Unit,
    onNavigateToAppointments: (Long) -> Unit,
    onNavigateToWeight: (Long) ->  Unit,
    onNavigateToMedication: (Long) -> Unit,
    viewModel: PetViewModel = hiltViewModel()
) {
    val detailState by viewModel.detailState.collectAsStateWithLifecycle()

    // Carga la mascota cuando aparece en la pantalla
    LaunchedEffect(petId) {
        viewModel.loadPetById(petId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = {onNavigateToEdit(petId) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when(val state = detailState) {
                is PetDetailState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is PetDetailState.Error -> {
                    Text(
                        text = state.mensaje,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is PetDetailState.Success -> {
                    PetDetailContent(
                        pet = state.pet,
                        onNavigateToVaccines = { onNavigateToVaccines(petId)},
                        onNavigateToAppointments = {onNavigateToAppointments(petId)},
                        onNavigateToWeight = {onNavigateToWeight(petId)},
                        onNavigateToMedication = {onNavigateToMedication(petId)},
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun PetDetailContent(
    pet: Pet,
    onNavigateToVaccines: () -> Unit,
    onNavigateToAppointments: () -> Unit,
    onNavigateToWeight: () -> Unit,
    onNavigateToMedication: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Info principal
        Text(
            text = pet.nombre,
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = pet.especie.displayName,
            style = MaterialTheme.typography.titleMedium
        )

        pet.peso?.let {
            Text(
                text = "$it kg",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        pet.fechaNacimiento?.let {
            Text(
                text = "Nacimiento: $it",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Navegacion a secciones
        Text(
            text = "Secciones",
            style = MaterialTheme.typography.titleMedium
        )
        Button(
            onClick = onNavigateToVaccines,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("\uD83D\uDC89 Vacunas e historial médico")
        }
        Button(
            onClick = onNavigateToMedication,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Medicaciones")
        }
        Button(
            onClick = onNavigateToAppointments,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("📅 Turnos veterinarios")
        }
        Button(
            onClick = onNavigateToWeight,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("⚖\uFE0F Registro de peso")
        }
    }
}