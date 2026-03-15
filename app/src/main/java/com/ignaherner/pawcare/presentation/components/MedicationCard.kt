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
import com.ignaherner.pawcare.domain.model.Medication
import com.ignaherner.pawcare.domain.model.MedicationStatus
import com.ignaherner.pawcare.ui.theme.PawCareTheme

@Composable
fun MedicationCard(
    medication: Medication,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
){
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
                        text = medication.nombre,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = medication.status.displayName,
                        style = MaterialTheme.typography.bodySmall,
                        color = when (medication.status) {
                            MedicationStatus.ACTIVO -> Color(0xFF4CAF50)
                            MedicationStatus.FINALIZADO -> Color(0xFF9E9E9E)
                        }
                    )
                }
                Text(
                    text = "Dosis: ${medication.dosis}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Cada ${medication.intervaloHoras} horas",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = medication.fechaInicio,
                    style = MaterialTheme.typography.bodySmall
                )

                medication.notas?.let {
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
                dosis = "1/2",
                notas = "Alergia",
                status = MedicationStatus.ACTIVO
            ),
            onClick = {},
            onDeleteClick = {}
        )
    }
}