package com.ignaherner.pawcare.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ignaherner.pawcare.R
import com.ignaherner.pawcare.domain.model.Pet
import com.ignaherner.pawcare.presentation.components.AlertBanner
import com.ignaherner.pawcare.presentation.components.AlertTone
import com.ignaherner.pawcare.presentation.components.ConfirmDeleteDialog
import com.ignaherner.pawcare.presentation.components.EmptyState
import com.ignaherner.pawcare.presentation.components.HomeScreenSkeleton
import com.ignaherner.pawcare.presentation.components.PawCareIcon
import com.ignaherner.pawcare.presentation.components.PawIconSize
import com.ignaherner.pawcare.presentation.components.PetSummaryCard
import com.ignaherner.pawcare.presentation.components.SwipeRevealCard
import com.ignaherner.pawcare.presentation.owners.OwnerState
import com.ignaherner.pawcare.presentation.owners.OwnerViewModel
import com.ignaherner.pawcare.ui.theme.PawRadii
import com.ignaherner.pawcare.ui.theme.PawSpace

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToPetDetail: (Long) -> Unit,
    onNavigateToAddPet: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToOwnerDetail: () -> Unit,
    bottomPadding: Dp = 0.dp,
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

    val subtitulo = when (val state = uiState) {
        is HomeUiState.Success -> {
            val alertas = viewModel.calcularAlertas(state.summaries)
            when {
                alertas.isEmpty() && state.summaries.size == 1 ->
                    "${state.summaries.first().pet.nombre} está al día"
                alertas.isEmpty() ->
                    "Tus ${state.summaries.size} mascotas están al día"
                state.summaries.size == 1 ->
                    "${state.summaries.first().pet.nombre} necesita atención"
                else ->
                    "${state.summaries.size} mascotas, ${alertas.size} pendientes"
            }
        }
        else -> ""
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
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(PawSpace.sm)
                    ) {
                        // Logo cuadrado verde con la patita
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(PawRadii.xs))
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_paw),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Text(
                            text = "PawCare",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO notificaciones */ }) {
                        PawCareIcon(
                            icon = Icons.Outlined.Notifications,
                            contentDescription = "Notificaciones",
                            size = PawIconSize.medium
                        )
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        PawCareIcon(
                            icon = Icons.Outlined.Settings,
                            contentDescription = "Configuración",
                            size = PawIconSize.medium
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        when (val state = uiState) {
            is HomeUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    HomeScreenSkeleton()
                }
            }
            is HomeUiState.Empty -> {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)) {
                    EmptyState(
                        icon = Icons.Outlined.Pets,
                        title = "Sin mascotas todavía",
                        body = "Agregá tu primera mascota para empezar a llevar su salud al día"
                    )
                }
            }
            is HomeUiState.Success -> {
                val alertas = remember(state.summaries) {
                    viewModel.calcularAlertas(state.summaries)
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(
                        bottom = bottomPadding + 96.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(PawSpace.md)
                ) {
                    // Saludo
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = PawSpace.lg),
                            verticalArrangement = Arrangement.spacedBy(PawSpace.xs)
                        ) {
                            Text(
                                text = "Hola, $nombreUsuario",
                                style = MaterialTheme.typography.displayMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = subtitulo,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Alertas
                    if (alertas.isNotEmpty()) {
                        item {
                            Column(
                                modifier = Modifier.padding(horizontal = PawSpace.lg),
                                verticalArrangement = Arrangement.spacedBy(PawSpace.sm)
                            ) {
                                alertas.forEach { alerta ->
                                    AlertBanner(
                                        icon = when (alerta.tipo) {
                                            AlertType.VACUNA_VENCIDA -> Icons.Outlined.Warning
                                            AlertType.VACUNA_PROXIMA -> Icons.Outlined.Notifications
                                            AlertType.MEDICAMENTO_ACTIVO -> Icons.Outlined.Medication
                                        },
                                        title = when (alerta.tipo) {
                                            AlertType.VACUNA_VENCIDA -> "1 vacuna vencida"
                                            AlertType.VACUNA_PROXIMA -> "1 recordatorio próximo"
                                            AlertType.MEDICAMENTO_ACTIVO -> "Tratamiento en curso"
                                        },
                                        body = alerta.descripcion,
                                        tone = when (alerta.tipo) {
                                            AlertType.VACUNA_VENCIDA -> AlertTone.DANGER
                                            AlertType.VACUNA_PROXIMA -> AlertTone.WARN
                                            AlertType.MEDICAMENTO_ACTIVO -> AlertTone.INFO
                                        },
                                        onClick = { /* TODO ir al detalle */ }
                                    )
                                }
                            }
                        }
                    }

                    // Header "Mascotas"
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = PawSpace.lg),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Mascotas",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }

                    // Lista de mascotas
                    items(
                        items = state.summaries,
                        key = { it.pet.id }
                    ) { summary ->
                        val estado = remember(summary) {
                            viewModel.calcularEstadoMascota(summary)
                        }
                        SwipeRevealCard(
                            onDelete = { petToDelete = summary.pet },
                            onEdit = { onNavigateToEdit(summary.pet.id) }
                        ) {
                            PetSummaryCard(
                                summary = summary,
                                estado = estado,
                                vacunaCount = summary.totalVacunas,
                                medicamentoCount = summary.totalMedicamentos,
                                onClick = { onNavigateToPetDetail(summary.pet.id) }
                            )
                        }
                    }
                }
            }
            is HomeUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = state.mensaje)
                }
            }
        }
    }
}
