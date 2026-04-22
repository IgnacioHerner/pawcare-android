package com.ignaherner.pawcare.presentation.deworming

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ignaherner.pawcare.domain.model.Deworming
import com.ignaherner.pawcare.utils.toFriendlyDate
import com.ignaherner.pawcare.presentation.components.ConfirmDeleteDialog
import com.ignaherner.pawcare.presentation.components.SwipeRevealCard
import com.ignaherner.pawcare.presentation.pets.PetDetailState
import com.ignaherner.pawcare.presentation.pets.PetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DewormingScreen(
    petId: Long,
    petName: String,
    onNavigateBack: () -> Unit,
    onNavigateToForm: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    isVeterinario: Boolean = false,
    viewModel: DewormingViewModel = hiltViewModel(),
    petViewModel: PetViewModel = hiltViewModel()
){
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
            titulo = "Eliminar ${deworming.fecha}?",
            mensaje = "Esta acción no se puede deshacer.",
            onConfirm = {
                viewModel.deleteDeworming(deworming)
                dewormingToDelete = null
            },
            onDismiss = { dewormingToDelete = null}
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        val titulo = when (val state = detailState) {
                            is PetDetailState.Success ->
                                "${state.pet.nombre} ${state.pet.especie.emoji()}"
                            else -> ""
                        }
                        Text(
                            text = titulo,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Desparasitacion \uD83E\uDEB1",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            if(isVeterinario) {
                FloatingActionButton(onClick = onNavigateToForm) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar desparasitacion")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ){
            when (val state = uiState) {
                is DewormingUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is DewormingUiState.Empty -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("\uD83E\uDEB1", style = MaterialTheme.typography.displayMedium)
                        Text(
                            text = "Sin desparacitaciones registradas",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Toca el + para agregar",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                is DewormingUiState.Success -> {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 16.dp,
                            bottom = 80.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = state.deworming,
                            key = {it.id}
                        ) { deworming ->
                            if (isVeterinario){
                                SwipeRevealCard(
                                    onDelete = { dewormingToDelete = deworming},
                                    onEdit = {onNavigateToEdit(deworming.id)}
                                ) {
                                    DewormingCard(deworming = deworming)
                                }
                            } else {
                                DewormingCard(deworming = deworming)
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
private fun DewormingCard(deworming: Deworming) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = deworming.fecha,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            deworming.producto?.let {
                Text(
                    text = "\uD83D\uDC8A Producto: $it",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            deworming.proximaFecha?.let {
                Text(
                    text = "\uD83D\uDCC5 Proxima fecha: ${it.toFriendlyDate()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            deworming.notas?.let {
                Text(
                    text = "📝 $it",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}