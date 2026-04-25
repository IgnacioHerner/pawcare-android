package com.ignaherner.pawcare.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.Monitor
import androidx.compose.material.icons.outlined.Vaccines
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.ignaherner.pawcare.domain.model.PetSummary
import com.ignaherner.pawcare.presentation.home.EstadoMascota
import com.ignaherner.pawcare.ui.theme.Danger
import com.ignaherner.pawcare.ui.theme.DangerSoft
import com.ignaherner.pawcare.ui.theme.Info
import com.ignaherner.pawcare.ui.theme.PawRadii
import com.ignaherner.pawcare.ui.theme.PawSpace
import com.ignaherner.pawcare.ui.theme.Success
import com.ignaherner.pawcare.ui.theme.SuccessSoft
import com.ignaherner.pawcare.ui.theme.Warn
import com.ignaherner.pawcare.ui.theme.WarnSoft
import com.ignaherner.pawcare.utils.calcularDiaActual
import com.ignaherner.pawcare.utils.calcularDiaNumero
import com.ignaherner.pawcare.utils.calcularEdad
import com.ignaherner.pawcare.utils.toFriendlyDate

@Composable
fun PetSummaryCard(
    summary: PetSummary,
    estado: EstadoMascota,
    onClick: () -> Unit,
    vacunaCount: Int = 0,
    medicamentoCount: Int = 0
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PawSpace.lg, vertical = PawSpace.xs),
        shape = RoundedCornerShape(PawRadii.md),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PawSpace.lg),
            verticalArrangement = Arrangement.spacedBy(PawSpace.md)
        ) {
            // Header foto + nombre + dot + chevron
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(PawSpace.md)
            ) {
                PawCareAvatar(
                    fotoUri = summary.pet.fotoUri,
                    nombre = summary.pet.nombre,
                    modifier = Modifier.size(56.dp)
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(PawSpace.sm)
                    ) {
                        Text(
                            text = summary.pet.nombre,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        // Dot de estado
                        if (estado != EstadoMascota.OK) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (estado) {
                                            EstadoMascota.URGENTE -> Danger
                                            EstadoMascota.ATENCION -> Warn
                                            EstadoMascota.OK -> Success
                                        }
                                    )
                            )
                        }
                    }
                    Text(
                        text = buildList {
                            summary.pet.raza?.let { add(it) }
                            summary.pet.sexo?.let { add(it.displayName) }
                            add(calcularEdad(summary.pet.fechaNacimiento, summary.pet.fechaNacimientoTipo))
                        }.joinToString(" · "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                PawCareIcon(
                    icon = Icons.Outlined.ChevronRight,
                    contentDescription = null,
                    size = PawIconSize.medium,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // Stats grid 3 columnas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    icon = Icons.Outlined.Vaccines,
                    value = vacunaCount.toString(),
                    label = "vacunas",
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    icon = Icons.Outlined.Medication,
                    value = medicamentoCount.toString(),
                    label = "medicamentos",
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    icon = Icons.Outlined.Monitor,
                    value = summary.ultimoPeso?.let { "${it.peso}kg" } ?: "—",
                    label = "último peso",
                    modifier = Modifier.weight(1f)
                )
            }

            // Mini-alert si hay urgencia
            if (estado != EstadoMascota.OK) {
                summary.proximaVacuna?.let { vacuna ->
                    val toneBg = when (estado) {
                        EstadoMascota.URGENTE -> DangerSoft
                        EstadoMascota.ATENCION -> WarnSoft
                        EstadoMascota.OK -> SuccessSoft
                    }
                    val toneFg = when (estado) {
                        EstadoMascota.URGENTE -> Danger
                        EstadoMascota.ATENCION -> Warn
                        EstadoMascota.OK -> Success
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(PawRadii.sm))
                            .background(toneBg)
                            .padding(horizontal = PawSpace.md, vertical = PawSpace.sm),
                        horizontalArrangement = Arrangement.spacedBy(PawSpace.sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PawCareIcon(
                            icon = Icons.Outlined.Warning,
                            contentDescription = null,
                            size = PawIconSize.default,
                            tint = toneFg
                        )
                        Text(
                            text = "${vacuna.nombre} ${
                                when (estado) {
                                    EstadoMascota.URGENTE -> "vencida"
                                    EstadoMascota.ATENCION -> "vence pronto"
                                    EstadoMascota.OK -> ""
                                }
                            }",
                            style = MaterialTheme.typography.labelMedium,
                            color = toneFg
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(PawSpace.xs)
        ) {
            PawCareIcon(
                icon = icon,
                contentDescription = null,
                size = PawIconSize.small,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}