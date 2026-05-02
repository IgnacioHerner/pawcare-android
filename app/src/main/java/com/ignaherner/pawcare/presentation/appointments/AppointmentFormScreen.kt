package com.ignaherner.pawcare.presentation.appointments

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ignaherner.pawcare.domain.model.Appointment
import com.ignaherner.pawcare.utils.fechaHoy
import com.ignaherner.pawcare.utils.toFormattedString
import com.ignaherner.pawcare.ui.theme.PawSpace

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentFormScreen(
    petId: Long,
    appointmentId: Long? = null,
    onNavigateBack: () -> Unit,
    viewModel: AppointmentViewModel = hiltViewModel()
) {
    var fecha by remember { mutableStateOf(fechaHoy()) }
    var motivo by remember { mutableStateOf("") }
    var veterinario by remember { mutableStateOf("") }
    var clinica by remember { mutableStateOf("") }
    var diagnostico by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    val appointmentDetailState by viewModel.appointentDetailState.collectAsStateWithLifecycle()

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    LaunchedEffect(appointmentId) {
        appointmentId?.let { viewModel.loadAppointmentById(it) }
    }

    LaunchedEffect(appointmentDetailState) {
        if (appointmentDetailState is AppointmentDetailState.Success) {
            val appointment = (appointmentDetailState as AppointmentDetailState.Success).appointments
            fecha = appointment.fecha
            motivo = appointment.motivo
            veterinario = appointment.veterinario ?: ""
            clinica = appointment.clinica ?: ""
            diagnostico = appointment.diagnostico ?: ""
            notas = appointment.notas ?: ""
        }
    }

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
                        fecha = localDate.toFormattedString()
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
                title = {
                    Text(if (appointmentId == null) "Nueva visita" else "Editar visita")
                },
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
            OutlinedTextField(
                value = fecha,
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha de la visita") },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Elegir fecha")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }
            )

            OutlinedTextField(
                value = motivo,
                onValueChange = { motivo = it },
                label = { Text("Motivo de la visita") },
                placeholder = { Text("Ej: Control anual, vacunación") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = veterinario,
                onValueChange = { veterinario = it },
                label = { Text("Veterinario (opcional)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = clinica,
                onValueChange = { clinica = it },
                label = { Text("Clínica (opcional)") },
                placeholder = { Text("Ej: Veterinaria San Martín") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = diagnostico,
                onValueChange = { diagnostico = it },
                label = { Text("Diagnóstico (opcional)") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = notas,
                onValueChange = { notas = it },
                label = { Text("Notas (opcional)") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val nuevaVisita = Appointment(
                        id = appointmentId ?: 0L,
                        firestoreId = when (val state = appointmentDetailState) {
                            is AppointmentDetailState.Success -> state.appointments.firestoreId
                            else -> ""
                        },
                        petId = petId,
                        fecha = fecha,
                        motivo = motivo,
                        veterinario = veterinario.ifBlank { null },
                        clinica = clinica.ifBlank { null },
                        diagnostico = diagnostico.ifBlank { null },
                        notas = notas.ifBlank { null }
                    )
                    if (appointmentId == null) {
                        viewModel.insertAppointment(nuevaVisita)
                    } else {
                        viewModel.updateAppointment(nuevaVisita)
                    }
                    onNavigateBack()
                },
                enabled = fecha.isNotBlank() && motivo.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(if (appointmentId == null) "Guardar" else "Actualizar")
            }
        }
    }
}