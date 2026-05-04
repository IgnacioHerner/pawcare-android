package com.ignaherner.pawcare.presentation.weight

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Monitor
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ignaherner.pawcare.domain.model.Weight
import com.ignaherner.pawcare.presentation.components.ConfirmDeleteDialog
import com.ignaherner.pawcare.presentation.components.EmptyState
import com.ignaherner.pawcare.presentation.components.PawCard
import com.ignaherner.pawcare.presentation.components.PawCareIcon
import com.ignaherner.pawcare.presentation.components.PawIconSize
import com.ignaherner.pawcare.presentation.components.SwipeRevealCard
import com.ignaherner.pawcare.presentation.pets.PetDetailState
import com.ignaherner.pawcare.presentation.pets.PetViewModel
import com.ignaherner.pawcare.ui.theme.Danger
import com.ignaherner.pawcare.ui.theme.PawSpace
import com.ignaherner.pawcare.ui.theme.Success
import com.ignaherner.pawcare.utils.toFriendlyDate
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightScreen(
    petId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToForm: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    isVeterinario: Boolean = false,
    viewModel: WeightViewModel = hiltViewModel(),
    petViewModel: PetViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val detailState by petViewModel.detailState.collectAsStateWithLifecycle()

    LaunchedEffect(petId) {
        viewModel.loadWeights(petId)
        petViewModel.loadPetById(petId)
    }

    var weightToDelete by remember { mutableStateOf<Weight?>(null) }

    weightToDelete?.let { weight ->
        ConfirmDeleteDialog(
            titulo = "¿Eliminar registro del ${weight.fecha.toFriendlyDate()}?",
            mensaje = "Esta acción no se puede deshacer.",
            onConfirm = {
                viewModel.deleteWeight(weight)
                weightToDelete = null
            },
            onDismiss = { weightToDelete = null }
        )
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(message = it, duration = SnackbarDuration.Short)
            viewModel.clearSnackbar()
        }
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
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Peso",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        PawCareIcon(
                            icon = Icons.Outlined.ArrowBack,
                            contentDescription = "Volver",
                            size = PawIconSize.medium
                        )
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
                    Icon(Icons.Default.Add, contentDescription = "Agregar peso")
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
                is WeightUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is WeightUiState.Empty -> {
                    EmptyState(
                        icon = Icons.Outlined.Monitor,
                        title = "Sin registros de peso",
                        body = if (isVeterinario)
                            "Tocá el + para registrar el primer peso"
                        else
                            "El veterinario podrá registrar el peso de tu mascota"
                    )
                }
                is WeightUiState.Success -> {
                    WeightContent(
                        weights = state.weights,
                        onDeleteWeight = { weightToDelete = it },
                        onEditWeight = { onNavigateToEdit(it.id) },
                        isVeterinario = isVeterinario,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                is WeightUiState.Error -> {
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
private fun WeightContent(
    weights: List<Weight>,
    onDeleteWeight: (Weight) -> Unit,
    onEditWeight: (Weight) -> Unit,
    isVeterinario: Boolean = false,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = PawSpace.lg,
            end = PawSpace.lg,
            top = PawSpace.sm,
            bottom = 96.dp
        ),
        verticalArrangement = Arrangement.spacedBy(PawSpace.md)
    ) {
        // Card principal — peso actual + gráfica
        item {
            WeightHeroCard(weights = weights)
        }

        // Historial
        itemsIndexed(weights) { index, weight ->
            val pesoAnterior = weights.getOrNull(index + 1)?.peso

            if (isVeterinario) {
                SwipeRevealCard(
                    onDelete = { onDeleteWeight(weight) },
                    onEdit = { onEditWeight(weight) }
                ) {
                    WeightHistoryCard(
                        weight = weight,
                        variacion = pesoAnterior?.let { weight.peso - it }
                    )
                }
            } else {
                WeightHistoryCard(
                    weight = weight,
                    variacion = pesoAnterior?.let { weight.peso - it }
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// HERO CARD — peso actual + gráfica
// ═══════════════════════════════════════════════════════════
@Composable
private fun WeightHeroCard(weights: List<Weight>) {
    val ultimoPeso = weights.firstOrNull()

    PawCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PawSpace.lg),
            verticalArrangement = Arrangement.spacedBy(PawSpace.md)
        ) {
            // Label + peso grande
            Text(
                text = "PESO ACTUAL",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(PawSpace.sm)
            ) {
                Text(
                    text = ultimoPeso?.let { "${"%.1f".format(it.peso)}" } ?: "—",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "kg",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                // Variación con el anterior
                if (weights.size >= 2) {
                    val diff = weights[0].peso - weights[1].peso
                    val signo = if (diff >= 0) "+" else ""
                    Text(
                        text = "$signo${"%.1f".format(diff)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (diff >= 0) Success else Danger,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }

            // Gráfica
            if (weights.size >= 2) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                WeightChartInline(weights = weights)
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// GRÁFICA INLINE
// ═══════════════════════════════════════════════════════════
@Composable
private fun WeightChartInline(weights: List<Weight>) {
    val pesosOrdenados = weights.reversed()
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(weights) {
        modelProducer.runTransaction {
            lineSeries {
                series(pesosOrdenados.map { it.peso.toFloat() })
            }
        }
    }

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(),
            startAxis = rememberStartAxis(),
            bottomAxis = rememberBottomAxis()
        ),
        modelProducer = modelProducer,
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
    )
}

// ═══════════════════════════════════════════════════════════
// WEIGHT HISTORY CARD — cada registro
// ═══════════════════════════════════════════════════════════
@Composable
private fun WeightHistoryCard(
    weight: Weight,
    variacion: Double?
) {
    PawCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PawSpace.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "${"%.1f".format(weight.peso)} kg",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = weight.fecha.toFriendlyDate(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                weight.notas?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontStyle = FontStyle.Italic
                    )
                }
            }

            // Variación
            variacion?.let {
                val signo = if (it >= 0) "↑" else "↓"
                val color = if (it >= 0) Success else Danger

                Text(
                    text = "$signo ${"%.1f".format(kotlin.math.abs(it))}",
                    style = MaterialTheme.typography.labelMedium,
                    color = color
                )
            }
        }
    }
}