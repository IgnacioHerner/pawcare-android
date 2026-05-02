package com.ignaherner.pawcare.presentation.appointments

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ignaherner.pawcare.domain.model.Appointment
import com.ignaherner.pawcare.presentation.components.ConfirmDeleteDialog
import com.ignaherner.pawcare.presentation.components.EmptyState
import com.ignaherner.pawcare.presentation.components.PawCard
import com.ignaherner.pawcare.presentation.components.PawCareIcon
import com.ignaherner.pawcare.presentation.components.PawIconSize
import com.ignaherner.pawcare.presentation.components.SwipeRevealCard
import com.ignaherner.pawcare.presentation.pets.PetDetailState
import com.ignaherner.pawcare.presentation.pets.PetViewModel
import com.ignaherner.pawcare.ui.theme.CatAppointment
import com.ignaherner.pawcare.ui.theme.CatAppointmentSoft
import com.ignaherner.pawcare.ui.theme.PawRadii
import com.ignaherner.pawcare.ui.theme.PawSpace
import com.ignaherner.pawcare.utils.toFriendlyDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentScreen(
    petId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onNavigateToForm: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    isVeterinario: Boolean = false,
    viewModel: AppointmentViewModel = hiltViewModel(),
    petViewModel: PetViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val detailState by petViewModel.detailState.collectAsStateWithLifecycle()
    var appointmentToDelete by remember { mutableStateOf<Appointment?>(null) }

    LaunchedEffect(petId) {
        viewModel.loadAppointments(petId)
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

    appointmentToDelete?.let { appointment ->
        ConfirmDeleteDialog(
            titulo = "¿Eliminar visita?",
            mensaje = "Se eliminará el registro de ${appointment.motivo}. Esta acción no se puede deshacer.",
            onConfirm = {
                viewModel.deleteAppointment(appointment)
                appointmentToDelete = null
            },
            onDismiss = { appointmentToDelete = null }
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
                            text = "Visitas",
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
                    Icon(Icons.Default.Add, contentDescription = "Agregar visita")
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is AppointmentUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is AppointmentUiState.Empty -> {
                    EmptyState(
                        icon = Icons.Outlined.CalendarMonth,
                        title = "Sin visitas registradas",
                        body = if (isVeterinario)
                            "Tocá el + para registrar una visita"
                        else
                            "El veterinario podrá registrar las visitas de tu mascota"
                    )
                }
                is AppointmentUiState.Success -> {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            top = PawSpace.sm,
                            bottom = 96.dp
                        )
                    ) {
                        items(
                            items = state.appointments,
                            key = { it.id }
                        ) { appointment ->
                            if (isVeterinario) {
                                SwipeRevealCard(
                                    onDelete = { appointmentToDelete = appointment },
                                    onEdit = { onNavigateToEdit(appointment.id) }
                                ) {
                                    AppointmentCard(
                                        appointment = appointment,
                                        onClick = { onNavigateToDetail(appointment.id) }
                                    )
                                }
                            } else {
                                AppointmentCard(
                                    appointment = appointment,
                                    onClick = { onNavigateToDetail(appointment.id) }
                                )
                            }
                        }
                    }
                }
                is AppointmentUiState.Error -> {
                    Text(
                        text = state.mensaje,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
private fun AppointmentCard(
    appointment: Appointment,
    onClick: () -> Unit = {}
) {
    PawCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PawSpace.lg, vertical = PawSpace.xs),
        onClick = onClick
    )  {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PawSpace.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(PawSpace.md)
        ) {
            // Tile ícono
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(PawRadii.sm))
                    .background(CatAppointmentSoft),
                contentAlignment = Alignment.Center
            ) {
                PawCareIcon(
                    icon = Icons.Outlined.CalendarMonth,
                    contentDescription = null,
                    size = PawIconSize.medium,
                    tint = CatAppointment
                )
            }

            // Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = appointment.motivo,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = appointment.fecha.toFriendlyDate(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                appointment.veterinario?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            PawCareIcon(
                icon = Icons.Outlined.ChevronRight,
                contentDescription = null,
                size = PawIconSize.medium,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}