package com.ignaherner.pawcare.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ignaherner.pawcare.domain.model.Medication
import com.ignaherner.pawcare.domain.model.MedicationStatus
import com.ignaherner.pawcare.ui.theme.Info
import com.ignaherner.pawcare.ui.theme.InfoSoft
import com.ignaherner.pawcare.ui.theme.InkMuted
import com.ignaherner.pawcare.ui.theme.PawRadii
import com.ignaherner.pawcare.ui.theme.PawSpace
import com.ignaherner.pawcare.ui.theme.SurfaceSunk
import com.ignaherner.pawcare.ui.theme.Warn
import com.ignaherner.pawcare.ui.theme.WarnSoft
import com.ignaherner.pawcare.utils.calcularDiaActual
import com.ignaherner.pawcare.utils.calcularDiaNumero
import com.ignaherner.pawcare.utils.calcularFechaFin
import com.ignaherner.pawcare.utils.toFriendlyDate

@Composable
fun MedicationCard(
    medication: Medication,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val (toneBg, toneFg) = when (medication.status) {
        MedicationStatus.ACTIVO -> WarnSoft to Warn
        MedicationStatus.PROGRAMADO -> InfoSoft to Info
        MedicationStatus.FINALIZADO -> SurfaceSunk to InkMuted
    }

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
                .padding(PawSpace.md),
            verticalArrangement = Arrangement.spacedBy(PawSpace.sm)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(PawSpace.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(PawRadii.sm))
                        .background(toneBg),
                    contentAlignment = Alignment.Center
                ) {
                    PawCareIcon(
                        icon = Icons.Outlined.Medication,
                        contentDescription = null,
                        size = PawIconSize.medium,
                        tint = toneFg
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = medication.nombre,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${medication.dosisDisplay} · ${medication.viaAdministracion.displayName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(PawRadii.xs),
                    color = toneBg
                ) {
                    Text(
                        text = medication.status.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = toneFg,
                        modifier = Modifier.padding(horizontal = PawSpace.sm, vertical = 4.dp)
                    )
                }
            }

            // Progreso si está activo y no es única dosis
            if (medication.status == MedicationStatus.ACTIVO && !medication.esUnicaDosis) {
                val diaActual = calcularDiaNumero(medication.fechaInicio, medication.duracionDias)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Día $diaActual de ${medication.duracionDias}",
                            style = MaterialTheme.typography.labelSmall,
                            color = toneFg
                        )
                        Text(
                            text = "Cada ${medication.intervaloHoras}h",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    LinearProgressIndicator(
                        progress = { diaActual.toFloat() / medication.duracionDias.toFloat() },
                        modifier = Modifier.fillMaxWidth(),
                        color = toneFg,
                        trackColor = toneBg
                    )
                }
            }
        }
    }
}
