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
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ignaherner.pawcare.domain.model.Weight
import com.ignaherner.pawcare.domain.model.WeightMetrics
import com.ignaherner.pawcare.domain.model.calcularMetricas
import com.ignaherner.pawcare.presentation.components.WeightCard
import com.ignaherner.pawcare.presentation.pets.PetDetailState
import com.ignaherner.pawcare.presentation.pets.PetViewModel
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
    viewModel: WeightViewModel = hiltViewModel(),
    petViewModel: PetViewModel = hiltViewModel()
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val detailState by petViewModel.detailState.collectAsStateWithLifecycle()

    LaunchedEffect(petId) {
        viewModel.loadWeights(petId)
        petViewModel.loadPetById(petId)
    }

    Scaffold(
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
                            text = "Registro de peso ⚖️",
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
            FloatingActionButton(onClick = onNavigateToForm) {
                Icon(Icons.Default.Add, contentDescription = "Agregar pesos")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when(val state = uiState) {
                is WeightUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is WeightUiState.Empty -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("⚖️", style = MaterialTheme.typography.displayLarge)
                        Text(
                            text = "Sin registros de peso todavía",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Tocá el + para agregar el primero",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                is WeightUiState.Success -> {
                    WeightContent(
                        weights = state.weights,
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
    modifier: Modifier = Modifier
) {
    val metricas = calcularMetricas(weights)

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // 1. Métricas arriba
        metricas?.let { m ->
            item {
                MetricsCard(metricas = m)
            }
        }

        // 2. Gráfica solo si hay 2+ registros
        if (weights.size >= 2) {
            item {
                WeightChart(weights = weights)
            }
        } else {
            item {
                Text(
                    text = "Agregá más registros par ver la evolucion \uD83D\uDCC8",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // 3. Lista de registros con índice
        itemsIndexed(weights) { index, weight ->
            WeightCard(
                weight = weight,
                pesoAnterior = weights.getOrNull(index + 1)?.peso,
                onClick = {},
                onDeleteClick = {}
            )
        }
    }

}

@Composable
private fun MetricsCard(metricas: WeightMetrics) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Resumen",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            // Tres métricas en una fila
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricaItem(
                    label = "Ultimo peso",
                    valor = "${"%.1f".format(metricas.ultimoPeso)} kg"
                )
                MetricaItem(
                    label = "Promedio",
                    valor = "${"%.1f".format(metricas.promedio)} kg"
                )

                metricas.cambio30Dias?.let {
                    val signo = if (it >= 0) "+" else ""
                    val color = if (it >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                    MetricaItem(
                        label = "Ultimo 30 dias",
                        valor = "$signo${"%.1f".format(it)} kg",
                        color = color
                    )
                }
            }
            Text(
                text = metricas.tendencia,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun MetricaItem(
    label: String,
    valor: String,
    color: Color = Color.Unspecified
){
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = valor,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun WeightChart(weights: List<Weight>) {

    // 1. Ordenamos ASC para la gráfica (más viejo → más reciente)
    val pesosOrdenados = weights.reversed()
    //    weights viene DESC: [hoy, ayer, hace2días]
    //    reversed()    ASC: [hace2días, ayer, hoy] ← la gráfica crece hacia la derecha

    // 2. Creamos el productor de datos de Vico
    val modelProducer = remember { CartesianChartModelProducer() }

    // 3. Cuando cambia la lista, actualizamos la gráfica
    LaunchedEffect(weights) {
        modelProducer.runTransaction {
            lineSeries {
                series(pesosOrdenados.map { it.peso.toFloat() })
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Evolución de peso",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            // 4. Renderizamos la gráfica
            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberLineCartesianLayer(), // tipo línea
                    startAxis = rememberStartAxis(), // eje Y (pesos)
                    bottomAxis = rememberBottomAxis() // eje X (tiempo)
                ),
                modelProducer = modelProducer,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }
    }
}