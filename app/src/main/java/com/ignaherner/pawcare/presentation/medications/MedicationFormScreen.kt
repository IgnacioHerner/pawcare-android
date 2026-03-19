package com.ignaherner.pawcare.presentation.medications

import android.widget.Button
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ignaherner.pawcare.domain.model.Medication
import com.ignaherner.pawcare.domain.model.MedicationStatus
import com.ignaherner.pawcare.presentation.settings.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationFormScreen(
    petId: Long,
    medicationId: Long? = null,
    onNavigateBack: () -> Unit,
    viewModel: MedicationViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    var nombre by remember { mutableStateOf("") }
    var fechaInicio by remember { mutableStateOf("") }
    var duracionDias by remember { mutableStateOf("") }
    var intervaloHoras by remember { mutableStateOf("") }
    var recetadoPor by remember { mutableStateOf("") }
    var dosis by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }
    var statusSeleccionado by remember { mutableStateOf<MedicationStatus>(MedicationStatus.ACTIVO) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val nombreVeterinarioState by settingsViewModel.nombreVeterinario.collectAsStateWithLifecycle()

    LaunchedEffect(nombreVeterinarioState) {
        if(recetadoPor.isNotBlank()) {
            recetadoPor = nombreVeterinarioState
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if(medicationId == null) "Nuevo medicamento" else "Editar medicamento")
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
                value = nombre,
                onValueChange = { nombre = it},
                label = { Text("Nombre")},
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = fechaInicio,
                onValueChange = { fechaInicio = it},
                label = { Text("Fecha de inicio: ")},
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = duracionDias,
                onValueChange = {duracionDias = it},
                label = { Text("Duracion dias: ")},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = recetadoPor,
                onValueChange = { recetadoPor = it},
                label = { Text("Recetado por")},
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = intervaloHoras,
                onValueChange = {intervaloHoras = it},
                label = { Text("Intervalo de horas: ")},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = dosis,
                onValueChange = {dosis = it},
                label = { Text("Cantidad de dosis: ")},
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = notas,
                onValueChange = {notas = it},
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
                    MedicationStatus.entries.forEach { status ->
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
                    val nuevoMedicamento = Medication(
                        id = medicationId ?: 0L,
                        petId = petId,
                        nombre = nombre,
                        fechaInicio = fechaInicio,
                        intervaloHoras = intervaloHoras.toInt(),
                        recetadoPor = recetadoPor.ifBlank { null },
                        duracionDias = duracionDias.toInt(),
                        dosis = dosis,
                        notas = notas.ifBlank { null},
                        status = statusSeleccionado
                    )
                    if (medicationId == null) {
                        viewModel.insertMedication(nuevoMedicamento)
                    }else {
                        viewModel.updateMedication(nuevoMedicamento)
                    }
                    onNavigateBack()
                },
                enabled = nombre.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ){
                Text(if (medicationId == null) "Guardar" else "Actualizar")
            }
        }
    }
}