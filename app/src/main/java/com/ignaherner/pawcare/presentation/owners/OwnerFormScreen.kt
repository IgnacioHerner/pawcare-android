package com.ignaherner.pawcare.presentation.owners

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ignaherner.pawcare.domain.model.Owner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerFormScreen(
    ownerId: Long?,
    onNavigateBack: () -> Unit,
    viewModel: OwnerViewModel = hiltViewModel()
){
    // Estado local del formulario
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }

    LaunchedEffect(ownerId) {
        ownerId?.let { viewModel.loadOwner() }
    }

    // cuando ownerState cambia, actualizamos los campos
    val ownerState by viewModel.ownerState.collectAsStateWithLifecycle()

    // Pre-llenar campos al editar
    LaunchedEffect(ownerState) {
        if(ownerState is OwnerState.Success) {
            val owner = (ownerState as OwnerState.Success).owner
            nombre = owner.nombre
            apellido = owner.apellido
            telefono = owner.telefono
            email = owner.email ?: ""
            ciudad = owner.ciudad
            telefono = owner.telefono ?: ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (ownerId == null) "Nuevo usuario" else "Editar usuario")
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
                label = { Text("Nombre")},
                modifier = Modifier.fillMaxWidth()
            )

           // Apellido
            OutlinedTextField(
                value = apellido,
                onValueChange = { apellido = it},
                label = { Text("Apellido")},
                modifier = Modifier.fillMaxWidth()
            )
            // Telefono
            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it},
                label = { Text("Telefono")},
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone
                )
            )
            // Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it},
                label = { Text("Email")},
                modifier = Modifier.fillMaxWidth(),
            )
            // Ciudad
            OutlinedTextField(
                value = ciudad,
                onValueChange = { ciudad = it},
                label = { Text("Ciudad")},
                modifier = Modifier.fillMaxWidth(),
            )
            // Direccion
            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it},
                label = { Text("Direccion")},
                modifier = Modifier.fillMaxWidth(),
            )

            // Boton guardar
            Button(
                onClick = {
                    val nuevoOwner = Owner(
                        id = ownerId ?: 0L,
                        nombre = nombre,
                        apellido = apellido,
                        telefono = telefono,
                        email = email.ifBlank { null },
                        ciudad = ciudad,
                        direccion = direccion.ifBlank { null }
                    )
                    if (ownerId == null) {
                        viewModel.insertOwner(nuevoOwner)
                    } else {
                        viewModel.updateOwner(nuevoOwner)
                    }
                    onNavigateBack()
                },
                enabled = nombre.isNotBlank() && apellido.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (ownerId == null ) "Guardar" else "Actualizar")
            }
        }
    }
}