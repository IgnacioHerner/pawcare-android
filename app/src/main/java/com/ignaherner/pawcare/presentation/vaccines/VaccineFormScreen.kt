package com.ignaherner.pawcare.presentation.vaccines

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
import com.ignaherner.pawcare.domain.model.Vaccine
import com.ignaherner.pawcare.domain.model.VaccineStatus
import com.ignaherner.pawcare.domain.model.displayName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaccineFormScreen(
    petId: Long,
    vaccineId: Long? = null,
    onNavigateBack: () -> Unit,
    viewModel: VaccineViewModel = hiltViewModel()
){
    // Estado local del formulario
    var nombre by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var proximaDosis by remember { mutableStateOf("") }
    var veterinario by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }
    var statusSeleccionado by remember { mutableStateOf<VaccineStatus>(VaccineStatus.Pendiente) }
    var dropdownExpanded by remember { mutableStateOf(false) }

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
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it},
                label = { Text("Nombre de la vacuna")},
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = fecha,
                onValueChange = { fecha = it},
                label = { Text("Fecha (dd/mm/yyyy)")},
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = proximaDosis,
                onValueChange = { proximaDosis = it},
                label = { Text("Proxima dosis (dd/mm/yyyy)")},
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = veterinario,
                onValueChange = { veterinario = it},
                label = { Text("Veterinario")},
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = notas,
                onValueChange = { notas = it},
                label = { Text("Notas")},
                modifier = Modifier.fillMaxWidth()
            )

            // Dropdown status
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
                        proximaDosis = proximaDosis.ifBlank { null },
                        veterinario = veterinario.ifBlank { null },
                        notas = notas.ifBlank { null },
                        status = statusSeleccionado
                    )
                    if (vaccineId == null){
                        viewModel.insertVaccine(nuevaVacuna)
                    } else {
                        viewModel.updateVaccine(nuevaVacuna)
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