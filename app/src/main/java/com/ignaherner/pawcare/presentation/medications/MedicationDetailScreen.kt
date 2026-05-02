package com.ignaherner.pawcare.presentation.medications

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
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocalPharmacy
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Notes
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ignaherner.pawcare.domain.model.Medication
import com.ignaherner.pawcare.domain.model.MedicationStatus
import com.ignaherner.pawcare.utils.calcularDiaNumero
import com.ignaherner.pawcare.utils.calcularFechaFin
import com.ignaherner.pawcare.utils.toFriendlyDate
import com.ignaherner.pawcare.presentation.components.InfoRow
import com.ignaherner.pawcare.presentation.components.PawCard
import com.ignaherner.pawcare.presentation.components.PawCareIcon
import com.ignaherner.pawcare.presentation.components.PawIconSize
import com.ignaherner.pawcare.ui.theme.Info
import com.ignaherner.pawcare.ui.theme.InfoSoft
import com.ignaherner.pawcare.ui.theme.InkMuted
import com.ignaherner.pawcare.ui.theme.PawRadii
import com.ignaherner.pawcare.ui.theme.PawSpace
import com.ignaherner.pawcare.ui.theme.SurfaceSunk
import com.ignaherner.pawcare.ui.theme.Warn
import com.ignaherner.pawcare.ui.theme.WarnSoft

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationDetailScreen(
    medicationId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    viewModel: MedicationViewModel = hiltViewModel()
) {
    val detailState by viewModel.medicationDetailState.collectAsStateWithLifecycle()

    LaunchedEffect(medicationId) {
        viewModel.loadMedicationById(medicationId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle medicamento") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToEdit(medicationId)}) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
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
                is MedicationDetailState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is MedicationDetailState.Error -> {
                    Text(
                        text = state.mensaje,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is MedicationDetailState.Success -> {
                    MedicationDetailContent(
                        medication = state.medication,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun MedicationDetailContent(
    medication: Medication,
    modifier: Modifier = Modifier
) {
    val (toneBg, toneFg) = when (medication.status) {
        MedicationStatus.ACTIVO -> WarnSoft to Warn
        MedicationStatus.PROGRAMADO -> InfoSoft to Info
        MedicationStatus.FINALIZADO -> SurfaceSunk to InkMuted
    }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(PawSpace.lg),
        verticalArrangement = Arrangement.spacedBy(PawSpace.lg)
    ) {
        // Header
        Column(verticalArrangement = Arrangement.spacedBy(PawSpace.sm)) {
            Text(
                text = medication.nombre,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Surface(
                shape = RoundedCornerShape(PawRadii.xs),
                color = toneBg
            ) {
                Text(
                    text = medication.status.displayName,
                    style = MaterialTheme.typography.labelMedium,
                    color = toneFg,
                    modifier = Modifier.padding(horizontal = PawSpace.md, vertical = PawSpace.xs)
                )
            }
        }

        // Progreso
        if (medication.status == MedicationStatus.ACTIVO && !medication.esUnicaDosis) {
            PawCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(PawSpace.lg),
                    verticalArrangement = Arrangement.spacedBy(PawSpace.sm)
                ) {
                    val diaActual = calcularDiaNumero(medication.fechaInicio, medication.duracionDias)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Progreso del tratamiento",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = "Día $diaActual de ${medication.duracionDias}",
                            style = MaterialTheme.typography.bodySmall,
                            color = toneFg
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
                    icon = Icons.Outlined.LocalPharmacy,
                    label = "Dosis",
                    value = medication.dosisDisplay
                )

                DetailRow(
                    icon = Icons.Outlined.MedicalServices,
                    label = "Vía",
                    value = medication.viaAdministracion.displayName
                )

                if (!medication.esUnicaDosis) {
                    DetailRow(
                        icon = Icons.Outlined.Schedule,
                        label = "Frecuencia",
                        value = "Cada ${medication.intervaloHoras}h"
                    )
                    DetailRow(
                        icon = Icons.Outlined.CalendarMonth,
                        label = "Inicio",
                        value = medication.fechaInicio.toFriendlyDate()
                    )
                    DetailRow(
                        icon = Icons.Outlined.Event,
                        label = "Fin estimado",
                        value = calcularFechaFin(medication.fechaInicio, medication.duracionDias).toFriendlyDate()
                    )
                } else {
                    DetailRow(
                        icon = Icons.Outlined.CalendarMonth,
                        label = "Fecha",
                        value = medication.fechaInicio.toFriendlyDate()
                    )
                    DetailRow(
                        icon = Icons.Outlined.Info,
                        label = "Tipo",
                        value = "Única dosis"
                    )
                }

                medication.recetadoPor?.let {
                    DetailRow(
                        icon = Icons.Outlined.Person,
                        label = "Recetado por",
                        value = it
                    )
                }

                medication.notas?.let {
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