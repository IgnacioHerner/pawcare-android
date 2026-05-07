package com.ignaherner.pawcare.presentation.condition

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
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notes
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ignaherner.pawcare.domain.model.Condition
import com.ignaherner.pawcare.domain.model.ConditionEstado
import com.ignaherner.pawcare.domain.model.Severidad
import com.ignaherner.pawcare.presentation.components.PawCard
import com.ignaherner.pawcare.utils.toFriendlyDate
import com.ignaherner.pawcare.presentation.components.PawCareIcon
import com.ignaherner.pawcare.presentation.components.PawIconSize
import com.ignaherner.pawcare.ui.theme.Danger
import com.ignaherner.pawcare.ui.theme.DangerSoft
import com.ignaherner.pawcare.ui.theme.PawRadio
import com.ignaherner.pawcare.ui.theme.PawSpace
import com.ignaherner.pawcare.ui.theme.Success
import com.ignaherner.pawcare.ui.theme.SuccessSoft
import com.ignaherner.pawcare.ui.theme.Warn
import com.ignaherner.pawcare.ui.theme.WarnSoft

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConditionDetailScreen(
    conditionId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    viewModel: ConditionViewModel = hiltViewModel()
) {
    val detailState by viewModel.conditionDetailState.collectAsStateWithLifecycle()

    LaunchedEffect(conditionId) {
        viewModel.loadConditionById(conditionId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle condición") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        PawCareIcon(
                            icon = Icons.Outlined.ArrowBack,
                            contentDescription = "Volver",
                            size = PawIconSize.medium
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToEdit(conditionId) }) {
                        PawCareIcon(
                            icon = Icons.Outlined.Edit,
                            contentDescription = "Editar",
                            size = PawIconSize.medium
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = detailState) {
                is ConditionDetailState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ConditionDetailState.Error -> {
                    Text(
                        text = state.mensaje,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is ConditionDetailState.Success -> {
                    ConditionDetailContent(
                        condition = state.condition,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun ConditionDetailContent(
    condition: Condition,
    modifier: Modifier = Modifier
) {
    val (severidadBg, severidadFg) = when (condition.severidad) {
        Severidad.LEVE -> SuccessSoft to Success
        Severidad.MODERADA -> WarnSoft to Warn
        Severidad.GRAVE -> DangerSoft to Danger
    }

    val (estadoBg, estadoFg) = when (condition.estado) {
        ConditionEstado.ACTIVA -> DangerSoft to Danger
        ConditionEstado.EN_TRATAMIENTO -> WarnSoft to Warn
        ConditionEstado.RESUELTA -> SuccessSoft to Success
    }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(PawSpace.lg),
        verticalArrangement = Arrangement.spacedBy(PawSpace.lg)
    ) {
        // Header — nombre + pills
        Column(verticalArrangement = Arrangement.spacedBy(PawSpace.sm)) {
            Text(
                text = condition.nombre,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Row(horizontalArrangement = Arrangement.spacedBy(PawSpace.sm)) {
                Surface(
                    shape = RoundedCornerShape(PawRadio.xs),
                    color = estadoBg
                ) {
                    Text(
                        text = condition.estado.displayName,
                        style = MaterialTheme.typography.labelMedium,
                        color = estadoFg,
                        modifier = Modifier.padding(horizontal = PawSpace.md, vertical = PawSpace.xs)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(PawRadio.xs),
                    color = severidadBg
                ) {
                    Text(
                        text = condition.severidad.displayName,
                        style = MaterialTheme.typography.labelMedium,
                        color = severidadFg,
                        modifier = Modifier.padding(horizontal = PawSpace.md, vertical = PawSpace.xs)
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
                    icon = Icons.Outlined.CalendarMonth,
                    label = "Fecha diagnóstico",
                    value = condition.fechaDiagnostico.toFriendlyDate()
                )

                DetailRow(
                    icon = Icons.Outlined.Warning,
                    label = "Severidad",
                    value = condition.severidad.displayName
                )

                DetailRow(
                    icon = Icons.Outlined.Info,
                    label = "Estado",
                    value = condition.estado.displayName
                )

                condition.veterinario?.let {
                    DetailRow(
                        icon = Icons.Outlined.Person,
                        label = "Veterinario",
                        value = it
                    )
                }

                condition.notas?.let {
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