package com.ignaherner.pawcare.presentation.vaccines

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ignaherner.pawcare.domain.model.Vaccine
import com.ignaherner.pawcare.domain.model.VaccineStatus
import com.ignaherner.pawcare.presentation.components.ConfirmDeleteDialog
import com.ignaherner.pawcare.presentation.components.SwipeRevealCard
import com.ignaherner.pawcare.presentation.components.VaccineCard
import com.ignaherner.pawcare.presentation.pets.PetDetailState
import com.ignaherner.pawcare.presentation.pets.PetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaccineScreen(
    petId: Long,
    petName: String,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onNavigateToForm: () -> Unit,
    viewModel: VaccineViewModel = hiltViewModel(),
    petViewModel: PetViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val detailState by petViewModel.detailState.collectAsStateWithLifecycle()

    var filtroSeleccionado by remember { mutableStateOf<VaccineStatus?>(null) }

    // Para el AlertDialog
    var vaccineToDelete by remember { mutableStateOf<Vaccine?>(null) }


    vaccineToDelete?.let { vaccine ->
        ConfirmDeleteDialog(
            titulo = "¿Eliminar a ${vaccine.nombre}?",
            mensaje = "Esta accion no se puede deshacer. ",
            onConfirm = {
                viewModel.deleteVaccine(vaccine)
                vaccineToDelete = null
            },
            onDismiss = { vaccineToDelete = null}
        )
    }

    LaunchedEffect(petId) {
        viewModel.loadVaccines(petId)
        petViewModel.loadPetById(petId)
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()

    LaunchedEffect(snackbarMessage) {
        android.util.Log.d("PawCare", "LaunchedEffect triggered: $snackbarMessage")
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                            text = "Vacunas 💉",
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
                Icon(Icons.Default.Add, contentDescription = "Agregar vacuna")
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
                    // Chip "Todas"
                    FilterChip(
                        selected = filtroSeleccionado == null,
                        onClick = { filtroSeleccionado = null},
                        label = { Text("Todas")}
                    )
                }
                item{
                    FilterChip(
                        selected = filtroSeleccionado is VaccineStatus.Pendiente,
                        onClick = { filtroSeleccionado = VaccineStatus.Pendiente},
                        label = { Text("Pendiente")},
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFF44336).copy(alpha = 0.15f),
                            selectedLabelColor = Color(0xFFF44336)
                        )
                    )
                }

                item {
                    FilterChip(
                        selected = filtroSeleccionado is VaccineStatus.Programada,
                        onClick = { filtroSeleccionado = VaccineStatus.Programada("")},
                        label = { Text("Programada")},
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFF57F17).copy(alpha = 0.15f),
                            selectedLabelColor = Color(0xFFF57F17)
                        )
                    )
                }

                item {
                    FilterChip(
                        selected = filtroSeleccionado is VaccineStatus.Aplicada,
                        onClick = { filtroSeleccionado = VaccineStatus.Aplicada("") },
                        label = { Text("Aplicada") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF4CAF50).copy(alpha = 0.15f),
                            selectedLabelColor = Color(0xFF4CAF50)
                        )
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                when (val state = uiState) {
                    is VaccineUiState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    is VaccineUiState.Empty -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "\uD83D\uDC89",
                                style = MaterialTheme.typography.displayLarge
                            )
                            Text(
                                text = "Todavía no tenés vacnas para tus mascotas",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Tocá el + para agregar la primera",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    is VaccineUiState.Success -> {
                        // Filtar segun el chip seleccionado
                        val vaccinesFiltradas = when (val filtro = filtroSeleccionado) {
                            null -> state.vaccines
                            is VaccineStatus.Pendiente -> state.vaccines.filter {
                                it.status is VaccineStatus.Pendiente
                            }
                            is VaccineStatus.Programada -> state.vaccines.filter {
                                it.status is VaccineStatus.Programada
                            }
                            is VaccineStatus.Aplicada -> state.vaccines.filter {
                                it.status is VaccineStatus.Aplicada
                            }
                        }

                        // Ordenar - pendiente, programada, aplicada
                        val vaccinesOrdenadas = vaccinesFiltradas.sortedBy {
                            when(it.status) {
                                is VaccineStatus.Pendiente -> 0
                                is VaccineStatus.Programada -> 1
                                is VaccineStatus.Aplicada -> 2
                            }
                        }
                        LazyColumn(contentPadding = PaddingValues(bottom = 80.dp)) {
                            items(
                                items = vaccinesOrdenadas,
                                key = { it.id }
                            ) { vaccine ->
                                SwipeRevealCard(
                                    onDelete = { vaccineToDelete = vaccine},
                                    onEdit = { onNavigateToEdit(vaccine.id)}
                                ) {
                                    VaccineCard(
                                        vaccine = vaccine,
                                        onClick = { },
                                        onDeleteClick = { viewModel.deleteVaccine(vaccine) }
                                    )
                                }
                            }
                        }
                    }
                    is VaccineUiState.Error -> {
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
