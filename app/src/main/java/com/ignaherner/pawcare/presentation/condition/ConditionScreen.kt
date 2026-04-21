package com.ignaherner.pawcare.presentation.condition

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
import com.ignaherner.pawcare.domain.model.toFriendlyDate
import com.ignaherner.pawcare.domain.model.Condition
import com.ignaherner.pawcare.presentation.components.ConfirmDeleteDialog
import com.ignaherner.pawcare.presentation.components.SwipeRevealCard
import com.ignaherner.pawcare.presentation.pets.PetDetailState
import com.ignaherner.pawcare.presentation.pets.PetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConditionScreen(
    petId: Long,
    petName: String,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onNavigateToForm: () -> Unit,
    isVeterinario: Boolean = false,
    viewModel: ConditionViewModel = hiltViewModel(),
    petViewModel: PetViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()
    var conditionToDelete by remember { mutableStateOf<Condition?>(null) }

    val detailState by petViewModel.detailState.collectAsStateWithLifecycle()

    LaunchedEffect(petId) {
        viewModel.loadConditions(petId)
        petViewModel.loadPetById(petId)
    }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
            viewModel.clearSnackbar()
        }
    }

    conditionToDelete?.let { condition ->
        ConfirmDeleteDialog(
            titulo = "¿Eliminar ${condition.nombre}?",
            mensaje = "Esta acción no se puede deshacer.",
            onConfirm = {
                viewModel.deleteCondition(condition)
                conditionToDelete = null
            },
            onDismiss = { conditionToDelete = null}
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        // Nombre + emoji si ya cargó
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
                            text = "Medicamentos \uD83C\uDFE5",
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
            if (isVeterinario){
                FloatingActionButton(onClick = onNavigateToForm) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar condición")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when(val state = uiState) {
                is ConditionUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is ConditionUiState.Empty -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("\uD83C\uDFE5", style = MaterialTheme.typography.displayMedium)
                        Text(
                            text = "Sin condiciones registradas",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Toca el + para agregar",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                is ConditionUiState.Success -> {
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
                            items = state.conditions,
                            key = {it.id}
                        ) { condition ->
                            if (isVeterinario){
                                SwipeRevealCard(
                                    onDelete = { conditionToDelete = condition},
                                    onEdit = { onNavigateToEdit(condition.id) }
                                ) {
                                    ConditionCard(condition = condition)
                                }
                            } else {
                                ConditionCard(condition = condition)
                            }

                        }
                    }
                }
                is ConditionUiState.Error -> {
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
private fun ConditionCard(condition: Condition) {
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
                text = condition.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            condition.fechaDiagnostico?.let {
                Text(
                    text = "📅 Diagnóstico: ${it.toFriendlyDate()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            condition.notas?.let {
                Text(
                    text = "📝 $it",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}