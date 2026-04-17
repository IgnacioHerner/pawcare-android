package com.ignaherner.pawcare.presentation.vet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ignaherner.pawcare.presentation.owners.OwnerState
import com.ignaherner.pawcare.presentation.owners.OwnerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VetHomeScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToPetDetail: (String) -> Unit,
    viewModel: VetViewModel = hiltViewModel(),
    ownerViewModel: OwnerViewModel = hiltViewModel()
) {
    var searchId by remember { mutableStateOf("") }
    val searchState by viewModel.searchState.collectAsStateWithLifecycle()
    val ownerState by ownerViewModel.ownerState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        ownerViewModel.loadOwner()
    }

    LaunchedEffect(searchState) {
        if (searchState is VetSearchState.Success){
            val pet = (searchState as VetSearchState.Success).pet
            onNavigateToPetDetail(pet.firestoreId)
            viewModel.resetSearch()
        }
    }

    val nombreVet = when (val state = ownerState) {
        is OwnerState.Success -> state.owner.nombre
        else -> "Veterinario"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Configuracion")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Saludo
            Column {
                Text(
                    text = "Hola, Dr. $nombreVet 👋",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Buscá una mascota por ID o QR",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Buscar por ID
            OutlinedTextField(
                value = searchId,
                onValueChange = { searchId = it},
                label = { Text("ID de la mascota")},
                placeholder = { Text("Pega el ID del QR aca")},
                trailingIcon = {
                    if (searchId.isNotBlank()){
                        IconButton(onClick = { searchId = ""}) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if(searchId.isNotBlank()) {
                            viewModel.buscarMascota(searchId.trim())
                        }
                    }
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Boton buscar
            Button(
                onClick = { viewModel.buscarMascota(searchId.trim())},
                enabled = searchId.isNotBlank() && searchState !is VetSearchState.Loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (searchState is VetSearchState.Loading){
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Buscar mascota")
                }
            }


            // Error
            val errorState = searchState as? VetSearchState.Error
            if (errorState != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorState.mensaje,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            HorizontalDivider()

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "🐾",
                    style = MaterialTheme.typography.displaySmall
                )
                Text(
                    text = "Escaneá el QR del collar de la mascota\ny copiá el ID que aparece",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}