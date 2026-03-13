package com.ignaherner.pawcare.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.NoOpUpdate
import com.ignaherner.pawcare.domain.model.Appointment
import com.ignaherner.pawcare.domain.model.AppointmentStatus
import com.ignaherner.pawcare.ui.theme.PawCareTheme

@Composable
fun AppointmentCard(
    appointment: Appointment,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = appointment.fecha,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = appointment.status.displayName,
                        style = MaterialTheme.typography.bodySmall,
                        color = when (appointment.status) {
                            AppointmentStatus.PENDIENTE -> Color(0xFFFF9800)
                            AppointmentStatus.AGENDADO -> Color(0xFF2196F3)
                            AppointmentStatus.REALIZADO -> Color(0xFF4CAF50)
                        }
                    )
                }
                appointment.veterinario?.let {
                    Text(
                        text = "Dr/a: $it",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                appointment.motivo?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                appointment.notas?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar turno"
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
fun AppoinmentCardPrevie() {
    PawCareTheme {
        AppointmentCard(
            appointment = Appointment(
                id = 1,
                petId = 1,
                fecha = "13-03-2026",
                veterinario = "Agustina Ochoa",
                motivo = "Urgencia",
                notas = "Atropello",
                status = AppointmentStatus.PENDIENTE
            ),
            onClick = {},
            onDeleteClick = {}
        )
    }
}