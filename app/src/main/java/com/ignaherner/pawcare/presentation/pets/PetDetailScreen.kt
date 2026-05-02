package com.ignaherner.pawcare.presentation.pets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Cake
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.Monitor
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material.icons.outlined.Vaccines
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ignaherner.pawcare.domain.model.MedicationStatus
import com.ignaherner.pawcare.domain.model.Owner
import com.ignaherner.pawcare.domain.model.Pet
import com.ignaherner.pawcare.domain.model.Weight
import com.ignaherner.pawcare.presentation.appointments.AppointmentUiState
import com.ignaherner.pawcare.presentation.appointments.AppointmentViewModel
import com.ignaherner.pawcare.presentation.components.CategoryRow
import com.ignaherner.pawcare.presentation.components.PawCard
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
import com.ignaherner.pawcare.ui.theme.*

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
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "LIBRETA SANITARIA",
                        style = MaterialTheme.typography.labelMedium,
                        letterSpacing = 1.sp
                    )
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
                actions = {
                    IconButton(onClick = { onNavigateToQR(petId) }) {
                        PawCareIcon(
                            icon = Icons.Outlined.QrCode,
                            contentDescription = "Ver QR",
                            size = PawIconSize.medium
                        )
                    }
                    IconButton(onClick = {
                        if (detailState is PetDetailState.Success) {
                            onNavigateToEdit(petId)
                        }
                    }) {
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
                        start = PawSpace.lg,
                        end = PawSpace.lg,
                        top = PawSpace.sm,
                        bottom = PawSpace.xxl
                    ),
                    verticalArrangement = Arrangement.spacedBy(PawSpace.lg)
                ) {
                    // Hero — foto centrada + nombre + subtítulo
                    item {
                        PetHeroSection(pet = state.pet)
                    }

                    // VitalStat grid
                    item {
                        PetVitalStats(
                            pet = state.pet,
                            ultimoPeso = when (val ws = weightState) {
                                is WeightUiState.Success -> ws.weights.firstOrNull()
                                else -> null
                            },
                            pesoAnterior = when (val ws = weightState) {
                                is WeightUiState.Success -> ws.weights.getOrNull(1)
                                else -> null
                            }
                        )
                    }

                    // Card tutor
                    item {
                        when (val ownerS = ownerState) {
                            is OwnerState.Success -> {
                                TutorCard(
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
                                color = CatVaccine,
                                colorSoft = CatVaccineSoft,
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
                                color = CatMedication,
                                colorSoft = CatMedicationSoft,
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
                                color = CatCondition,
                                colorSoft = CatConditionSoft,
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
                                color = CatDeworming,
                                colorSoft = CatDewormingSoft,
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
                                color = CatWeight,
                                colorSoft = CatWeightSoft,
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
                                color = CatAppointment,
                                colorSoft = CatAppointmentSoft,
                                onClick = { onNavigateToAppointments(petId, state.pet.nombre) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// HERO — foto centrada + nombre + metadata
// ═══════════════════════════════════════════════════════════
@Composable
private fun PetHeroSection(pet: Pet) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PawSpace.sm)
    ) {
        // Foto grande centrada
        PawCareAvatar(
            fotoUri = pet.fotoUri,
            nombre = pet.nombre,
            modifier = Modifier.size(92.dp),
            textStyle = MaterialTheme.typography.displayMedium
        )

        // Nombre
        Text(
            text = pet.nombre,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Subtítulo: Especie · Raza · Sexo
        Text(
            text = buildList {
                add(pet.especie.displayName)
                pet.raza?.let { add(it) }
                pet.sexo?.let { add(it.displayName) }
            }.joinToString(" · "),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ═══════════════════════════════════════════════════════════
// VITAL STATS — grid 3 columnas (peso, edad, código)
// ═══════════════════════════════════════════════════════════
@Composable
private fun PetVitalStats(
    pet: Pet,
    ultimoPeso: Weight?,
    pesoAnterior: Weight?
) {
    PawCard(modifier = Modifier.fillMaxWidth()){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PawSpace.lg),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Peso actual
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    PawCareIcon(
                        icon = Icons.Outlined.Monitor,
                        contentDescription = null,
                        size = PawIconSize.small,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "PESO ACTUAL",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (ultimoPeso != null) {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "${ultimoPeso.peso}",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "kg",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        // Diferencia con peso anterior
                        pesoAnterior?.let {
                            val diff = ultimoPeso.peso - it.peso
                            val signo = if (diff >= 0) "+" else ""
                            Text(
                                text = "$signo${"%.1f".format(diff)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (diff >= 0) Success else Danger
                            )
                        }
                    }
                } else {
                    Text(
                        text = "—",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Divisor vertical
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(48.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )

            // Edad
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    PawCareIcon(
                        icon = Icons.Outlined.Cake,
                        contentDescription = null,
                        size = PawIconSize.small,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "EDAD",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = calcularEdad(pet.fechaNacimiento, pet.fechaNacimientoTipo)
                        .let { if (it.contains("desconocida", ignoreCase = true)) "—" else it },
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Divisor vertical
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(48.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )

            // Código
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    PawCareIcon(
                        icon = Icons.Outlined.Tag,
                        contentDescription = null,
                        size = PawIconSize.small,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "CÓDIGO",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = pet.codigo.ifBlank { "—" },
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// TUTOR CARD — datos del dueño
// ═══════════════════════════════════════════════════════════
@Composable
private fun TutorCard(
    owner: Owner,
    onClick: () -> Unit
) {
        PawCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = onClick
        ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PawSpace.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(PawSpace.md)
        ) {
            PawCareAvatar(
                fotoUri = owner.fotoUri,
                nombre = owner.nombre,
                modifier = Modifier.size(48.dp)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(PawSpace.xs)
            ) {
                Text(
                    text = "TUTOR",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${owner.nombre} ${owner.apellido}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                owner.ciudad?.let {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(PawSpace.xs),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PawCareIcon(
                            icon = Icons.Outlined.LocationOn,
                            contentDescription = null,
                            size = PawIconSize.small,
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

            PawCareIcon(
                icon = Icons.Outlined.ChevronRight,
                contentDescription = null,
                size = PawIconSize.medium,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════
// SECTION TITLE
// ═══════════════════════════════════════════════════════════
@Composable
private fun SectionTitle(
    icon: ImageVector,
    titulo: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
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
