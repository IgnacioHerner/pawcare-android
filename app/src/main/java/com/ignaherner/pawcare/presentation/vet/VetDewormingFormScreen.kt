package com.ignaherner.pawcare.presentation.vet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ignaherner.pawcare.domain.model.Deworming
import com.ignaherner.pawcare.domain.model.fechaHoy
import com.ignaherner.pawcare.domain.model.toFormattedString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VetDewormingFormScreen(
    petFirestoreId: String,
    onNavigateBack: () -> Unit,
    viewModel: VetViewModel = hiltViewModel()
) {
    var fecha by remember { mutableStateOf(fechaHoy()) }
    var producto by remember { mutableStateOf("") }
    var proximaFecha by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }
    var showFechaPicker by remember { mutableStateOf(false) }
    var showProximaPicker by remember { mutableStateOf(false) }
    val fechaPickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )
    val proximaPickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    if (showFechaPicker) {
        DatePickerDialog(
            onDismissRequest = { showFechaPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    fechaPickerState.selectedDateMillis?.let { millis ->
                        val localDate = java.time.Instant
                            .ofEpochMilli(millis)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                        fecha = localDate.toFormattedString()
                    }
                    showFechaPicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showFechaPicker = false }) { Text("Cancelar") }
            }
        ) { DatePicker(state = fechaPickerState) }
    }

    if (showProximaPicker) {
        DatePickerDialog(
            onDismissRequest = { showProximaPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    proximaPickerState.selectedDateMillis?.let { millis ->
                        val localDate = java.time.Instant
                            .ofEpochMilli(millis)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                        proximaFecha = localDate.toFormattedString()
                    }
                    showProximaPicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showProximaPicker = false }) { Text("Cancelar") }
            }
        ) { DatePicker(state = proximaPickerState) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva desparasitación") },
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
            OutlinedTextField(
                value = fecha,
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha de desparasitación") },
                trailingIcon = {
                    IconButton(onClick = { showFechaPicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Fecha")
                    }
                },
                modifier = Modifier.fillMaxWidth().clickable { showFechaPicker = true }
            )

            OutlinedTextField(
                value = producto,
                onValueChange = { producto = it },
                label = { Text("Producto (opcional)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = proximaFecha,
                onValueChange = {},
                readOnly = true,
                label = { Text("Próxima fecha (opcional)") },
                trailingIcon = {
                    IconButton(onClick = { showProximaPicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Fecha")
                    }
                },
                modifier = Modifier.fillMaxWidth().clickable { showProximaPicker = true }
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
                    val nuevaDesparasitacion = Deworming(
                        id = 0L,
                        firestoreId = "",
                        petId = 0L,
                        fecha = fecha,
                        producto = producto.ifBlank { null },
                        proximaFecha = proximaFecha.ifBlank { null },
                        notas = notas.ifBlank { null }
                    )
                    viewModel.guardarDesparasitacion(nuevaDesparasitacion, petFirestoreId)
                    onNavigateBack()
                },
                enabled = fecha.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Guardar") }
        }
    }
}