package com.ignaherner.pawcare.presentation.pets

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Phone
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
import com.ignaherner.pawcare.presentation.appointments.AppointmentUiState
import com.ignaherner.pawcare.presentation.appointments.AppointmentViewModel
import com.ignaherner.pawcare.presentation.condition.ConditionUiState
import com.ignaherner.pawcare.presentation.condition.ConditionViewModel
import com.ignaherner.pawcare.presentation.deworming.DewormingUiState
import com.ignaherner.pawcare.presentation.deworming.DewormingViewModel
import com.ignaherner.pawcare.presentation.medications.MedicationUiState
import com.ignaherner.pawcare.presentation.medications.MedicationViewModel
import com.ignaherner.pawcare.presentation.owners.OwnerState
import com.ignaherner.pawcare.presentation.owners.OwnerViewModel
import com.ignaherner.pawcare.presentation.vaccines.VaccineUiState
import com.ignaherner.pawcare.presentation.vaccines.VaccineViewModel
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
    onNavigateToQR: (Long) -> Unit,
    viewModel: PetViewModel = hiltViewModel(),
    ownerViewModel: OwnerViewModel = hiltViewModel(),
    weightViewModel: WeightViewModel = hiltViewModel(),
    vaccineViewModel: VaccineViewModel = hiltViewModel(),
    medicationViewModel: MedicationViewModel = hiltViewModel(),
    conditionViewModel: ConditionViewModel = hiltViewModel(),
    dewormingViewModel: DewormingViewModel = hiltViewModel(),
    appointmentViewModel: AppointmentViewModel = hiltViewModel()
) {
    val detailState by viewModel.detailState.collectAsStateWithLifecycle()
    val ownerState by ownerViewModel.ownerState.collectAsStateWithLifecycle()
    val weightState by weightViewModel.uiState.collectAsStateWithLifecycle()
    val vaccineState by vaccineViewModel.uiState.collectAsStateWithLifecycle()
    val medicationState by medicationViewModel.uiState.collectAsStateWithLifecycle()
    val conditionState by conditionViewModel.uiState.collectAsStateWithLifecycle()
    val dewormingState by dewormingViewModel.uiState.collectAsStateWithLifecycle()
    val appointmentState by appointmentViewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    LaunchedEffect(petId) {
        viewModel.loadPetById(petId)
        ownerViewModel.loadOwner()
        weightViewModel.loadWeights(petId)
        vaccineViewModel.loadVaccines(petId)
        medicationViewModel.loadMedications(petId)
        conditionViewModel.loadConditions(petId)
        dewormingViewModel.loadDewormings(petId)
        appointmentViewModel.loadAppointments(petId)
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    when (val state = detailState) {
                        is PetDetailState.Success -> Text(
                            text = state.pet.nombre,
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
            is PetDetailState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            is PetDetailState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { Text(text = state.mensaje) }
            }
            is PetDetailState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = 24.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
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

                    // Historial Clínico
                    item {
                        SectionTitle(
                            icon = Icons.Default.MedicalServices,
                            titulo = "Historial Clínico"
                        )
                    }

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
                                    count = when (val s = vaccineState) {
                                        is VaccineUiState.Success -> s.vaccines.size
                                        else -> 0
                                    },
                                    onClick = { onNavigateToVaccines(petId, state.pet.nombre) }
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            SeccionCard(
                                seccion = SeccionItem(
                                    titulo = "Medicamentos",
                                    icono = Icons.Default.LocalPharmacy,
                                    color = MedicationColor,
                                    count = when (val s = medicationState) {
                                        is MedicationUiState.Success -> s.medications.size
                                        else -> 0
                                    },
                                    onClick = { onNavigateToMedication(petId, state.pet.nombre) }
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

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
                                    color = MaterialTheme.colorScheme.error,
                                    count = when (val s = conditionState) {
                                        is ConditionUiState.Success -> s.conditions.size
                                        else -> 0
                                    },
                                    onClick = { onNavigateToConditions(petId, state.pet.nombre) }
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            SeccionCard(
                                seccion = SeccionItem(
                                    titulo = "Desparasitación",
                                    icono = Icons.Default.Vaccines,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    count = when (val s = dewormingState) {
                                        is DewormingUiState.Success -> s.dewormings.size
                                        else -> 0
                                    },
                                    onClick = { onNavigateToDeworming(petId, state.pet.nombre) }
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Seguimiento
                    item {
                        SectionTitle(
                            icon = Icons.Default.TrendingUp,
                            titulo = "Seguimiento"
                        )
                    }

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
                                    count = when (val s = weightState) {
                                        is WeightUiState.Success -> s.weights.size
                                        else -> 0
                                    },
                                    onClick = { onNavigateToWeight(petId) }
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            SeccionCard(
                                seccion = SeccionItem(
                                    titulo = "Visitas",
                                    icono = Icons.Default.CalendarMonth,
                                    color = AppointmentColor,
                                    count = when (val s = appointmentState) {
                                        is AppointmentUiState.Success -> s.appointments.size
                                        else -> 0
                                    },
                                    onClick = {
                                        onNavigateToAppointments(petId, state.pet.nombre)
                                    }
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(
    icon: ImageVector,
    titulo: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
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
private fun PetHeaderSection(
    pet: Pet,
    ultimoPeso: Weight?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
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
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

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
                AssistChip(onClick = {}, label = { Text(it) })
            }
            pet.sexo?.let {
                AssistChip(onClick = {}, label = { Text(it.displayName) })
            }
            AssistChip(
                onClick = {},
                label = { Text(calcularEdad(pet.fechaNacimiento, pet.fechaNacimientoTipo)) }
            )
            ultimoPeso?.let {
                AssistChip(
                    onClick = {},
                    label = { Text("${it.peso} kg") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.FitnessCenter,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = WeightColor.copy(alpha = 0.15f),
                        labelColor = WeightColor,
                        leadingIconContentColor = WeightColor
                    )
                )
            }
            if (pet.castrado) {
                AssistChip(
                    onClick = {},
                    label = { Text("Castrado/a") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        labelColor = MaterialTheme.colorScheme.primary,
                        leadingIconContentColor = MaterialTheme.colorScheme.primary
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
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
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
                        style = MaterialTheme.typography.bodySmall,
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
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

data class SeccionItem(
    val titulo: String,
    val icono: ImageVector,
    val color: Color,
    val count: Int? = null,
    val onClick: () -> Unit
)

@Composable
private fun SeccionCard(
    seccion: SeccionItem,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = seccion.onClick,
        modifier = modifier.fillMaxSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = seccion.color.copy(alpha = 0.12f)
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
                modifier = Modifier.size(28.dp),
                tint = seccion.color
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = seccion.titulo,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = seccion.color
            )
            seccion.count?.let {
                Text(
                    text = when (it) {
                        0 -> "Sin registros"
                        1 -> "1 registro"
                        else -> "$it registros"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = if (it > 0) seccion.color.copy(alpha = 0.7f)
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
