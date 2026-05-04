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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
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
    vetProfileViewModel: VetProfileViewModel = hiltViewModel()  // ← cambiar
) {
    var searchId by remember { mutableStateOf("") }
    val searchState by viewModel.searchState.collectAsStateWithLifecycle()
    val vetState by vetProfileViewModel.vetState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        vetProfileViewModel.loadVeterinario()
    }

    LaunchedEffect(searchState) {
        if (searchState is VetSearchState.Success){
            val pet = (searchState as VetSearchState.Success).pet
            onNavigateToPetDetail(pet.firestoreId)
            viewModel.resetSearch()
        }
    }

    val nombreVet = when (val state = vetState) {  // ← cambiar
        is VetState.Success -> state.vet.nombre
        else -> "Veterinario"
    }

    // Estado sin formato — solo letras y números
    var searchRaw by remember { mutableStateOf("") }

    // Lo que se muestra con el guion
    val searchDisplay = when {
        searchRaw.length <= 3 -> searchRaw
        else -> "${searchRaw.take(3)}-${searchRaw.drop(3).take(4)}"
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
                value = searchRaw,
                onValueChange = { input ->
                    val limpio = input
                        .filter { it.isLetterOrDigit() }
                        .uppercase()
                        .take(7) // máximo 3 letras + 4 números
                    searchRaw = limpio
                },
                label = { Text("Código de la mascota") },
                placeholder = { Text("Ej: MIL-4829") },
                visualTransformation = VisualTransformation { text ->
                    val original = text.text
                    val formatted = when {
                        original.length <= 3 -> original
                        else -> "${original.take(3)}-${original.drop(3)}"
                    }
                    TransformedText(
                        AnnotatedString(formatted),
                        object : OffsetMapping {
                            override fun originalToTransformed(offset: Int): Int {
                                return if (offset <= 3) offset else offset + 1
                            }

                            override fun transformedToOriginal(offset: Int): Int {
                                return if (offset <= 3) offset else offset - 1
                            }
                        }
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (searchRaw.length < 3)
                        KeyboardType.Text
                    else
                        KeyboardType.Number,
                    capitalization = if (searchRaw.length < 3)
                        KeyboardCapitalization.Characters
                    else
                        KeyboardCapitalization.None,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (searchRaw.length == 7) {
                            viewModel.buscarMascota("${searchRaw.take(3)}-${searchRaw.drop(3)}")
                        }
                    }
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Boton buscar
            Button(
                onClick = {
                    val codigoFormateado = "${searchRaw.take(3)}-${searchRaw.drop(3)}"
                    viewModel.buscarMascota(codigoFormateado)
                },
                enabled = searchRaw.length == 7 && searchState !is VetSearchState.Loading,
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