package com.ignaherner.pawcare.presentation.medications

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
import androidx.compose.material3.Switch
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ignaherner.pawcare.domain.model.DosisUnidad
import com.ignaherner.pawcare.domain.model.Medication
import com.ignaherner.pawcare.domain.model.MedicationStatus
import com.ignaherner.pawcare.domain.model.ViaAdministracion
import com.ignaherner.pawcare.utils.fechaHoy
import com.ignaherner.pawcare.utils.toFormattedString
import com.ignaherner.pawcare.presentation.settings.SettingsViewModel
import com.ignaherner.pawcare.presentation.vet.VetProfileViewModel
import com.ignaherner.pawcare.presentation.vet.VetState
import com.ignaherner.pawcare.ui.theme.PawRadii
import com.ignaherner.pawcare.ui.theme.PawSpace

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationFormScreen(
    onNavigateBack: () -> Unit,
    onSave:(Medication) -> Unit,
    medicationId: Long? = null,
    isVetMode: Boolean = false,
    medicationViewModel: MedicationViewModel? = null,
    vetProfileViewModel: VetProfileViewModel? = null
) {
    var nombre by remember { mutableStateOf("") }
    var dosisCantidad by remember { mutableStateOf("1") }
    var dosisUnidad by remember { mutableStateOf(DosisUnidad.COMPRIMIDO) }
    var viaAdministracion by remember { mutableStateOf(ViaAdministracion.ORAL) }
    var esUnicaDosis by remember { mutableStateOf(false) }
    var duracionDias by remember { mutableStateOf("") }
    var intervaloHoras by remember { mutableStateOf("") }
    var fechaInicio by remember { mutableStateOf(fechaHoy()) }
    var recetadoPor by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }
    var firestoreId by remember { mutableStateOf("") }

    var unidadDropdownExpanded by remember { mutableStateOf(false) }

    val medicationDetailState = medicationViewModel?.medicationDetailState?.collectAsStateWithLifecycle()

    LaunchedEffect(medicationId) {
        medicationId?.let { medicationViewModel?.loadMedicationById(it) }
    }

    LaunchedEffect(medicationDetailState?.value) {
        if (medicationDetailState?.value is MedicationDetailState.Success) {
            val medication = (medicationDetailState.value as MedicationDetailState.Success).medication
            nombre = medication.nombre
            dosisCantidad = if (medication.dosisCantidad == medication.dosisCantidad.toInt().toDouble()) {
                medication.dosisCantidad.toInt().toString()
            } else {
                medication.dosisCantidad.toString()
            }
            dosisUnidad = medication.dosisUnidad
            viaAdministracion = medication.viaAdministracion
            fechaInicio = medication.fechaInicio
            duracionDias = medication.duracionDias.toString()
            intervaloHoras = medication.intervaloHoras.toString()
            recetadoPor = medication.recetadoPor ?: ""
            esUnicaDosis = medication.esUnicaDosis
            notas = medication.notas ?: ""
            firestoreId = medication.firestoreId
        }
    }

    val vetState = vetProfileViewModel?.vetState?.collectAsStateWithLifecycle()

    LaunchedEffect(vetState?.value) {
        if (vetState?.value is VetState.Success && recetadoPor.isBlank()) {
            val vet = (vetState.value as VetState.Success).vet
            recetadoPor = "Dr. ${vet.nombre} ${vet.apellido}"
        }
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    var showDatePicker by remember { mutableStateOf(false) }

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
                        fechaInicio = localDate.toFormattedString()
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
        isVetMode -> "Nueva medicacion"
        medicationId == null -> "Nueva medicacion"
        else -> "Editar medicacion"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {Text(titleText)},
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
            // Nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del medicamento") },
                placeholder = { Text("Ej: Amoxicilina") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                )
            )

            // Dosis: cantidad + unidad
            Column(verticalArrangement = Arrangement.spacedBy(PawSpace.sm)) {
                Text(
                    text = "Dosis",
                    style = MaterialTheme.typography.titleSmall
                )
                Row(horizontalArrangement = Arrangement.spacedBy(PawSpace.sm)) {
                    OutlinedTextField(
                        value = dosisCantidad,
                        onValueChange = { dosisCantidad = it },
                        label = { Text("Cantidad") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f),

                    )
                    ExposedDropdownMenuBox(
                        expanded = unidadDropdownExpanded,
                        onExpandedChange = { unidadDropdownExpanded = it },
                        modifier = Modifier.weight(1.5f)
                    ) {
                        OutlinedTextField(
                            value = dosisUnidad.displayName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Unidad") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = unidadDropdownExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = unidadDropdownExpanded,
                            onDismissRequest = { unidadDropdownExpanded = false }
                        ) {
                            DosisUnidad.entries.forEach { unidad ->
                                DropdownMenuItem(
                                    text = { Text(unidad.displayName) },
                                    onClick = {
                                        dosisUnidad = unidad
                                        unidadDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Vía de administración
            Column(verticalArrangement = Arrangement.spacedBy(PawSpace.sm)) {
                Text(
                    text = "Vía de administración",
                    style = MaterialTheme.typography.titleSmall
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(PawSpace.sm),
                    verticalArrangement = Arrangement.spacedBy(PawSpace.sm)
                ) {
                    ViaAdministracion.entries.forEach { via ->
                        FilterChip(
                            selected = viaAdministracion == via,
                            onClick = { viaAdministracion = via },
                            label = { Text(via.displayName) }
                        )
                    }
                }
            }

            // Única dosis
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "¿Es de única dosis?",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Se administra una sola vez",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = esUnicaDosis,
                    onCheckedChange = {
                        esUnicaDosis = it
                        if (it) {
                            duracionDias = "0"
                            intervaloHoras = "0"
                        }
                    }
                )
            }

            // Duración + intervalo si NO es única dosis
            if (!esUnicaDosis) {
                Row(horizontalArrangement = Arrangement.spacedBy(PawSpace.sm)) {
                    OutlinedTextField(
                        value = duracionDias,
                        onValueChange = { duracionDias = it },
                        label = { Text("Días") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = intervaloHoras,
                        onValueChange = { intervaloHoras = it },
                        label = { Text("Cada X horas") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Fecha de inicio
            OutlinedTextField(
                value = fechaInicio,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                label = { Text("Fecha de inicio") },
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


            // Recetado por
            OutlinedTextField(
                value = recetadoPor,
                onValueChange = { recetadoPor = it },
                label = { Text("Recetado por (opcional)") },
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

            Button(
                onClick = {
                    val medication = Medication(
                        id = medicationId ?: 0L,
                        firestoreId = firestoreId,
                        petId = 0L,
                        nombre = nombre.trim(),
                        dosisCantidad = dosisCantidad.toDoubleOrNull() ?: 1.0,
                        dosisUnidad = dosisUnidad,
                        viaAdministracion = viaAdministracion,
                        esUnicaDosis = esUnicaDosis,
                        fechaInicio = fechaInicio,
                        duracionDias = if (esUnicaDosis) 0 else duracionDias.toIntOrNull() ?: 1,
                        intervaloHoras = if (esUnicaDosis) 0 else intervaloHoras.toIntOrNull() ?: 8,
                        recetadoPor = recetadoPor.trim().ifBlank { null },
                        notas = notas.trim().ifBlank { null }
                    )
                    onSave(medication)
                    onNavigateBack()
                },
                enabled = nombre.isNotBlank() && dosisCantidad.isNotBlank() &&
                        (esUnicaDosis || (duracionDias.isNotBlank() && intervaloHoras.isNotBlank())),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(PawRadii.md)
            ) {
                Text(
                    text = if (medicationId == null) "Guardar" else "Actualizar",
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}