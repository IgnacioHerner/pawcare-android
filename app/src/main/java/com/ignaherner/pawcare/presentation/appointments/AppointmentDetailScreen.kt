package com.ignaherner.pawcare.presentation.appointments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ignaherner.pawcare.domain.model.Appointment
import com.ignaherner.pawcare.domain.model.AppointmentStatus
import com.ignaherner.pawcare.domain.model.toFriendlyDate
import com.ignaherner.pawcare.presentation.components.InfoRow

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
                title = { Text("Detalle del turno") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToEdit(appointmentId)}) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar turno")
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
            when (val state = detailState) {
                is AppointmentDetailState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is AppointmentDetailState.Error -> {
                    Text(
                        text = state.mensaje,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is AppointmentDetailState.Success -> {
                    AppointmentDetailContent(
                        appoinment = state.appointments,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun AppointmentDetailContent(
    appoinment: Appointment,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = appoinment.fecha,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            AssistChip(
                onClick = {},
                label = {
                    Text(
                        text = when (appoinment.status) {
                            AppointmentStatus.PENDIENTE -> "Pendiente"
                            AppointmentStatus.AGENDADO -> "Agendado"
                            AppointmentStatus.REALIZADO -> "Realizado"
                        },
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = when (appoinment.status) {
                        AppointmentStatus.PENDIENTE -> Color(0xFFFF9800).copy(alpha = 0.15f)
                        AppointmentStatus.AGENDADO -> Color(0xFF2196F3).copy(alpha = 0.15f)
                        AppointmentStatus.REALIZADO -> Color(0xFF4CAF50).copy(alpha = 0.15f)
                    },
                    labelColor = when (appoinment.status) {
                        AppointmentStatus.PENDIENTE -> Color(0xFFFF9800)
                        AppointmentStatus.AGENDADO -> Color(0xFF2196F3)
                        AppointmentStatus.REALIZADO -> Color(0xFF4CAF50)
                    }
                )
            )
        }
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Información",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                InfoRow("📅 Fecha", appoinment.fecha.toFriendlyDate())

                appoinment.veterinario?.let {
                    InfoRow("👨‍⚕️ Veterinario", it)
                }

                appoinment.motivo?.let {
                    InfoRow("📋 Motivo", it)
                }

                appoinment.notas?.let {
                    InfoRow("📝 Notas", it)
                }

            }
        }
    }
}