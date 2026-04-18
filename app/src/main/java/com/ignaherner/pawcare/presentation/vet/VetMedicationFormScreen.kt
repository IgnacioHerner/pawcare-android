package com.ignaherner.pawcare.presentation.vet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ignaherner.pawcare.domain.model.Medication
import com.ignaherner.pawcare.domain.model.MedicationStatus
import com.ignaherner.pawcare.domain.model.fechaHoy
import com.ignaherner.pawcare.domain.model.toFormattedString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VetMedicationFormScreen(
    petFirestoreId: String,
    onNavigateBack: () -> Unit,
    viewModel: VetViewModel = hiltViewModel()
) {
    var nombre by remember { mutableStateOf("") }
    var fechaInicio by remember { mutableStateOf(fechaHoy()) }
    var duracionDias by remember { mutableStateOf("") }
    var intervaloHoras by remember { mutableStateOf("") }
    var dosis by remember { mutableStateOf("") }
    var esUnicaDosis by remember { mutableStateOf(false) }
    var recetadoPor by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }
    var statusSeleccionado by remember { mutableStateOf(MedicationStatus.ACTIVO) }
    var dosisExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )
    val opcionesDosis = listOf("1/8", "1/4", "1/2", "1", "1½", "2", "3", "4")

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val localDate = java.time.Instant
                            .ofEpochMilli(millis)
                            .atZone(java.time.ZoneId.systemDefault())
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo medicamento") },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del medicamento") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("¿Es de única dosis?", style = MaterialTheme.typography.bodyLarge)
                    Text("Se administra una sola vez",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(checked = esUnicaDosis, onCheckedChange = {
                    esUnicaDosis = it
                    if (it) { duracionDias = "1"; intervaloHoras = "0" }
                })
            }

            if (!esUnicaDosis) {
                OutlinedTextField(
                    value = duracionDias,
                    onValueChange = { duracionDias = it },
                    label = { Text("Duración en días") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = intervaloHoras,
                    onValueChange = { intervaloHoras = it },
                    label = { Text("Cada cuántas horas") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            ExposedDropdownMenuBox(
                expanded = dosisExpanded,
                onExpandedChange = { dosisExpanded = it }
            ) {
                OutlinedTextField(
                    value = dosis,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Dosis") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = dosisExpanded)
                    },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = dosisExpanded,
                    onDismissRequest = { dosisExpanded = false }
                ) {
                    opcionesDosis.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text("$opcion comprimido/s") },
                            onClick = { dosis = opcion; dosisExpanded = false }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = fechaInicio,
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha de inicio") },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Fecha")
                    }
                },
                modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true }
            )

            OutlinedTextField(
                value = recetadoPor,
                onValueChange = { recetadoPor = it },
                label = { Text("Recetado por") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = notas,
                onValueChange = { notas = it },
                label = { Text("Notas (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Button(
                onClick = {
                    val nuevoMed = Medication(
                        id = 0L,
                        firestoreId = "",
                        petId = 0L,
                        nombre = nombre,
                        fechaInicio = fechaInicio,
                        duracionDias = if (esUnicaDosis) 1 else duracionDias.toIntOrNull() ?: 1,
                        intervaloHoras = if (esUnicaDosis) 0 else intervaloHoras.toIntOrNull() ?: 8,
                        dosis = dosis,
                        esUnicaDosis = esUnicaDosis,
                        notas = notas.ifBlank { null },
                        recetadoPor = recetadoPor.ifBlank { null },
                        status = statusSeleccionado
                    )
                    viewModel.guardarMedicamento(nuevoMed, petFirestoreId)
                    onNavigateBack()
                },
                enabled = nombre.isNotBlank() && dosis.isNotBlank() &&
                        (esUnicaDosis || (duracionDias.isNotBlank() && intervaloHoras.isNotBlank())),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Guardar") }
        }
    }
}
