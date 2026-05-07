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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.Monitor
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Vaccines
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ignaherner.pawcare.domain.model.VetHistorialTipo
import com.ignaherner.pawcare.domain.model.VetPetSummary
import com.ignaherner.pawcare.presentation.components.CategoryRow
import com.ignaherner.pawcare.presentation.components.PawCard
import com.ignaherner.pawcare.presentation.components.PawCareAvatar
import com.ignaherner.pawcare.presentation.components.PawCareIcon
import com.ignaherner.pawcare.presentation.components.PawIconSize
import com.ignaherner.pawcare.ui.theme.CatAppointment
import com.ignaherner.pawcare.ui.theme.CatAppointmentSoft
import com.ignaherner.pawcare.ui.theme.CatCondition
import com.ignaherner.pawcare.ui.theme.CatConditionSoft
import com.ignaherner.pawcare.ui.theme.CatDeworming
import com.ignaherner.pawcare.ui.theme.CatDewormingSoft
import com.ignaherner.pawcare.ui.theme.CatMedication
import com.ignaherner.pawcare.ui.theme.CatMedicationSoft
import com.ignaherner.pawcare.ui.theme.CatVaccine
import com.ignaherner.pawcare.ui.theme.CatVaccineSoft
import com.ignaherner.pawcare.ui.theme.CatWeight
import com.ignaherner.pawcare.ui.theme.CatWeightSoft
import com.ignaherner.pawcare.ui.theme.PawRadio
import com.ignaherner.pawcare.ui.theme.PawSpace
import com.ignaherner.pawcare.ui.theme.Success
import com.ignaherner.pawcare.ui.theme.VetPrimary
import com.ignaherner.pawcare.ui.theme.VetPrimaryInk
import com.ignaherner.pawcare.ui.theme.VetPrimarySoft
import com.ignaherner.pawcare.ui.theme.Warn
import com.ignaherner.pawcare.utils.calcularEdad
import com.ignaherner.pawcare.utils.diasHastaFecha
import com.ignaherner.pawcare.utils.toFriendlyDate


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VetPetDetailScreen(
    firestoreId: String,
    onNavigateBack: () -> Unit,
    onNavigateToHistorial: (String, VetHistorialTipo) -> Unit,
    onNavigateToOwnerDetail: (String) -> Unit,
    onNavigateToVetVaccineForm: (String) -> Unit = {},
    onNavigateToVetMedicationForm: (String) -> Unit = {},
    onNavigateToVetConditionForm: (String) -> Unit = {},
    onNavigateToVetDewormingForm: (String) -> Unit = {},
    onNavigateToVetAppointmentForm: (String) -> Unit = {},
    onNavigateToVetWeightForm: (String) -> Unit = {},
    viewModel: VetViewModel = hiltViewModel()
) {
    val summaryState by viewModel.summaryState.collectAsStateWithLifecycle()
    var showQuickRegister by remember { mutableStateOf(false) }

    LaunchedEffect(firestoreId) {
        viewModel.cargarResumen(firestoreId)
    }

    // Bottom Sheet — Registro rápido
    if (showQuickRegister) {
        ModalBottomSheet(
            onDismissRequest = { showQuickRegister = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = PawSpace.xl, vertical = PawSpace.lg),
                verticalArrangement = Arrangement.spacedBy(PawSpace.lg)
            ) {
                Text(
                    text = "Registro rápido",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "¿Qué querés registrar?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Column(verticalArrangement = Arrangement.spacedBy(PawSpace.sm)) {
                    QuickRegisterOption(
                        icon = Icons.Outlined.Vaccines,
                        title = "Vacuna",
                        color = CatVaccine,
                        colorSoft = CatVaccineSoft,
                        onClick = {
                            showQuickRegister = false
                            onNavigateToVetVaccineForm(firestoreId)
                        }
                    )
                    QuickRegisterOption(
                        icon = Icons.Outlined.Medication,
                        title = "Medicamento",
                        color = CatMedication,
                        colorSoft = CatMedicationSoft,
                        onClick = {
                            showQuickRegister = false
                            onNavigateToVetMedicationForm(firestoreId)
                        }
                    )
                    QuickRegisterOption(
                        icon = Icons.Outlined.HealthAndSafety,
                        title = "Condición",
                        color = CatCondition,
                        colorSoft = CatConditionSoft,
                        onClick = {
                            showQuickRegister = false
                            onNavigateToVetConditionForm(firestoreId)
                        }
                    )
                    QuickRegisterOption(
                        icon = Icons.Outlined.Shield,
                        title = "Desparasitación",
                        color = CatDeworming,
                        colorSoft = CatDewormingSoft,
                        onClick = {
                            showQuickRegister = false
                            onNavigateToVetDewormingForm(firestoreId)
                        }
                    )
                    QuickRegisterOption(
                        icon = Icons.Outlined.CalendarMonth,
                        title = "Visita",
                        color = CatAppointment,
                        colorSoft = CatAppointmentSoft,
                        onClick = {
                            showQuickRegister = false
                            onNavigateToVetAppointmentForm(firestoreId)
                        }
                    )
                    QuickRegisterOption(
                        icon = Icons.Outlined.Monitor,
                        title = "Peso",
                        color = CatWeight,
                        colorSoft = CatWeightSoft,
                        onClick = {
                            showQuickRegister = false
                            onNavigateToVetWeightForm(firestoreId)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(PawSpace.lg))
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "MODO VETERINARIO",
                        style = MaterialTheme.typography.labelMedium,
                        letterSpacing = 1.sp,
                        color = VetPrimaryInk
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showQuickRegister = true },
                containerColor = VetPrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(PawSpace.sm))
                Text(
                    text = "Registro rápido",
                    style = MaterialTheme.typography.titleSmall
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = summaryState) {
                is VetSummaryState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is VetSummaryState.Error -> {
                    Text(text = state.mensaje, modifier = Modifier.align(Alignment.Center))
                }
                is VetSummaryState.Success -> {
                    VetLibretaContent(
                        summary = state.summary,
                        firestoreId = firestoreId,
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
private fun VetLibretaContent(
    summary: VetPetSummary,
    firestoreId: String,
    onNavigateToHistorial: (VetHistorialTipo) -> Unit,
    onNavigateToOwnerDetail: (String) -> Unit,
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
        verticalArrangement = Arrangement.spacedBy(PawSpace.lg)
    ) {
        // Hero — mascota
        item {
            PawCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(PawSpace.lg),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(PawSpace.md)
                ) {
                    PawCareAvatar(
                        fotoUri = summary.pet.fotoUri,
                        nombre = summary.pet.nombre,
                        modifier = Modifier.size(64.dp)
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(PawSpace.xs)
                    ) {
                        Text(
                            text = summary.pet.nombre,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = buildList {
                                add(summary.pet.especie.displayName)
                                summary.pet.raza?.let { add(it) }
                                summary.pet.sexo?.let { add(it.displayName) }
                                add(calcularEdad(summary.pet.fechaNacimiento, summary.pet.fechaNacimientoTipo)
                                    .let { if (it.contains("desconocida", ignoreCase = true)) "Edad desconocida" else it })
                            }.joinToString(" · "),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Código pill
                        if (summary.pet.codigo.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(PawRadio.xs),
                                color = VetPrimarySoft
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = PawSpace.sm, vertical = 2.dp),
                                    horizontalArrangement = Arrangement.spacedBy(PawSpace.xs),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    PawCareIcon(
                                        icon = Icons.Outlined.QrCode2,
                                        contentDescription = null,
                                        size = PawIconSize.small,
                                        tint = VetPrimaryInk
                                    )
                                    Text(
                                        text = summary.pet.codigo,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = VetPrimaryInk,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Dashboard grid
        item {
            Text(
                text = "DASHBOARD",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )
        }

        item {
            PawCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        // Peso
                        DashboardStat(
                            label = "PESO",
                            value = summary.ultimoPeso?.let { "${it.peso}" } ?: "—",
                            unit = summary.ultimoPeso?.let { "kg" },
                            extra = if (summary.ultimoPeso != null) "+0.3" else null,
                            extraColor = Success,
                            modifier = Modifier.weight(1f)
                        )

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(80.dp)
                                .background(MaterialTheme.colorScheme.outlineVariant)
                        )

                        // Edad
                        DashboardStat(
                            label = "EDAD",
                            value = calcularEdad(summary.pet.fechaNacimiento, summary.pet.fechaNacimientoTipo)
                                .let { if (it.contains("desconocida", ignoreCase = true)) "—" else it },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    Row(modifier = Modifier.fillMaxWidth()) {
                        // Última visita
                        val diasDesdeVisita = summary.ultimoTurno?.fecha?.let {
                            diasHastaFecha(it) * -1 // invertir porque diasHastaFecha cuenta hacia adelante
                        }

                        DashboardStat(
                            label = "ÚLTIMA VISITA",
                            value = summary.ultimoTurno?.fecha?.toFriendlyDate()?.split(" ")?.take(2)?.joinToString(" ") ?: "—",
                            extra = diasDesdeVisita?.let { dias ->
                                when {
                                    dias == 0L -> "Hoy"
                                    dias == 1L -> "Ayer"
                                    dias > 0 -> "Hace $dias días"
                                    else -> null
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(80.dp)
                                .background(MaterialTheme.colorScheme.outlineVariant)
                        )

                        // Alertas
                        val alertCount = listOfNotNull(
                            summary.medicamentoActivo?.let { 1 }
                        ).sum()

                        DashboardStat(
                            label = "ALERTAS",
                            value = alertCount.toString(),
                            extra = if (alertCount > 0) "Atención" else null,
                            extraColor = if (alertCount > 0) Warn else null,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Tutor
        summary.owner?.let { owner ->
            item {
                Text(
                    text = "TUTOR",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )
            }

            item {
                PawCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onNavigateToOwnerDetail(summary.pet.ownerId) }
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

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${owner.nombre} ${owner.apellido}",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = owner.telefono,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Botón llamar
                        Surface(
                            shape = CircleShape,
                            color = VetPrimarySoft,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                PawCareIcon(
                                    icon = Icons.Outlined.Phone,
                                    contentDescription = "Llamar",
                                    size = PawIconSize.default,
                                    tint = VetPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Historial clínico
        item {
            Text(
                text = "Historial clínico",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(PawSpace.sm)) {
                CategoryRow(
                    icon = Icons.Outlined.Vaccines,
                    title = "Vacunas",
                    count = summary.totalVacunas,
                    hint = if (summary.totalVacunas == 0) "Sin registros"
                    else "${summary.totalVacunas} registradas",
                    color = CatVaccine,
                    colorSoft = CatVaccineSoft,
                    onClick = { onNavigateToHistorial(VetHistorialTipo.VACUNAS) }
                )

                CategoryRow(
                    icon = Icons.Outlined.Medication,
                    title = "Medicamentos",
                    count = summary.totalMedicamentos,
                    hint = summary.medicamentoActivo?.let { "${it.nombre} en curso" }
                        ?: "Sin tratamientos activos",
                    color = CatMedication,
                    colorSoft = CatMedicationSoft,
                    onClick = { onNavigateToHistorial(VetHistorialTipo.MEDICAMENTOS) }
                )

                CategoryRow(
                    icon = Icons.Outlined.HealthAndSafety,
                    title = "Condiciones",
                    count = summary.condiciones.size,
                    hint = if (summary.condiciones.isEmpty()) "Sin registros"
                    else "${summary.condiciones.size} registradas",
                    color = CatCondition,
                    colorSoft = CatConditionSoft,
                    onClick = { onNavigateToHistorial(VetHistorialTipo.CONDICIONES) }
                )

                CategoryRow(
                    icon = Icons.Outlined.Shield,
                    title = "Desparasitación",
                    count = summary.totalDesparasitaciones,
                    hint = if (summary.totalDesparasitaciones == 0) "Sin registros"
                    else "${summary.totalDesparasitaciones} aplicaciones",
                    color = CatDeworming,
                    colorSoft = CatDewormingSoft,
                    onClick = { onNavigateToHistorial(VetHistorialTipo.DESPARASITACIONES) }
                )

                CategoryRow(
                    icon = Icons.Outlined.Monitor,
                    title = "Peso",
                    count = 0,
                    hint = summary.ultimoPeso?.let { "Último: ${it.peso} kg" }
                        ?: "Sin registros",
                    color = CatWeight,
                    colorSoft = CatWeightSoft,
                    onClick = { onNavigateToHistorial(VetHistorialTipo.PESOS) }
                )

                CategoryRow(
                    icon = Icons.Outlined.CalendarMonth,
                    title = "Visitas",
                    count = 0,
                    hint = summary.ultimoTurno?.let { "Última: ${it.fecha.toFriendlyDate()}" }
                        ?: "Sin registros",
                    color = CatAppointment,
                    colorSoft = CatAppointmentSoft,
                    onClick = { onNavigateToHistorial(VetHistorialTipo.TURNOS) }
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// DASHBOARD STAT
// ═══════════════════════════════════════════════════════════
@Composable
private fun DashboardStat(
    label: String,
    value: String,
    unit: String? = null,
    extra: String? = null,
    extraColor: Color? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(PawSpace.md),
        verticalArrangement = Arrangement.spacedBy(PawSpace.xs)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(PawSpace.xs)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            unit?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
            extra?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall,
                    color = extraColor ?: MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// QUICK REGISTER OPTION
// ═══════════════════════════════════════════════════════════
@Composable
private fun QuickRegisterOption(
    icon: ImageVector,
    title: String,
    color: Color,
    colorSoft: Color,
    onClick: () -> Unit
) {
    PawCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PawSpace.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(PawSpace.md)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(PawRadio.sm))
                    .background(colorSoft),
                contentAlignment = Alignment.Center
            ) {
                PawCareIcon(
                    icon = icon,
                    contentDescription = null,
                    size = PawIconSize.medium,
                    tint = color
                )
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            PawCareIcon(
                icon = Icons.Outlined.ChevronRight,
                contentDescription = null,
                size = PawIconSize.medium,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}