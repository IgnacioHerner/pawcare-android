package com.ignaherner.pawcare.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ignaherner.pawcare.domain.model.Medication
import com.ignaherner.pawcare.domain.model.MedicationStatus
import com.ignaherner.pawcare.domain.model.calcularDiaActual
import com.ignaherner.pawcare.domain.model.calcularFechaFin
import com.ignaherner.pawcare.domain.model.toFriendlyDate
import com.ignaherner.pawcare.ui.theme.PawCareTheme

@Composable
fun MedicationCard(
    medication: Medication,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
){
    val isActivo = medication.status == MedicationStatus.ACTIVO

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActivo)
                Color(0xFF4CAF50).copy(alpha = 0.05f)
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header - nombre + status chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = medication.nombre,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            text = if (isActivo) "En curso" else "Finalizado",
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (isActivo)
                            Color(0xFF4CAFF50).copy(alpha = 0.15f)
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                        labelColor = if (isActivo)
                            Color(0xFF4CAF50)
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            // Dosis e intervalo
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("\uD83D\uDC8A", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = "Dosis: ${medication.dosis}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⏱", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = "Cada ${medication.intervaloHoras}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Fechas
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${medication.fechaInicio.toFriendlyDate()} → " +
                            "Fin: ${calcularFechaFin(medication.fechaInicio, medication.duracionDias).toFriendlyDate()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Progreso - solo si está activo
            if (isActivo) {
                Text(
                    text = calcularDiaActual(medication.fechaInicio, medication.duracionDias),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Medium
                )
            }

            // Recetado por
            medication.recetadoPor?.let {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("👨‍⚕️", style = MaterialTheme.typography.bodySmall)
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // notas
            medication.notas?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }

            // Boton eliminar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onDeleteClick,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Eliminar")
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
fun MedicationCardPreview(){
    PawCareTheme {
        MedicationCard(
            medication = Medication (
                id = 1,
                petId = 1,
                nombre = "Corticoides",
                fechaInicio = "14-03-2026",
                duracionDias = 4,
                intervaloHoras = 12,
                recetadoPor = "Agustina Ochoa",
                dosis = "1/2",
                notas = "Alergia",
                status = MedicationStatus.ACTIVO
            ),
            onClick = {},
            onDeleteClick = {}
        )
    }
}