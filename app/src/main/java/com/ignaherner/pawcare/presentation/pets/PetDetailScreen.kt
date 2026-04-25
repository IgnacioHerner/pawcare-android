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
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
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
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.Monitor
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material.icons.outlined.Vaccines
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
import com.ignaherner.pawcare.domain.model.MedicationStatus
import com.ignaherner.pawcare.domain.model.Owner
import com.ignaherner.pawcare.domain.model.Pet
import com.ignaherner.pawcare.domain.model.Weight
import com.ignaherner.pawcare.presentation.appointments.AppointmentUiState
import com.ignaherner.pawcare.presentation.appointments.AppointmentViewModel
import com.ignaherner.pawcare.presentation.components.CategoryRow
import com.ignaherner.pawcare.presentation.components.PawCareAvatar
import com.ignaherner.pawcare.presentation.components.PawCareIcon
import com.ignaherner.pawcare.presentation.components.PawIconSize
import com.ignaherner.pawcare.presentation.components.Tone
import com.ignaherner.pawcare.presentation.condition.ConditionUiState
import com.ignaherner.pawcare.presentation.condition.ConditionViewModel
import com.ignaherner.pawcare.presentation.deworming.DewormingUiState
import com.ignaherner.pawcare.presentation.deworming.DewormingViewModel
import com.ignaherner.pawcare.presentation.medications.MedicationUiState
import com.ignaherner.pawcare.presentation.medications.MedicationViewModel
import com.ignaherner.pawcare.utils.calcularEdad
import com.ignaherner.pawcare.presentation.owners.OwnerState
import com.ignaherner.pawcare.presentation.owners.OwnerViewModel
import com.ignaherner.pawcare.presentation.vaccines.VaccineUiState
import com.ignaherner.pawcare.presentation.vaccines.VaccineViewModel
import com.ignaherner.pawcare.presentation.weight.WeightUiState
import com.ignaherner.pawcare.presentation.weight.WeightViewModel
import com.ignaherner.pawcare.ui.theme.PawSpace

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
    vaccineViewModel: VaccineViewModel = hiltViewModel(),
    conditionViewModel: ConditionViewModel = hiltViewModel(),
    dewormingViewModel: DewormingViewModel = hiltViewModel(),
    appointmentViewModel: AppointmentViewModel = hiltViewModel(),
    medicationViewModel: MedicationViewModel = hiltViewModel(),
    weightViewModel: WeightViewModel = hiltViewModel()
) {
    val detailState by viewModel.detailState.collectAsStateWithLifecycle()
    val ownerState by ownerViewModel.ownerState.collectAsStateWithLifecycle()
    val weightState by weightViewModel.uiState.collectAsStateWithLifecycle()
    val medicationState by medicationViewModel.uiState.collectAsStateWithLifecycle()
    val vaccineState by vaccineViewModel.uiState.collectAsStateWithLifecycle()
    val conditionState by conditionViewModel.uiState.collectAsStateWithLifecycle()
    val dewormingState by dewormingViewModel.uiState.collectAsStateWithLifecycle()
    val appointmentState by appointmentViewModel.uiState.collectAsStateWithLifecycle()
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

                    // Historial Clínico
                    item {
                        SectionTitle(
                            icon = Icons.Outlined.MedicalServices,
                            titulo = "Historial Clínico"
                        )
                    }

                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(PawSpace.sm)) {
                            val vaccineCount = when (val s = vaccineState) {
                                is VaccineUiState.Success -> s.vaccines.size
                                else -> 0
                            }
                            CategoryRow(
                                icon = Icons.Outlined.Vaccines,
                                title = "Vacunas",
                                count = vaccineCount,
                                hint = when (vaccineCount) {
                                    0 -> "Sin registros"
                                    1 -> "1 vacuna registrada"
                                    else -> "$vaccineCount vacunas registradas"
                                },
                                tone = if (vaccineCount > 0) Tone.OK else Tone.MUTED,
                                onClick = { onNavigateToVaccines(petId, state.pet.nombre) }
                            )

                            val medicationCount = when (val s = medicationState) {
                                is MedicationUiState.Success -> s.medications.size
                                else -> 0
                            }
                            val medicamentosActivos = when (val s = medicationState) {
                                is MedicationUiState.Success ->
                                    s.medications.count { it.status == MedicationStatus.ACTIVO }
                                else -> 0
                            }
                            CategoryRow(
                                icon = Icons.Outlined.Medication,
                                title = "Medicamentos",
                                count = medicationCount,
                                hint = when {
                                    medicationCount == 0 -> "Sin registros"
                                    medicamentosActivos > 0 -> "$medicamentosActivos en curso"
                                    else -> "Sin tratamientos activos"
                                },
                                tone = if (medicamentosActivos > 0) Tone.WARN else Tone.MUTED,
                                onClick = { onNavigateToMedication(petId, state.pet.nombre) }
                            )

                            val conditionCount = when (val s = conditionState) {
                                is ConditionUiState.Success -> s.conditions.size
                                else -> 0
                            }
                            CategoryRow(
                                icon = Icons.Outlined.HealthAndSafety,
                                title = "Condiciones",
                                count = conditionCount,
                                hint = when (conditionCount) {
                                    0 -> "Sin registros"
                                    1 -> "1 condición registrada"
                                    else -> "$conditionCount condiciones registradas"
                                },
                                tone = if (conditionCount > 0) Tone.WARN else Tone.MUTED,
                                onClick = { onNavigateToConditions(petId, state.pet.nombre) }
                            )

                            val dewormingCount = when (val s = dewormingState) {
                                is DewormingUiState.Success -> s.deworming.size
                                else -> 0
                            }
                            CategoryRow(
                                icon = Icons.Outlined.Shield,
                                title = "Desparasitación",
                                count = dewormingCount,
                                hint = when (dewormingCount) {
                                    0 -> "Sin registros"
                                    1 -> "1 aplicación registrada"
                                    else -> "$dewormingCount aplicaciones"
                                },
                                tone = if (dewormingCount > 0) Tone.OK else Tone.MUTED,
                                onClick = { onNavigateToDeworming(petId, state.pet.nombre) }
                            )
                        }
                    }

                    // Seguimiento
                    item {
                        SectionTitle(
                            icon = Icons.Outlined.TrendingUp,
                            titulo = "Seguimiento"
                        )
                    }

                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(PawSpace.sm)) {
                            val weightCount = when (val s = weightState) {
                                is WeightUiState.Success -> s.weights.size
                                else -> 0
                            }
                            CategoryRow(
                                icon = Icons.Outlined.Monitor,
                                title = "Peso",
                                count = weightCount,
                                hint = when (val s = weightState) {
                                    is WeightUiState.Success ->
                                        s.weights.firstOrNull()?.let { "Último: ${it.peso} kg" }
                                            ?: "Sin registros"
                                    else -> "Sin registros"
                                },
                                tone = if (weightCount > 0) Tone.INFO else Tone.MUTED,
                                onClick = { onNavigateToWeight(petId) }
                            )

                            val appointmentCount = when (val s = appointmentState) {
                                is AppointmentUiState.Success -> s.appointments.size
                                else -> 0
                            }
                            CategoryRow(
                                icon = Icons.Outlined.CalendarMonth,
                                title = "Visitas",
                                count = appointmentCount,
                                hint = when (appointmentCount) {
                                    0 -> "Sin registros"
                                    1 -> "1 visita registrada"
                                    else -> "$appointmentCount visitas registradas"
                                },
                                tone = if (appointmentCount > 0) Tone.INFO else Tone.MUTED,
                                onClick = { onNavigateToAppointments(petId, state.pet.nombre) }
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
        PawCareAvatar(
            fotoUri = pet.fotoUri,
            nombre = pet.nombre,
            modifier = Modifier.size(80.dp)
        )

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
                        containerColor = Color(0xFFE91E63).copy(alpha = 0.15f),
                        labelColor = Color(0xFFE91E63)
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
            PawCareAvatar(
                fotoUri = owner.fotoUri,
                nombre = owner.nombre,
                modifier = Modifier.size(80.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${owner.nombre}${owner.apellido}",
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


@Composable
private fun SectionTitle(
    icon: ImageVector,
    titulo: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = PawSpace.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PawSpace.sm)
    ) {
        PawCareIcon(
            icon = icon,
            contentDescription = null,
            size = PawIconSize.medium,
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = titulo,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
