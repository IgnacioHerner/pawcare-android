package com.ignaherner.pawcare.presentation.deworming

import android.widget.DatePicker
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ignaherner.pawcare.domain.model.Deworming
import com.ignaherner.pawcare.domain.model.toFormattedString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DewormingFormScreen(
    petId: Long,
    petName: String,
    dewormingId: Long? = null,
    onNavigateBack: () -> Unit,
    viewModel: DewormingViewModel
) {
    var fecha by remember { mutableStateOf("") }
    var producto by remember { mutableStateOf("") }
    var proximaFecha by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }

    val dewormingDetailState by viewModel.dewormingDetailState.collectAsStateWithLifecycle()
    var showDatePicker by remember { mutableStateOf(false) }
    var showProximaFechaPicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    LaunchedEffect(dewormingId) {
        dewormingId?.let { viewModel.loadDewormingById(it) }
    }

    LaunchedEffect(dewormingDetailState) {
        if (dewormingDetailState is DewormingDetailState.Success) {
            val deworming = (dewormingDetailState as DewormingDetailState.Success).deworming
            fecha = deworming.fecha
            producto = deworming.producto ?: ""
            proximaFecha = deworming.proximaFecha ?: ""
            notas = deworming.notas ?: ""
        }
    }

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
                ) { Text("Aceptar") }
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (dewormingId == null) "Nueva desparasitacion" else "Editar desparasitacion")
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
            // Fecha desparasitacion
            OutlinedTextField(
                value = fecha,
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha de desparasitacion")},
                trailingIcon = {
                    IconButton(onClick = {showDatePicker = true}) {
                        Icon(Icons.Default.DateRange, contentDescription = "Elegir fecha")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true}
            )

            OutlinedTextField(
                value = producto,
                onValueChange = { producto = it},
                label = { Text("Producto")},
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = proximaFecha,
                onValueChange = {},
                readOnly = true,
                label = { Text("Próxima fecha (opcional)") },
                trailingIcon = {
                    IconButton(onClick = { showProximaFechaPicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Elegir fecha")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showProximaFechaPicker = true }
            )

            OutlinedTextField(
                value = notas,
                onValueChange = { notas = it},
                label = { Text("Notas")},
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val nuevaDesparasitacion = Deworming(
                        id = dewormingId ?: 0L,
                        firestoreId = when (val state = dewormingDetailState) {
                            is DewormingDetailState.Success -> state.deworming.firestoreId
                            else -> ""
                        },
                        petId = petId,
                        fecha = fecha,
                        producto = producto.ifBlank { null },
                        proximaFecha = proximaFecha.ifBlank { null },
                        notas = notas.ifBlank { null }
                    )
                    if (dewormingId == null) {
                        viewModel.insertDeworming(nuevaDesparasitacion)
                    } else {
                        viewModel.updateDeworming(nuevaDesparasitacion)
                    }
                    onNavigateBack()
                },
                enabled = fecha.isNotBlank()
            ) {
                Text(if (dewormingId == null) "Guardar" else "Actualizar")
            }
        }
    }
}