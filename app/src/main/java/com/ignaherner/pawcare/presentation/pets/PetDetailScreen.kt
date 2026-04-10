package com.ignaherner.pawcare.presentation.pets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ignaherner.pawcare.domain.model.Owner
import com.ignaherner.pawcare.domain.model.Pet
import com.ignaherner.pawcare.domain.model.Weight
import com.ignaherner.pawcare.domain.model.calcularEdad
import com.ignaherner.pawcare.presentation.owners.OwnerState
import com.ignaherner.pawcare.presentation.owners.OwnerViewModel
import com.ignaherner.pawcare.presentation.weight.WeightUiState
import com.ignaherner.pawcare.presentation.weight.WeightViewModel
import com.ignaherner.pawcare.ui.theme.AppointmentColor
import com.ignaherner.pawcare.ui.theme.MedicationColor
import com.ignaherner.pawcare.ui.theme.VaccineColor
import com.ignaherner.pawcare.ui.theme.WeightColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetDetailScreen(
    petId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onNavigateToVaccines: (Long, String) -> Unit,
    onNavigateToAppointments: (Long, String) -> Unit,
    onNavigateToWeight: (Long) -> Unit,
    onNavigateToMedication: (Long, String) -> Unit,
    onNavigateToOwnerDetail: () -> Unit,
    onNavigateToConditions: (Long, String) -> Unit,
    onNavigateToDeworming: (Long, String) -> Unit,
    onNavigateToQR: (Long ) -> Unit,
    viewModel: PetViewModel = hiltViewModel(),
    ownerViewModel: OwnerViewModel = hiltViewModel(),
    weightViewModel: WeightViewModel = hiltViewModel()
) {
    val detailState by viewModel.detailState.collectAsStateWithLifecycle()
    val ownerState by ownerViewModel.ownerState.collectAsStateWithLifecycle()
    val weightState by weightViewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    LaunchedEffect(petId) {
        viewModel.loadPetById(petId)
        ownerViewModel.loadOwner()
        weightViewModel.loadWeights(petId)
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    when (val state = detailState) {
                        is PetDetailState.Success -> Text(
                            text = state.pet.nombre,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )

                        else -> Text("")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToQR(petId) }) {
                        Icon(Icons.Default.QrCode, contentDescription = "Ver QR")
                    }
                    IconButton(onClick = {
                        if (detailState is PetDetailState.Success) {
                            onNavigateToEdit(petId)
                        }
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        when (val state = detailState) {
            is PetDetailState.Loading ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

            is PetDetailState.Error ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = state.mensaje)
                }

            is PetDetailState.Success ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header
                    item {
                        PetHeaderSection(
                            pet = state.pet,
                            ultimoPeso = when (val ws = weightState) {
                                is WeightUiState.Success -> ws.weights.firstOrNull()
                                else -> null
                            }
                        )
                    }

                    // Card del dueño
                    item {
                        when (val ownerS = ownerState) {
                            is OwnerState.Success -> {
                                OwnerContactCard(
                                    owner = ownerS.owner,
                                    onClick = onNavigateToOwnerDetail
                                )
                            }
                            else -> {}
                        }
                    }

                    // Título Historial Clínico
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MedicalServices,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "Historial Clínico",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Historial fila 1
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            SeccionCard(
                                seccion = SeccionItem(
                                    titulo = "Vacunas",
                                    icono = Icons.Default.Favorite,
                                    color = VaccineColor,
                                    onClick = { onNavigateToVaccines(petId, state.pet.nombre) }
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            SeccionCard(
                                seccion = SeccionItem(
                                    titulo = "Medicamentos",
                                    icono = Icons.Default.LocalPharmacy,
                                    color = MedicationColor,
                                    onClick = { onNavigateToMedication(petId, state.pet.nombre) }
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Historial fila 2
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            SeccionCard(
                                seccion = SeccionItem(
                                    titulo = "Condiciones",
                                    icono = Icons.Default.MedicalServices,
                                    color = Color(0xFFE91E63),
                                    onClick = { onNavigateToConditions(petId, state.pet.nombre) }
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            SeccionCard(
                                seccion = SeccionItem(
                                    titulo = "Desparasitación",
                                    icono = Icons.Default.Vaccines,
                                    color = Color(0xFF795548),
                                    onClick = { onNavigateToDeworming(petId, state.pet.nombre) }
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Título Seguimiento
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "Seguimiento",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Seguimiento fila
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            SeccionCard(
                                seccion = SeccionItem(
                                    titulo = "Peso",
                                    icono = Icons.Default.FitnessCenter,
                                    color = WeightColor,
                                    onClick = { onNavigateToWeight(petId) }
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            SeccionCard(
                                seccion = SeccionItem(
                                    titulo = "Visitas",
                                    icono = Icons.Default.CalendarMonth,
                                    color = AppointmentColor,
                                    onClick = { onNavigateToAppointments(petId, state.pet.nombre) }
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
        }
    }
}


@Composable
private fun PetHeaderSection(
    pet: Pet,
    ultimoPeso: Weight?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Foto circular
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            if (pet.fotoUri != null) {
                AsyncImage(
                    model = pet.fotoUri,
                    contentDescription = "Foto de ${pet.nombre}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = pet.nombre.first().uppercaseChar().toString(),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        // Chips con FlowRow
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            AssistChip(
                onClick = {},
                label = { Text(pet.especie.displayName) }
            )
            pet.raza?.let {
                AssistChip(
                    onClick = {},
                    label = { Text(it) }
                )
            }
            pet.sexo?.let {
                AssistChip(
                    onClick = {},
                    label = { Text(it.displayName) }
                )
            }
            AssistChip(
                onClick = {},
                label = {
                    Text(calcularEdad(pet.fechaNacimiento, pet.fechaNacimientoTipo))
                }
            )
            ultimoPeso?.let {
                AssistChip(
                    onClick = {},
                    label = { Text("⚖️ ${it.peso} kg") },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = WeightColor.copy(alpha = 0.15f),
                        labelColor = WeightColor
                    )
                )
            }
            if (pet.castrado) {
                AssistChip(
                    onClick = {},
                    label = { Text("✂️ Castrado/a") },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        labelColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}

@Composable
private fun OwnerContactCard(
    owner: Owner,
    onClick: () -> Unit
) {
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                if (owner.fotoUri != null) {
                    AsyncImage(
                        model = owner.fotoUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = owner.nombre.first().uppercaseChar().toString(),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${owner.nombre} ${owner.apellido}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "📍 ${owner.ciudad}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "📞 ${owner.telefono}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}


// Data class para cada card de seccion
data class SeccionItem(
    val titulo: String,
    val icono: ImageVector,
    val color: Color,
    val onClick: () -> Unit
)

// Card de seccion
@Composable
private fun SeccionCard(
    seccion: SeccionItem,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = seccion.onClick,
        modifier = modifier
            .fillMaxSize(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = seccion.color.copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = seccion.icono,
                contentDescription = seccion.titulo,
                modifier = Modifier.size(30.dp),
                tint = seccion.color
            )
            Text(
                text = seccion.titulo,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = seccion.color
            )
        }
    }
}
