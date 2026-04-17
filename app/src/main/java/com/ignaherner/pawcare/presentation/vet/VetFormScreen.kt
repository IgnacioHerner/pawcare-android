package com.ignaherner.pawcare.presentation.vet

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ignaherner.pawcare.domain.model.Veterinario
import com.ignaherner.pawcare.presentation.pets.copyImageToInternalStorage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VetFormScreen(
    onNavigateBack: () -> Unit,
    viewModel: VetProfileViewModel = hiltViewModel()
){

    // Estado local del formulario
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var matricula by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccionVeterinaria by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("") }
    var especialidad by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadVeterinario()
    }

    val vetState by viewModel.vetState.collectAsStateWithLifecycle()

    LaunchedEffect(vetState) {
        if(vetState is VetState.Success){
            val vet = (vetState as VetState.Success).vet
            nombre = vet.nombre
            apellido = vet.apellido
            matricula = vet.matricula
            telefono = vet.telefono
            direccionVeterinaria = vet.direccionVet ?: ""
            ciudad = vet.ciudadVet ?: ""
            especialidad = vet.especialidad ?: ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (vetState is VetState.Success) "Editar perfil" else "Nuevo perfil")
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
                value = apellido,
                onValueChange = { apellido = it},
                label = { Text("Apellido")},
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = matricula,
                onValueChange = { matricula = it},
                label = { Text("Matricula")},
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it},
                label = { Text("Telefono")},
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone
                )
            )

            OutlinedTextField(
                value = direccionVeterinaria,
                onValueChange = { direccionVeterinaria = it},
                label = { Text("Direccion de el consultorio")},
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = ciudad,
                onValueChange = { ciudad = it},
                label = { Text("Ciudad")},
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = especialidad,
                onValueChange = { especialidad = it},
                label = { Text("Especialidad")},
                modifier = Modifier.fillMaxWidth(),
            )

            Button(
                onClick = {
                    val nuevoVet = Veterinario(
                        nombre = nombre,
                        apellido = apellido,
                        matricula = matricula,
                        telefono = telefono,
                        direccionVet = direccionVeterinaria.ifBlank { null },
                        ciudadVet = ciudad.ifBlank { null },
                        especialidad = especialidad.ifBlank { null }
                    )
                    viewModel.guardarVeterinario(nuevoVet)
                    onNavigateBack()
                },
                enabled = nombre.isNotBlank() &&
                        apellido.isNotBlank() &&
                        matricula.isNotBlank() &&
                        telefono.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (vetState is VetState.Success) "Actualizar" else "Guardar")
            }
        }
    }
}