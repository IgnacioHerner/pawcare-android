package com.ignaherner.pawcare.presentation.condition

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ignaherner.pawcare.utils.toFriendlyDate
import com.ignaherner.pawcare.domain.model.Condition
import com.ignaherner.pawcare.domain.model.ConditionEstado
import com.ignaherner.pawcare.domain.model.Severidad
import com.ignaherner.pawcare.presentation.components.ConfirmDeleteDialog
import com.ignaherner.pawcare.presentation.components.EmptyState
import com.ignaherner.pawcare.presentation.components.PawCard
import com.ignaherner.pawcare.presentation.components.PawCareIcon
import com.ignaherner.pawcare.presentation.components.PawIconSize
import com.ignaherner.pawcare.presentation.components.SwipeRevealCard
import com.ignaherner.pawcare.presentation.pets.PetDetailState
import com.ignaherner.pawcare.presentation.pets.PetViewModel
import com.ignaherner.pawcare.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConditionScreen(
    petId: Long,
    petName: String,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onNavigateToForm: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    isVeterinario: Boolean = false,
    viewModel: ConditionViewModel = hiltViewModel(),
    petViewModel: PetViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()
    var conditionToDelete by remember { mutableStateOf<Condition?>(null) }
    var filtroSeleccionado by remember { mutableStateOf<ConditionEstado?>(null) }

    val detailState by petViewModel.detailState.collectAsStateWithLifecycle()

    LaunchedEffect(petId) {
        viewModel.loadConditions(petId)
        petViewModel.loadPetById(petId)
    }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
            viewModel.clearSnackbar()
        }
    }

    conditionToDelete?.let { condition ->
        ConfirmDeleteDialog(
            titulo = "¿Eliminar ${condition.nombre}?",
            mensaje = "Esta acción no se puede deshacer.",
            onConfirm = {
                viewModel.deleteCondition(condition)
                conditionToDelete = null
            },
            onDismiss = { conditionToDelete = null }
        )
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
                            text = "Condiciones",
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
                    Icon(Icons.Default.Add, contentDescription = "Agregar condición")
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
                        selected = filtroSeleccionado == ConditionEstado.ACTIVA,
                        onClick = { filtroSeleccionado = ConditionEstado.ACTIVA },
                        label = { Text("Activas") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DangerSoft,
                            selectedLabelColor = Danger
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = filtroSeleccionado == ConditionEstado.EN_TRATAMIENTO,
                        onClick = { filtroSeleccionado = ConditionEstado.EN_TRATAMIENTO },
                        label = { Text("En tratamiento") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = WarnSoft,
                            selectedLabelColor = Warn
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = filtroSeleccionado == ConditionEstado.RESUELTA,
                        onClick = { filtroSeleccionado = ConditionEstado.RESUELTA },
                        label = { Text("Resueltas") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SuccessSoft,
                            selectedLabelColor = Success
                        )
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when (val state = uiState) {
                    is ConditionUiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    is ConditionUiState.Empty -> {
                        EmptyState(
                            icon = Icons.Outlined.HealthAndSafety,
                            title = "Sin condiciones registradas",
                            body = if (isVeterinario)
                                "Tocá el + para registrar una condición"
                            else
                                "El veterinario podrá registrar condiciones de tu mascota"
                        )
                    }
                    is ConditionUiState.Success -> {
                        val conditionsFiltradas = when (filtroSeleccionado) {
                            null -> state.conditions
                            else -> state.conditions.filter { it.estado == filtroSeleccionado }
                        }

                        if (conditionsFiltradas.isEmpty()) {
                            EmptyState(
                                icon = Icons.Outlined.FilterAlt,
                                title = "Sin resultados",
                                body = "No hay condiciones con el filtro seleccionado"
                            )
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(
                                    top = PawSpace.sm,
                                    bottom = 96.dp
                                )
                            ) {
                                items(
                                    items = conditionsFiltradas,
                                    key = { it.id }
                                ) { condition ->
                                    if (isVeterinario) {
                                        SwipeRevealCard(
                                            onDelete = { conditionToDelete = condition },
                                            onEdit = { onNavigateToEdit(condition.id) }
                                        ) {
                                            ConditionCard(
                                                condition = condition,
                                                onClick = { onNavigateToDetail(condition.id)}
                                            )
                                        }
                                    } else {
                                        ConditionCard(
                                            condition = condition,
                                            onClick = { onNavigateToDetail(condition.id)}
                                        )
                                    }
                                }
                            }
                        }
                    }
                    is ConditionUiState.Error -> {
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

@Composable
private fun ConditionCard(
    condition: Condition,
    onClick: () -> Unit = {}
) {
    val (severidadBg, severidadFg) = when (condition.severidad) {
        Severidad.LEVE -> SuccessSoft to Success
        Severidad.MODERADA -> WarnSoft to Warn
        Severidad.GRAVE -> DangerSoft to Danger
    }

    val (estadoBg, estadoFg) = when (condition.estado) {
        ConditionEstado.ACTIVA -> DangerSoft to Danger
        ConditionEstado.EN_TRATAMIENTO -> WarnSoft to Warn
        ConditionEstado.RESUELTA -> SuccessSoft to Success
    }

    PawCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PawSpace.lg, vertical = PawSpace.xs),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PawSpace.md),
            verticalArrangement = Arrangement.spacedBy(PawSpace.sm)
        ) {
            // Header — nombre + severidad
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(PawSpace.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(PawRadio.sm))
                        .background(severidadBg),
                    contentAlignment = Alignment.Center
                ) {
                    PawCareIcon(
                        icon = Icons.Outlined.HealthAndSafety,
                        contentDescription = null,
                        size = PawIconSize.medium,
                        tint = severidadFg
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = condition.nombre,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = condition.fechaDiagnostico.toFriendlyDate(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Estado pill
                Surface(
                    shape = RoundedCornerShape(PawRadio.xs),
                    color = estadoBg
                ) {
                    Text(
                        text = condition.estado.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = estadoFg,
                        modifier = Modifier.padding(horizontal = PawSpace.sm, vertical = 4.dp)
                    )
                }
            }

            // Severidad pill
            Row(
                horizontalArrangement = Arrangement.spacedBy(PawSpace.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PawCareIcon(
                    icon = Icons.Outlined.Warning,
                    contentDescription = null,
                    size = PawIconSize.small,
                    tint = severidadFg
                )
                Text(
                    text = "Severidad: ${condition.severidad.displayName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = severidadFg
                )
            }

            // Veterinario
            condition.veterinario?.let {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(PawSpace.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PawCareIcon(
                        icon = Icons.Outlined.Person,
                        contentDescription = null,
                        size = PawIconSize.small,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Notas
            condition.notas?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}