package com.ignaherner.pawcare.presentation.deworming

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ignaherner.pawcare.domain.model.Deworming
import com.ignaherner.pawcare.utils.toFriendlyDate
import com.ignaherner.pawcare.presentation.components.ConfirmDeleteDialog
import com.ignaherner.pawcare.presentation.components.EmptyState
import com.ignaherner.pawcare.presentation.components.PawCard
import com.ignaherner.pawcare.presentation.components.PawCareIcon
import com.ignaherner.pawcare.presentation.components.PawIconSize
import com.ignaherner.pawcare.presentation.components.SwipeRevealCard
import com.ignaherner.pawcare.presentation.pets.PetDetailState
import com.ignaherner.pawcare.presentation.pets.PetViewModel
import com.ignaherner.pawcare.ui.theme.CatDeworming
import com.ignaherner.pawcare.ui.theme.CatDewormingSoft
import com.ignaherner.pawcare.ui.theme.PawRadio
import com.ignaherner.pawcare.ui.theme.PawSpace

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DewormingScreen(
    petId: Long,
    petName: String,
    onNavigateBack: () -> Unit,
    onNavigateToForm: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onNavigateToDetail: (Long) -> Unit = {},
    isVeterinario: Boolean = false,
    viewModel: DewormingViewModel = hiltViewModel(),
    petViewModel: PetViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()
    var dewormingToDelete by remember { mutableStateOf<Deworming?>(null) }

    val detailState by petViewModel.detailState.collectAsStateWithLifecycle()

    LaunchedEffect(petId) {
        viewModel.loadDewormings(petId)
        petViewModel.loadPetById(petId)
    }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
            viewModel.clearSnackbar()
        }
    }

    dewormingToDelete?.let { deworming ->
        ConfirmDeleteDialog(
            titulo = "¿Eliminar ${deworming.producto}?",
            mensaje = "Esta acción no se puede deshacer.",
            onConfirm = {
                viewModel.deleteDeworming(deworming)
                dewormingToDelete = null
            },
            onDismiss = { dewormingToDelete = null }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        val titulo = when (val state = detailState) {
                            is PetDetailState.Success -> state.pet.nombre
                            else -> ""
                        }
                        Text(
                            text = titulo,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Desparasitación",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            if (isVeterinario) {
                FloatingActionButton(
                    onClick = onNavigateToForm,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar desparasitación")
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is DewormingUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is DewormingUiState.Empty -> {
                    EmptyState(
                        icon = Icons.Outlined.Shield,
                        title = "Sin desparasitaciones registradas",
                        body = if (isVeterinario)
                            "Tocá el + para registrar una desparasitación"
                        else
                            "El veterinario podrá registrar las desparasitaciones de tu mascota"
                    )
                }
                is DewormingUiState.Success -> {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            top = PawSpace.sm,
                            bottom = 96.dp
                        )
                    ) {
                        items(
                            items = state.deworming,
                            key = { it.id }
                        ) { deworming ->
                            if (isVeterinario) {
                                SwipeRevealCard(
                                    onDelete = { dewormingToDelete = deworming },
                                    onEdit = { onNavigateToEdit(deworming.id) }
                                ) {
                                    DewormingCard(
                                        deworming = deworming,
                                        onClick = { onNavigateToDetail(deworming.id) }
                                    )
                                }
                            } else {
                                DewormingCard(
                                    deworming = deworming,
                                    onClick = { onNavigateToDetail(deworming.id) }
                                )
                            }
                        }
                    }
                }
                is DewormingUiState.Error -> {
                    Text(
                        text = state.mensaje,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
private fun DewormingCard(
    deworming: Deworming,
    onClick: () -> Unit = {}
) {
    PawCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PawSpace.lg, vertical = PawSpace.xs),
        onClick = onClick
    )  {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PawSpace.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(PawSpace.md)
        ) {
            // Tile ícono
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(PawRadio.sm))
                    .background(CatDewormingSoft),
                contentAlignment = Alignment.Center
            ) {
                PawCareIcon(
                    icon = Icons.Outlined.Shield,
                    contentDescription = null,
                    size = PawIconSize.medium,
                    tint = CatDeworming
                )
            }

            // Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = deworming.producto,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${deworming.tipo.displayName} · ${deworming.fechaAplicacion.toFriendlyDate()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                deworming.proximaDosis?.let {
                    Text(
                        text = "Próxima: ${it.toFriendlyDate()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = CatDeworming
                    )
                }
            }

            // Tipo pill
            Surface(
                shape = RoundedCornerShape(PawRadio.xs),
                color = CatDewormingSoft
            ) {
                Text(
                    text = deworming.tipo.displayName,
                    style = MaterialTheme.typography.labelSmall,
                    color = CatDeworming,
                    modifier = Modifier.padding(horizontal = PawSpace.sm, vertical = 4.dp)
                )
            }
        }
    }
}