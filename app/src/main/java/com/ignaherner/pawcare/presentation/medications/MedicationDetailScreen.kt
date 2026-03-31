package com.ignaherner.pawcare.presentation.medications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import com.ignaherner.pawcare.domain.model.Medication
import com.ignaherner.pawcare.domain.model.MedicationStatus
import com.ignaherner.pawcare.domain.model.calcularDiaNumero
import com.ignaherner.pawcare.domain.model.calcularFechaFin
import com.ignaherner.pawcare.domain.model.toFriendlyDate
import com.ignaherner.pawcare.presentation.components.InfoRow

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
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header - NOMBRE + STATUS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = medication.nombre,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            AssistChip(
                onClick = {},
                label = {
                    Text(
                        text = if (medication.status == MedicationStatus.ACTIVO)
                            "En curso" else "Finalizado",
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (medication.status == MedicationStatus.ACTIVO)
                        Color(0xFF4CAF50).copy(alpha = 0.15f)
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                    labelColor = if (medication.status == MedicationStatus.ACTIVO)
                        Color(0xFF4CAF50)
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }

        // Progreso - solo si esta activo
        if (medication.status == MedicationStatus.ACTIVO && !medication.esUnicaDosis) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val diaActual = calcularDiaNumero(
                        medication.fechaInicio,
                        medication.duracionDias
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Progreso del tratamiento",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Día $diaActual de ${medication.duracionDias}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    LinearProgressIndicator(
                        progress = {
                            diaActual.toFloat() / medication.duracionDias.toFloat()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Info del medicamento
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Informacíon",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                InfoRow("💊 Dosis", medication.dosis)

                if (!medication.esUnicaDosis) {
                    InfoRow("⏱ Frecuencia", "Cada ${medication.intervaloHoras}h")
                    InfoRow("📅 Inicio", medication.fechaInicio.toFriendlyDate())
                    InfoRow(
                        "🏁 Fin estimado",
                        calcularFechaFin(
                            medication.fechaInicio,
                            medication.duracionDias
                        ).toFriendlyDate()
                    )
                } else {
                    InfoRow("📅 Fecha", medication.fechaInicio.toFriendlyDate())
                    InfoRow("💊 Tipo", "Única dosis")
                }

                medication.recetadoPor?.let {
                    InfoRow("👨‍⚕️ Recetado por", it)
                }

                medication.notas?.let {
                    InfoRow("📝 Notas", it)
                }
            }
        }
    }
}