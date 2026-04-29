package com.ignaherner.pawcare.presentation.deworming

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
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Notes
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.Schedule
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
import com.ignaherner.pawcare.domain.model.Deworming
import com.ignaherner.pawcare.utils.toFriendlyDate
import com.ignaherner.pawcare.presentation.components.PawCareIcon
import com.ignaherner.pawcare.presentation.components.PawIconSize
import com.ignaherner.pawcare.ui.theme.CatDeworming
import com.ignaherner.pawcare.ui.theme.CatDewormingSoft
import com.ignaherner.pawcare.ui.theme.PawRadii
import com.ignaherner.pawcare.ui.theme.PawSpace

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DewormingDetailScreen(
    dewormingId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    viewModel: DewormingViewModel = hiltViewModel()
) {
    val detailState by viewModel.dewormingDetailState.collectAsStateWithLifecycle()

    LaunchedEffect(dewormingId) {
        viewModel.loadDewormingById(dewormingId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle desparasitación") },
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
                    IconButton(onClick = { onNavigateToEdit(dewormingId) }) {
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
                is DewormingDetailState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is DewormingDetailState.Error -> {
                    Text(
                        text = state.mensaje,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is DewormingDetailState.Success -> {
                    DewormingDetailContent(
                        deworming = state.deworming,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun DewormingDetailContent(
    deworming: Deworming,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(PawSpace.lg),
        verticalArrangement = Arrangement.spacedBy(PawSpace.lg)
    ) {
        // Header — producto + pills
        Column(verticalArrangement = Arrangement.spacedBy(PawSpace.sm)) {
            Text(
                text = deworming.producto,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Row(horizontalArrangement = Arrangement.spacedBy(PawSpace.sm)) {
                Surface(
                    shape = RoundedCornerShape(PawRadii.xs),
                    color = CatDewormingSoft
                ) {
                    Text(
                        text = deworming.tipo.displayName,
                        style = MaterialTheme.typography.labelMedium,
                        color = CatDeworming,
                        modifier = Modifier.padding(horizontal = PawSpace.md, vertical = PawSpace.xs)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(PawRadii.xs),
                    color = CatDewormingSoft
                ) {
                    Text(
                        text = deworming.frecuencia.displayName,
                        style = MaterialTheme.typography.labelMedium,
                        color = CatDeworming,
                        modifier = Modifier.padding(horizontal = PawSpace.md, vertical = PawSpace.xs)
                    )
                }
            }
        }

        // Info card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(PawRadii.md),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
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
                    value = deworming.fechaAplicacion.toFriendlyDate()
                )

                DetailRow(
                    icon = Icons.Outlined.Category,
                    label = "Tipo",
                    value = deworming.tipo.displayName
                )

                DetailRow(
                    icon = Icons.Outlined.Repeat,
                    label = "Frecuencia",
                    value = deworming.frecuencia.displayName
                )

                deworming.proximaDosis?.let {
                    DetailRow(
                        icon = Icons.Outlined.Schedule,
                        label = "Próxima dosis",
                        value = it.toFriendlyDate()
                    )
                }

                deworming.veterinario?.let {
                    DetailRow(
                        icon = Icons.Outlined.Person,
                        label = "Veterinario",
                        value = it
                    )
                }

                deworming.notas?.let {
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