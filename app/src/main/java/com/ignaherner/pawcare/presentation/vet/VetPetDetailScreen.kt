package com.ignaherner.pawcare.presentation.vet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ignaherner.pawcare.domain.model.VetHistorialTipo
import com.ignaherner.pawcare.domain.model.VetPetSummary
import com.ignaherner.pawcare.presentation.components.PawCareAvatar
import com.ignaherner.pawcare.utils.calcularEdad
import com.ignaherner.pawcare.utils.toFriendlyDate


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VetPetDetailScreen(
    firestoreId: String,
    onNavigateBack: () -> Unit,
    onNavigateToHistorial: (String, VetHistorialTipo) -> Unit,
    onNavigateToOwnerDetail: (String) -> Unit,
    viewModel: VetViewModel = hiltViewModel()
) {
    val summaryState by viewModel.summaryState.collectAsStateWithLifecycle()

    LaunchedEffect(firestoreId) {
        viewModel.cargarResumen(firestoreId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Libreta Sanitaria") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
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
            when (val state = summaryState) {
                is VetSummaryState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is VetSummaryState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("😔", style = MaterialTheme.typography.displayMedium)
                        Text(state.mensaje)
                    }
                }
                is VetSummaryState.Success -> {
                    VetLibretaSanitaria(
                        summary = state.summary,
                        onNavigateToHistorial = { tipo ->
                            onNavigateToHistorial(firestoreId, tipo)
                        },
                        onNavigateToOwnerDetail = onNavigateToOwnerDetail,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun VetLibretaSanitaria(
    summary: VetPetSummary,
    onNavigateToHistorial: (VetHistorialTipo) -> Unit,
    onNavigateToOwnerDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                PawCareAvatar(
                    fotoUri = summary.pet.fotoUri,
                    nombre = summary.pet.nombre,
                    modifier = Modifier.size(80.dp)
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = summary.pet.nombre,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AssistChip(onClick = {}, label = { Text(summary.pet.especie.displayName) })
                        summary.pet.raza?.let {
                            AssistChip(onClick = {}, label = { Text(it) })
                        }
                        summary.pet.sexo?.let {
                            AssistChip(onClick = {}, label = { Text(it.displayName) })
                        }
                        AssistChip(
                            onClick = {},
                            label = {
                                Text(calcularEdad(summary.pet.fechaNacimiento, summary.pet.fechaNacimientoTipo))
                            }
                        )
                        if (summary.pet.castrado) {
                            AssistChip(
                                onClick = {},
                                label = { Text("Castrado/a") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }

        // Dueño
        summary.owner?.let { owner ->
            item {
                SectionHeader(
                    icon = Icons.Default.Person,
                    titulo = "Dueño"
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedCard(
                    onClick = { onNavigateToOwnerDetail(summary.pet.ownerId)},
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PawCareAvatar(
                            fotoUri = owner.fotoUri,
                            nombre = owner.nombre,
                            modifier = Modifier.size(80.dp)
                        )
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "${owner.nombre} ${owner.apellido}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Phone,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = owner.telefono,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            owner.ciudad?.let {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Resumen Clínico
        item {
            SectionHeader(
                icon = Icons.Default.MedicalServices,
                titulo = "Resumen Clínico"
            )
            Spacer(modifier = Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DashboardCard(
                        icon = Icons.Default.Favorite,
                        titulo = "Última vacuna",
                        contenido = summary.ultimaVacuna?.tipo?.displayName ?: "Sin registros",
                        subtitulo = summary.ultimaVacuna?.fechaAplicacion?.toFriendlyDate(),
                        color = Color(0xFFE91E63),
                        onClick = { onNavigateToHistorial(VetHistorialTipo.VACUNAS) },
                        modifier = Modifier.weight(1f)
                    )
                    // Medicamentos
                    DashboardCard(
                        icon = Icons.Default.LocalPharmacy,
                        titulo = "Medicamento activo",
                        contenido = summary.medicamentoActivo?.nombre ?: "Sin activos",
                        subtitulo = summary.medicamentoActivo?.dosisDisplay,
                        color = Color(0xFFE91E63),
                        onClick = { onNavigateToHistorial(VetHistorialTipo.MEDICAMENTOS) },
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Peso
                    DashboardCard(
                        icon = Icons.Default.FitnessCenter,
                        titulo = "Último peso",
                        contenido = summary.ultimoPeso?.let { "${it.peso} kg" } ?: "Sin registros",
                        subtitulo = summary.ultimoPeso?.fecha?.toFriendlyDate(),
                        color = Color(0xFFE91E63),
                        onClick = { onNavigateToHistorial(VetHistorialTipo.PESOS) },
                        modifier = Modifier.weight(1f)
                    )
                    // Turnos
                    DashboardCard(
                        icon = Icons.Default.CalendarMonth,
                        titulo = "Último turno",
                        contenido = summary.ultimoTurno?.motivo ?: "Sin registros",
                        subtitulo = summary.ultimoTurno?.fecha?.toFriendlyDate(),
                        color = Color(0xFFE91E63),
                        onClick = { onNavigateToHistorial(VetHistorialTipo.TURNOS) },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Desparasitación
                DashboardCard(
                    icon = Icons.Default.Vaccines,
                    titulo = "Última desparasitación",
                    contenido = summary.ultimaDesparasitacion?.fecha?.toFriendlyDate() ?: "Sin registros",
                    subtitulo = summary.ultimaDesparasitacion?.producto,
                    color = MaterialTheme.colorScheme.tertiary,
                    onClick = { onNavigateToHistorial(VetHistorialTipo.DESPARASITACIONES) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Condiciones
        if (summary.condiciones.isNotEmpty()) {
            item {
                SectionHeader(
                    icon = Icons.Default.Warning,
                    titulo = "Condiciones"
                )
            }
            items(summary.condiciones) { condition ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = condition.nombre,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        condition.fechaDiagnostico?.let {
                            Text(
                                text = "Diagnóstico: ${it.toFriendlyDate()}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        condition.notas?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    icon: ImageVector,
    titulo: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = titulo,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun DashboardCard(
    icon: ImageVector,
    titulo: String,
    contenido: String,
    subtitulo: String?,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.12f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.labelSmall,
                    color = color,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = contenido,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                subtitulo?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}