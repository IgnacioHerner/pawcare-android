package com.ignaherner.pawcare.presentation.vaccines

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Notes
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ignaherner.pawcare.domain.model.Vaccine
import com.ignaherner.pawcare.domain.model.VaccineStatus
import com.ignaherner.pawcare.presentation.components.PawCard
import com.ignaherner.pawcare.utils.toFriendlyDate
import com.ignaherner.pawcare.presentation.components.PawCareIcon
import com.ignaherner.pawcare.presentation.components.PawIconSize
import com.ignaherner.pawcare.ui.theme.Danger
import com.ignaherner.pawcare.ui.theme.DangerSoft
import com.ignaherner.pawcare.ui.theme.Info
import com.ignaherner.pawcare.ui.theme.InfoSoft
import com.ignaherner.pawcare.ui.theme.PawRadio
import com.ignaherner.pawcare.ui.theme.PawSpace
import com.ignaherner.pawcare.ui.theme.Success
import com.ignaherner.pawcare.ui.theme.SuccessSoft

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
    val (toneBg, toneFg) = when (vaccine.status) {
        is VaccineStatus.Aplicada -> SuccessSoft to Success
        is VaccineStatus.Programada -> InfoSoft to Info
        is VaccineStatus.Vencida -> DangerSoft to Danger
    }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(PawSpace.lg),
        verticalArrangement = Arrangement.spacedBy(PawSpace.lg)
    ) {
        // Header con tipo + status
        Column(verticalArrangement = Arrangement.spacedBy(PawSpace.sm)) {
            Text(
                text = vaccine.tipo.displayName,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            vaccine.nombreComercial?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Surface(
                shape = RoundedCornerShape(PawRadio.xs),
                color = toneBg
            ) {
                Text(
                    text = vaccine.status.displayName(),
                    style = MaterialTheme.typography.labelMedium,
                    color = toneFg,
                    modifier = Modifier.padding(horizontal = PawSpace.md, vertical = PawSpace.xs)
                )
            }
        }

        // Card de info
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
                    icon = Icons.Outlined.CalendarMonth,
                    label = "Fecha aplicación",
                    value = vaccine.fechaAplicacion.toFriendlyDate()
                )

                DetailRow(
                    icon = Icons.Outlined.Repeat,
                    label = "Frecuencia",
                    value = vaccine.frecuencia.displayName
                )

                vaccine.proximaDosis?.let {
                    DetailRow(
                        icon = Icons.Outlined.Schedule,
                        label = "Próxima dosis",
                        value = it.toFriendlyDate()
                    )
                }

                vaccine.veterinario?.let {
                    DetailRow(
                        icon = Icons.Outlined.Person,
                        label = "Veterinario",
                        value = it
                    )
                }

                vaccine.notas?.let {
                    DetailRow(
                        icon = Icons.Outlined.Notes,
                        label = "Notas",
                        value = it
                    )
                }
            }
        }

        // Descripción de la vacuna
        PawCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(PawSpace.lg),
                verticalArrangement = Arrangement.spacedBy(PawSpace.sm)
            ) {
                Text(
                    text = "SOBRE ESTA VACUNA",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = vaccine.tipo.descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
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