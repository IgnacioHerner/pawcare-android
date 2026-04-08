package com.ignaherner.pawcare.presentation.vaccines

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
import com.ignaherner.pawcare.domain.model.Vaccine
import com.ignaherner.pawcare.domain.model.VaccineStatus
import com.ignaherner.pawcare.domain.model.toFriendlyDate
import com.ignaherner.pawcare.presentation.components.InfoRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaccineDetailScreen(
    vaccineId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    viewModel: VaccineViewModel = hiltViewModel()
) {
    val detailState by viewModel.vaccineDetailState.collectAsStateWithLifecycle()

    LaunchedEffect(vaccineId) {
        viewModel.loadVaccineById(vaccineId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle vacuna") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = {onNavigateToEdit(vaccineId)}) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar vacuna")
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
                is VaccineDetailState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is VaccineDetailState.Error -> {
                    Text(
                        text = state.mensaje,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is VaccineDetailState.Success -> {
                    VaccineDetailContent(
                        vaccine = state.vaccine,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun VaccineDetailContent(
    vaccine: Vaccine,
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
                text = vaccine.nombre,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            AssistChip(
                onClick = {},
                label = {
                    Text(
                        text = when (vaccine.status) {
                            is VaccineStatus.Aplicada -> "Aplicada"
                            is VaccineStatus.Pendiente -> "Pendiente"
                            is VaccineStatus.Programada -> "Programada"
                        },
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = when (vaccine.status) {
                        is VaccineStatus.Aplicada -> Color(0xFF4CAF50).copy(alpha = 0.15f)
                        is VaccineStatus.Programada -> Color(0xFF2196F3).copy(alpha = 0.15f)
                        is VaccineStatus.Pendiente -> Color(0xFFFF9800).copy(alpha = 0.15f)
                    },
                    labelColor = when (vaccine.status) {
                        is VaccineStatus.Aplicada -> Color(0xFF4CAF50)
                        is VaccineStatus.Programada -> Color(0xFF2196F3)
                        is VaccineStatus.Pendiente -> Color(0xFFFF9800)
                    }
                )
            )
        }

        // Info de la vacuna
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

                vaccine.fecha?.let {
                    InfoRow("📅 Fecha aplicación", it.toFriendlyDate())
                }

                InfoRow(
                    "🔄 Es anual",
                    if (vaccine.esAnual) "Sí" else "No"
                )

                vaccine.proximaDosis?.let {
                    InfoRow("💉 Próxima dosis", it.toFriendlyDate())
                }

                vaccine.veterinario?.let {
                    InfoRow("👨‍⚕️ Veterinario", it)
                }

                vaccine.notas?.let {
                    InfoRow("📝 Notas", it)
                }
            }
        }
    }
}