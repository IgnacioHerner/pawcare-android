package com.ignaherner.pawcare.presentation.pets

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ignaherner.pawcare.domain.model.Especie
import com.ignaherner.pawcare.domain.model.Pet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetFormScreen(
    petId: Long?,
    onNavigateBack: () -> Unit,
    viewModel: PetViewModel = hiltViewModel()
) {

    // Estado local del formulario
    var nombre by remember { mutableStateOf("") }
    var especieSeleccionada by remember { mutableStateOf(Especie.PERRO) }
    var peso by remember { mutableStateOf("") }
    var dropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (petId == null) "Nueva Mascota" else "Editar Mascota")
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

            // Campo nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it},
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxSize()
            )

            ExposedDropdownMenuBox(
                expanded = dropdownExpanded,
                onExpandedChange = { dropdownExpanded = it}
            ) {
                OutlinedTextField(
                    value = especieSeleccionada.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Especie")},
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false}
                ) {
                    Especie.entries.forEach { especie ->
                        DropdownMenuItem(
                            text = { Text(especie.displayName)},
                            onClick = {
                                especieSeleccionada = especie
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Campo peso
            OutlinedTextField(
                value = peso,
                onValueChange = { peso = it},
                label = { Text("Peso (kg)")},
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // Botón guardar
            Button(
                onClick = {
                    val nuevaMascota = Pet(
                        id = petId ?: 0L,
                        nombre = nombre,
                        especie = especieSeleccionada,
                        fechaNacimiento = null,
                        peso = peso.toDoubleOrNull(),
                        fotoUri = null
                    )
                    if (petId == null) {
                        viewModel.insertPet(nuevaMascota)
                    } else {
                        viewModel.updatePet(nuevaMascota)
                    }
                    onNavigateBack()
                },
                enabled = nombre.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (petId == null) "Guardar" else "Actualizar")
            }
        }
    }
}