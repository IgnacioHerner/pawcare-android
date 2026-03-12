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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ignaherner.pawcare.domain.model.Vaccine
import com.ignaherner.pawcare.domain.model.VaccineStatus
import com.ignaherner.pawcare.domain.model.color
import com.ignaherner.pawcare.domain.model.displayName
import com.ignaherner.pawcare.ui.theme.PawCareTheme

@Composable
fun VaccineCard(
    vaccine: Vaccine,
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
                // Nombre y status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = vaccine.nombre,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = vaccine.status.displayName(),
                        style = MaterialTheme.typography.bodySmall,
                        color = vaccine.status.color()
                    )
                }

                // Fecha
                vaccine.fecha?.let {
                    Text(
                        text = "Fecha: $it",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Proxima dosis
                vaccine.proximaDosis?.let {
                    Text(
                        text = "Proxima dosis: $it",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Veterianario
                vaccine.veterinario?.let {
                    Text(
                        text = "Dr/a: $it",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Notas
                vaccine.notas?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar ${vaccine.nombre}"
                )
            }
        }
    }
}


@PreviewLightDark
@Composable
fun VaccineCardPreview() {
    PawCareTheme {
        VaccineCard(
            vaccine = Vaccine(
                id = 1,
                petId = 1,
                nombre = "Antirrábica",
                fecha = "2024-01-15",
                proximaDosis = "2025-01-15",
                veterinario = "Dr. García",
                notas = "Sin reacciones adversas",
                status = VaccineStatus.Aplicada("2024-01-15")
            ),
            onClick = {},
            onDeleteClick = {}
        )
    }
}