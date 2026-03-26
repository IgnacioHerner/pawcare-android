package com.ignaherner.pawcare.presentation.vaccines

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ignaherner.pawcare.domain.model.Vaccine
import com.ignaherner.pawcare.domain.model.VaccineStatus
import com.ignaherner.pawcare.domain.model.calcularProximaDosis
import com.ignaherner.pawcare.domain.model.displayName
import com.ignaherner.pawcare.domain.model.fechaHoy
import com.ignaherner.pawcare.domain.model.toFormattedString
import com.ignaherner.pawcare.presentation.settings.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaccineFormScreen(
    petId: Long,
    petName: String,
    vaccineId: Long? = null,
    onNavigateBack: () -> Unit,
    viewModel: VaccineViewModel,
    settingsViewModel: SettingsViewModel = hiltViewModel()
){
    // Estado local del formulario
    var nombre by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf(fechaHoy()) }
    var esAnual by remember { mutableStateOf(false) }
    var proximaDosis by remember { mutableStateOf("") }
    var veterinario by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }
    var statusSeleccionado by remember { mutableStateOf<VaccineStatus>(VaccineStatus.Pendiente) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    // Estado para controlar si el dialog esta abierto
    var showDatePicker by remember { mutableStateOf(false) }

    val nombreVeterinarioState by settingsViewModel.nombreVeterinario.collectAsStateWithLifecycle()

    // Colecta el detailState
    val vaccineDetailState by viewModel.vaccineDetailState.collectAsStateWithLifecycle()

    // Cargar la vacuna si estamos editando
    LaunchedEffect(vaccineId) {
        vaccineId?.let{ viewModel.loadVaccineById(it)}
    }

    LaunchedEffect(vaccineDetailState) {
        if(vaccineDetailState is VaccineDetailState.Success) {
            val vaccine = (vaccineDetailState as VaccineDetailState.Success).vaccine
            nombre = vaccine.nombre
            fecha = vaccine.fecha ?: ""
            esAnual = vaccine.esAnual
            veterinario = vaccine.veterinario ?: ""
            notas = vaccine.notas ?: ""
            statusSeleccionado = vaccine.status
        }
    }

    LaunchedEffect(nombreVeterinarioState) {
        if (veterinario.isNotBlank()) {
            veterinario = nombreVeterinarioState
        }
    }

    // DatePickerState
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    // Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false},
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val localDate = java.time.Instant
                                .ofEpochMilli(millis)
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate()
                            fecha = localDate.toFormattedString()
                        }
                        showDatePicker = false
                    }
                ) { Text("Aceptar")}
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false}) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    val statusOptions = listOf(
        VaccineStatus.Pendiente,
        VaccineStatus.Programada(""),
        VaccineStatus.Aplicada("")
    )
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                        Text( if(vaccineId == null) "Nueva Vacuna" else "Editar Vacunar")
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it},
                label = { Text("Nombre de la vacuna")},
                modifier = Modifier.fillMaxWidth()
            )

            // Fecha
            OutlinedTextField(
                value = fecha,
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha de aplicación")},
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true}) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Elegir fecha"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable{ showDatePicker = true}
            )

            // Switch anual
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column{
                    Text(
                        text = "Vacuna anual",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Calcula la proxima dosis automaticamente",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = esAnual,
                    onCheckedChange = { esAnual = it}
                )
            }

            // Proxima dosis - solo si es anual
            if(esAnual && fecha.isNotBlank()) {
                OutlinedTextField(
                    value = calcularProximaDosis(fecha),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Proxima dosis")},
                    modifier = Modifier.fillMaxWidth()
                )
            }

//            // Proxima dosis
//            OutlinedTextField(
//                value = proximaDosis,
//                onValueChange = { proximaDosis = it},
//                label = { Text("Proxima dosis (dd/mm/yyyy)")},
//                modifier = Modifier.fillMaxWidth()
//            )

            // Veterinario
            OutlinedTextField(
                value = nombreVeterinarioState,
                onValueChange = {},
                label = { Text("Veterinario")},
                modifier = Modifier.fillMaxWidth()
            )

            // Notas opcional
            OutlinedTextField(
                value = notas,
                onValueChange = { notas = it},
                label = { Text("Notas")},
                modifier = Modifier.fillMaxWidth()
            )

            // Dropdown status de la vacuna
            ExposedDropdownMenuBox(
                expanded = dropdownExpanded,
                onExpandedChange = { dropdownExpanded = it}
            ) {
                OutlinedTextField(
                    value = statusSeleccionado.displayName(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Estado")},
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded =  dropdownExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false}
                ) {
                    statusOptions.forEach { status ->
                        DropdownMenuItem(
                            text = { Text(status.displayName()) },
                            onClick = {
                                statusSeleccionado = status
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Button(
                onClick = {
                    val nuevaVacuna = Vaccine(
                        id = vaccineId ?: 0L,
                        petId = petId,
                        nombre = nombre,
                        fecha = fecha.ifBlank { null },
                        esAnual = esAnual,
                        proximaDosis = if (esAnual) calcularProximaDosis(fecha) else null,
                        veterinario = veterinario.ifBlank { null },
                        notas = notas.ifBlank { null },
                        status = statusSeleccionado
                    )
                    if (vaccineId == null){
                        viewModel.insertVaccine(nuevaVacuna, petName)
                    } else {
                        viewModel.updateVaccine(nuevaVacuna, petName)
                    }
                    onNavigateBack()
                },
                enabled = nombre.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (vaccineId == null) "Guardar" else "Actualizar")
            }
        }
    }
}