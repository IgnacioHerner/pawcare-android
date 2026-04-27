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
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.Vaccines
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
import androidx.compose.material3.TopAppBarDefaults
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
import com.ignaherner.pawcare.presentation.components.EmptyState
import com.ignaherner.pawcare.presentation.components.SwipeRevealCard
import com.ignaherner.pawcare.presentation.components.VaccineCard
import com.ignaherner.pawcare.presentation.pets.PetDetailState
import com.ignaherner.pawcare.presentation.pets.PetViewModel
import com.ignaherner.pawcare.ui.theme.Danger
import com.ignaherner.pawcare.ui.theme.DangerSoft
import com.ignaherner.pawcare.ui.theme.Info
import com.ignaherner.pawcare.ui.theme.InfoSoft
import com.ignaherner.pawcare.ui.theme.PawSpace
import com.ignaherner.pawcare.ui.theme.Success
import com.ignaherner.pawcare.ui.theme.SuccessSoft

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaccineScreen(
    petId: Long,
    petName: String,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onNavigateToForm: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    viewModel: VaccineViewModel = hiltViewModel(),
    petViewModel: PetViewModel = hiltViewModel(),
    isVeterinario: Boolean = false
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val detailState by petViewModel.detailState.collectAsStateWithLifecycle()

    // Filtro: null = todas, o un VaccineStatus específico
    var filtroSeleccionado by remember { mutableStateOf<VaccineStatus?>(null) }

    var vaccineToDelete by remember { mutableStateOf<Vaccine?>(null) }

    vaccineToDelete?.let { vaccine ->
        ConfirmDeleteDialog(
            titulo = "¿Eliminar ${vaccine.tipo.displayName}?",
            mensaje = "Esta acción no se puede deshacer.",
            onConfirm = {
                viewModel.deleteVaccine(vaccine)
                vaccineToDelete = null
            },
            onDismiss = { vaccineToDelete = null }
        )
    }

    LaunchedEffect(petId) {
        viewModel.loadVaccines(petId)
        petViewModel.loadPetById(petId)
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(message = it, duration = SnackbarDuration.Short)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        val titulo = when (val state = detailState) {
                            is PetDetailState.Success -> state.pet.nombre
                            else -> ""
                        }
                        Text(
                            text = titulo,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Vacunas",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            if (isVeterinario) {
                FloatingActionButton(
                    onClick = onNavigateToForm,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar vacuna")
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filtros
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = PawSpace.lg, vertical = PawSpace.sm),
                horizontalArrangement = Arrangement.spacedBy(PawSpace.sm)
            ) {
                item {
                    FilterChip(
                        selected = filtroSeleccionado == null,
                        onClick = { filtroSeleccionado = null },
                        label = { Text("Todas") }
                    )
                }
                item {
                    FilterChip(
                        selected = filtroSeleccionado is VaccineStatus.Aplicada,
                        onClick = { filtroSeleccionado = VaccineStatus.Aplicada },
                        label = { Text("Aplicadas") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SuccessSoft,
                            selectedLabelColor = Success
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = filtroSeleccionado is VaccineStatus.Programada,
                        onClick = { filtroSeleccionado = VaccineStatus.Programada },
                        label = { Text("Programadas") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = InfoSoft,
                            selectedLabelColor = Info
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = filtroSeleccionado is VaccineStatus.Vencida,
                        onClick = { filtroSeleccionado = VaccineStatus.Vencida },
                        label = { Text("Vencidas") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DangerSoft,
                            selectedLabelColor = Danger
                        )
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when (val state = uiState) {
                    is VaccineUiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    is VaccineUiState.Empty -> {
                        EmptyState(
                            icon = Icons.Outlined.Vaccines,
                            title = "Sin vacunas todavía",
                            body = if (isVeterinario)
                                "Tocá el + para agregar la primera vacuna"
                            else
                                "El veterinario podrá registrar las vacunas de tu mascota"
                        )
                    }
                    is VaccineUiState.Success -> {
                        // Filtrar
                        val vaccinesFiltradas = when (filtroSeleccionado) {
                            null -> state.vaccines
                            is VaccineStatus.Aplicada -> state.vaccines.filter {
                                it.status is VaccineStatus.Aplicada
                            }
                            is VaccineStatus.Programada -> state.vaccines.filter {
                                it.status is VaccineStatus.Programada
                            }
                            is VaccineStatus.Vencida -> state.vaccines.filter {
                                it.status is VaccineStatus.Vencida
                            }
                        }

                        // Ordenar: vencidas primero, luego programadas, luego aplicadas
                        val vaccinesOrdenadas = vaccinesFiltradas.sortedBy {
                            when (it.status) {
                                is VaccineStatus.Vencida -> 0
                                is VaccineStatus.Programada -> 1
                                is VaccineStatus.Aplicada -> 2
                            }
                        }

                        if (vaccinesOrdenadas.isEmpty()) {
                            EmptyState(
                                icon = Icons.Outlined.FilterAlt,
                                title = "Sin resultados",
                                body = "No hay vacunas con el filtro seleccionado"
                            )
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(
                                    top = PawSpace.sm,
                                    bottom = 96.dp
                                )
                            ) {
                                items(
                                    items = vaccinesOrdenadas,
                                    key = { it.id }
                                ) { vaccine ->
                                    if (isVeterinario) {
                                        SwipeRevealCard(
                                            onDelete = { vaccineToDelete = vaccine },
                                            onEdit = { onNavigateToEdit(vaccine.id) }
                                        ) {
                                            VaccineCard(
                                                vaccine = vaccine,
                                                onClick = { onNavigateToDetail(vaccine.id) },
                                                onDeleteClick = {}
                                            )
                                        }
                                    } else {
                                        VaccineCard(
                                            vaccine = vaccine,
                                            onClick = { onNavigateToDetail(vaccine.id) },
                                            onDeleteClick = {}
                                        )
                                    }
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