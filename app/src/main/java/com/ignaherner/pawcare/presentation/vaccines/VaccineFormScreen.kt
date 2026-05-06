package com.ignaherner.pawcare.presentation.vaccines

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ignaherner.pawcare.domain.model.Especie
import com.ignaherner.pawcare.domain.model.FrecuenciaVacuna
import com.ignaherner.pawcare.domain.model.TipoVacuna
import com.ignaherner.pawcare.domain.model.Vaccine
import com.ignaherner.pawcare.presentation.components.PawCareIcon
import com.ignaherner.pawcare.presentation.components.PawIconSize
import com.ignaherner.pawcare.presentation.pets.PetDetailState
import com.ignaherner.pawcare.presentation.pets.PetViewModel
import com.ignaherner.pawcare.utils.fechaHoy
import com.ignaherner.pawcare.utils.toFormattedString
import com.ignaherner.pawcare.presentation.settings.SettingsViewModel
import com.ignaherner.pawcare.presentation.vet.VetProfileViewModel
import com.ignaherner.pawcare.presentation.vet.VetState
import com.ignaherner.pawcare.ui.theme.PawRadii
import com.ignaherner.pawcare.ui.theme.PawSpace
import com.ignaherner.pawcare.utils.calcularProximaDosisConFrecuencia

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaccineFormScreen(
    onNavigateBack: () -> Unit,
    onSave: (Vaccine) -> Unit,
    vaccineId: Long? = null,
    isVetMode: Boolean = false,
    especie: Especie = Especie.PERRO,
    vaccineViewModel: VaccineViewModel? = null,
    vetProfileViewModel: VetProfileViewModel? = null
) {
    var tipoSeleccionado by remember { mutableStateOf<TipoVacuna?>(null) }
    var nombreComercial by remember { mutableStateOf("") }
    var fechaAplicacion by remember { mutableStateOf(fechaHoy()) }
    var frecuencia by remember { mutableStateOf(FrecuenciaVacuna.ANUAL) }
    var veterinario by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }
    var firestoreId by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    var tipoDropdownExpanded by remember { mutableStateOf(false) }

    // Tipos disponibles según especie
    val tiposDisponibles = remember(especie) {
        TipoVacuna.porEspecie(especie)
    }

    // Cargar vacuna si edita (solo dueño)
    val vaccineDetailState = vaccineViewModel?.vaccineDetailState?.collectAsStateWithLifecycle()

    LaunchedEffect(vaccineId) {
        vaccineId?.let { vaccineViewModel?.loadVaccineById(it) }
    }

    LaunchedEffect(vaccineDetailState?.value) {
        if (vaccineDetailState?.value is VaccineDetailState.Success) {
            val vaccine = (vaccineDetailState.value as VaccineDetailState.Success).vaccine
            tipoSeleccionado = vaccine.tipo
            nombreComercial = vaccine.nombreComercial ?: ""
            fechaAplicacion = vaccine.fechaAplicacion
            frecuencia = vaccine.frecuencia
            veterinario = vaccine.veterinario ?: ""
            notas = vaccine.notas ?: ""
            firestoreId = vaccine.firestoreId
        }
    }

    // Auto-fill veterinario (solo vet)
    val vetState = vetProfileViewModel?.vetState?.collectAsStateWithLifecycle()

    LaunchedEffect(vetState?.value) {
        if (vetState?.value is VetState.Success && veterinario.isBlank()) {
            val vet = (vetState.value as VetState.Success).vet
            veterinario = "Dr. ${vet.nombre} ${vet.apellido}"
        }
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val localDate = java.time.Instant
                            .ofEpochMilli(millis)
                            .atZone(java.time.ZoneId.of("UTC"))
                            .toLocalDate()
                        fechaAplicacion = localDate.toFormattedString()
                    }
                    showDatePicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    val titleText = when{
        isVetMode -> "Nueva vacuna"
        vaccineId == null -> "Nueva vacuna"
        else -> "Editar vacuna"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titleText) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(PawSpace.lg)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(PawSpace.lg)
        ) {
            // Tipo de vacuna - dropdown
            ExposedDropdownMenuBox(
                expanded = tipoDropdownExpanded,
                onExpandedChange = { tipoDropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = tipoSeleccionado?.displayName ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo de vacuna") },
                    placeholder = { Text("Seleccioná una vacuna") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = tipoDropdownExpanded)
                    },
                    supportingText = tipoSeleccionado?.let {
                        { Text(it.descripcion) }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = tipoDropdownExpanded,
                    onDismissRequest = { tipoDropdownExpanded = false }
                ) {
                    tiposDisponibles.forEach { tipo ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(
                                        text = tipo.displayName,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = tipo.descripcion,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            onClick = {
                                tipoSeleccionado = tipo
                                tipoDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Nombre comercial - opcional
            OutlinedTextField(
                value = nombreComercial,
                onValueChange = { nombreComercial = it },
                label = { Text("Nombre comercial (opcional)") },
                placeholder = { Text("Ej: Nobivac DHPPi") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

            // Fecha de aplicación
            OutlinedTextField(
                value = fechaAplicacion,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                label = { Text("Fecha de aplicacion") },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Elegir fecha")
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }
            )

            // Frecuencia - chips
            Column(verticalArrangement = Arrangement.spacedBy(PawSpace.sm)) {
                Text(
                    text = "Frecuencia",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(PawSpace.sm),
                    verticalArrangement = Arrangement.spacedBy(PawSpace.sm)
                ) {
                    FrecuenciaVacuna.entries.forEach { freq ->
                        FilterChip(
                            selected = frecuencia == freq,
                            onClick = { frecuencia = freq },
                            label = { Text(freq.displayName) }
                        )
                    }
                }
            }

            // Próxima dosis calculada
            if (frecuencia != FrecuenciaVacuna.UNICA && fechaAplicacion.isNotBlank()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(PawRadii.sm)
                ) {
                    Row(
                        modifier = Modifier.padding(PawSpace.md),
                        horizontalArrangement = Arrangement.spacedBy(PawSpace.sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PawCareIcon(
                            icon = Icons.Outlined.Schedule,
                            contentDescription = null,
                            size = PawIconSize.medium,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Column {
                            Text(
                                text = "Próxima dosis",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = calcularProximaDosisConFrecuencia(fechaAplicacion, frecuencia),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            // Veterinario
            OutlinedTextField(
                value = veterinario,
                onValueChange = { veterinario = it },
                label = { Text("Veterinario (opcional)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

            // Notas
            OutlinedTextField(
                value = notas,
                onValueChange = { notas = it },
                label = { Text("Notas (opcional)") },
                minLines = 2,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth()
            )

            // Botón guardar
            Button(
                onClick = {
                    val tipo = tipoSeleccionado ?: return@Button
                    val proximaDosis = if (frecuencia != FrecuenciaVacuna.UNICA) {
                        calcularProximaDosisConFrecuencia(fechaAplicacion, frecuencia)
                    } else null

                    val vaccine = Vaccine(
                        id = vaccineId ?: 0L,
                        firestoreId = firestoreId,
                        petId = 0L,
                        tipo = tipo,
                        nombreComercial = nombreComercial.trim().ifBlank { null },
                        fechaAplicacion = fechaAplicacion,
                        frecuencia = frecuencia,
                        proximaDosis = proximaDosis,
                        veterinario = veterinario.trim().ifBlank { null },
                        notas = notas.trim().ifBlank { null }
                    )
                    onSave(vaccine)
                    onNavigateBack()
                },
                enabled = tipoSeleccionado != null && fechaAplicacion.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(PawRadii.md)
            ) {
                Text(
                    text = if (vaccineId == null) "Guardar" else "Actualizar",
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}
