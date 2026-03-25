package com.ignaherner.pawcare.presentation.medications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ignaherner.pawcare.domain.model.Medication
import com.ignaherner.pawcare.domain.model.MedicationStatus
import com.ignaherner.pawcare.presentation.components.MedicationCard
import com.ignaherner.pawcare.presentation.pets.PetDetailState
import com.ignaherner.pawcare.presentation.pets.PetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationScreen(
    petId: Long,
    petName: String,
    onNavigateBack: () -> Unit,
    onNavigateToForm: () -> Unit,
    viewModel: MedicationViewModel = hiltViewModel(),
    petViewModel: PetViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val detailState by petViewModel.detailState.collectAsStateWithLifecycle()

    var filtroSeleccionado by remember { mutableStateOf<MedicationStatus?>(null) }

    LaunchedEffect(petId) {
        viewModel.loadMedications(petId)
        petViewModel.loadPetById(petId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        // Nombre + emoji si ya cargó
                        val titulo = when (val state = detailState) {
                            is PetDetailState.Success ->
                                "${state.pet.nombre} ${state.pet.especie.emoji()}"
                            else -> ""
                        }
                        Text(
                            text = titulo,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Medicamentos \uD83D\uDC8A",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToForm) {
                Icon(Icons.Default.Add, contentDescription = "Agregar medicamento")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = filtroSeleccionado == null,
                        onClick = { filtroSeleccionado = null},
                        label = { Text("Todas", style = MaterialTheme.typography.labelSmall)}
                    )
                }

                item {
                    FilterChip(
                        selected = filtroSeleccionado == MedicationStatus.ACTIVO,
                        onClick = { filtroSeleccionado = MedicationStatus.ACTIVO},
                        label = { Text("En curso", style = MaterialTheme.typography.labelSmall)},
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF4CAF50).copy(alpha = 0.15f),
                            selectedLabelColor = Color(0xFF4CAF50)
                        )
                    )
                }

                item {
                    FilterChip(
                        selected = filtroSeleccionado == MedicationStatus.FINALIZADO,
                        onClick = { filtroSeleccionado = MedicationStatus.FINALIZADO},
                        label = { Text("Finalizado", style = MaterialTheme.typography.labelSmall)},
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                            selectedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                when(val state = uiState) {
                    is MedicationUiState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    is MedicationUiState.Empty -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "\uD83D\uDC8A",
                                style = MaterialTheme.typography.displayLarge
                            )
                            Text(
                                text = "Todavía no tenés medicaciones para tus mascotas",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Tocá el + para agregar la primera",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    is MedicationUiState.Success -> {
                        val medicationsFiltradas = when(filtroSeleccionado) {
                            null -> state.medications
                            MedicationStatus.ACTIVO -> state.medications.filter {
                                it.status == MedicationStatus.ACTIVO
                            }

                            MedicationStatus.FINALIZADO -> state.medications.filter {
                                it.status == MedicationStatus.FINALIZADO
                            }
                        }

                        LazyColumn(contentPadding = PaddingValues(bottom = 80.dp)) {
                            items(
                                items = medicationsFiltradas,
                                key = {it.id}
                            ) { medication ->
                                MedicationCard(
                                    medication = medication,
                                    onClick = {},
                                    onDeleteClick = { viewModel.deleteMedication(medication)}
                                )
                            }
                        }
                    }
                    is MedicationUiState.Error -> {
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