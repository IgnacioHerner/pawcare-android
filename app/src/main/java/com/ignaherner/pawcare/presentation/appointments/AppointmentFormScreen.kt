package com.ignaherner.pawcare.presentation.appointments

import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ignaherner.pawcare.domain.model.Appointment
import com.ignaherner.pawcare.domain.model.AppointmentStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentFormScreen(
    petId: Long,
    appointmentId: Long? = null,
    onNavigateBack: () -> Unit,
    viewModel: AppointmentViewModel = hiltViewModel()
) {
    // Estado local del formulario
    var fecha by remember { mutableStateOf("") }
    var veterinario by remember { mutableStateOf("") }
    var motivo by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }
    var statusSeleccionado by remember { mutableStateOf<AppointmentStatus>(AppointmentStatus.PENDIENTE) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if(appointmentId == null) "Nuevo turno" else "Editar turno")
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
            OutlinedTextField(
                value = fecha,
                onValueChange = { fecha = it},
                label = { Text("Fecha (dd//mm/yyyy)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = veterinario,
                onValueChange = {veterinario = it},
                label = { Text("Veterinario")},
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = motivo,
                onValueChange = { motivo = it},
                label = { Text("Motivo")},
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = notas,
                onValueChange = { notas = it},
                label = { Text("Notas")},
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = dropdownExpanded,
                onExpandedChange = { dropdownExpanded = it}
            ) {
                OutlinedTextField(
                    value = statusSeleccionado.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Estado")},
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = {dropdownExpanded = false}
                ) {
                    AppointmentStatus.entries.forEach { status ->
                        DropdownMenuItem(
                            text = { Text(status.displayName)},
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
                    val nuevoTurno = Appointment(
                        id = appointmentId ?: 0L,
                        petId = petId,
                        fecha = fecha,
                        veterinario = veterinario.ifBlank { null },
                        motivo = motivo.ifBlank { null },
                        notas = notas.ifBlank { null },
                        status = statusSeleccionado
                    )
                    if (appointmentId == null) {
                        viewModel.insertAppointment(nuevoTurno)
                    } else {
                        viewModel.updateAppointment(nuevoTurno)
                    }
                    onNavigateBack()
                },
                enabled = fecha.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if(appointmentId == null) "Guardar" else "Actualizar")
            }
        }
    }
}