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
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
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
import com.ignaherner.pawcare.domain.model.Vaccine
import com.ignaherner.pawcare.domain.model.VaccineStatus
import com.ignaherner.pawcare.domain.model.color
import com.ignaherner.pawcare.domain.model.displayName
import com.ignaherner.pawcare.domain.model.toFriendlyDate
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
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header - nombre + status chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = vaccine.nombre,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            text = vaccine.status.displayName(),
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = when (vaccine.status) {
                            is VaccineStatus.Aplicada -> Color(0xFF4CAF50).copy(alpha = 0.15f)
                            is VaccineStatus.Programada -> Color(0xFFFFEB3B).copy(alpha = 0.15f)
                            is VaccineStatus.Pendiente -> Color(0xFFF44336).copy(alpha = 0.15f)

                        },
                        labelColor = when (vaccine.status) {
                            is VaccineStatus.Aplicada -> Color(0xFF4CAF50)
                            is VaccineStatus.Programada -> Color(0xFFF57F17)
                            is VaccineStatus.Pendiente  -> Color(0xFFF44336)
                        }
                    )
                )
            }

            // Fecha aplicacion
            vaccine.fecha?.let {
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
                        text = it.toFriendlyDate(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Próxima dosis
            vaccine.proximaDosis?.let {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = "Próxima: ${it.toFriendlyDate()}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            // Veterinario
            vaccine.veterinario?.let {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "\uD83D\uDC68\u200D⚕\uFE0F",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
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