package com.ignaherner.mispatitas.presentation.appointments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Notes
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ignaherner.mispatitas.domain.model.Appointment
import com.ignaherner.mispatitas.presentation.components.PawCard
import com.ignaherner.mispatitas.utils.toFriendlyDate
import com.ignaherner.mispatitas.presentation.components.PawCareIcon
import com.ignaherner.mispatitas.presentation.components.PawIconSize
import com.ignaherner.mispatitas.ui.theme.PawRadio
import com.ignaherner.mispatitas.ui.theme.PawSpace

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentDetailScreen(
    appointmentId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    viewModel: AppointmentViewModel = hiltViewModel()
) {
    val detailState by viewModel.appointentDetailState.collectAsStateWithLifecycle()

    LaunchedEffect(appointmentId) {
        viewModel.loadAppointmentById(appointmentId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de visita") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        PawCareIcon(
                            icon = Icons.Outlined.ArrowBack,
                            contentDescription = "Volver",
                            size = PawIconSize.medium
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToEdit(appointmentId) }) {
                        PawCareIcon(
                            icon = Icons.Outlined.Edit,
                            contentDescription = "Editar",
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
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = detailState) {
                is AppointmentDetailState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is AppointmentDetailState.Error -> {
                    Text(
                        text = state.mensaje,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is AppointmentDetailState.Success -> {
                    AppointmentDetailContent(
                        appointment = state.appointments,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun AppointmentDetailContent(
    appointment: Appointment,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(PawSpace.lg),
        verticalArrangement = Arrangement.spacedBy(PawSpace.lg)
    ) {
        // Header
        Column(verticalArrangement = Arrangement.spacedBy(PawSpace.sm)) {
            Text(
                text = appointment.motivo,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = appointment.fecha.toFriendlyDate(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Info card
        PawCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(PawSpace.lg),
                verticalArrangement = Arrangement.spacedBy(PawSpace.md)
            ) {
                Text(
                    text = "INFORMACIÓN",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                DetailRow(
                    icon = Icons.Outlined.CalendarMonth,
                    label = "Fecha",
                    value = appointment.fecha.toFriendlyDate()
                )

                DetailRow(
                    icon = Icons.Outlined.Description,
                    label = "Motivo",
                    value = appointment.motivo
                )

                appointment.veterinario?.let {
                    DetailRow(
                        icon = Icons.Outlined.Person,
                        label = "Veterinario",
                        value = it
                    )
                }

                appointment.clinica?.let {
                    DetailRow(
                        icon = Icons.Outlined.LocalHospital,
                        label = "Clínica",
                        value = it
                    )
                }

                appointment.diagnostico?.let {
                    DetailRow(
                        icon = Icons.Outlined.MedicalServices,
                        label = "Diagnóstico",
                        value = it
                    )
                }

                appointment.notas?.let {
                    DetailRow(
                        icon = Icons.Outlined.Notes,
                        label = "Notas",
                        value = it
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(PawSpace.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PawCareIcon(
            icon = icon,
            contentDescription = null,
            size = PawIconSize.default,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
