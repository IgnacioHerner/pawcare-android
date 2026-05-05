package com.ignaherner.pawcare.presentation.vet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ignaherner.pawcare.domain.model.Weight
import com.ignaherner.pawcare.utils.fechaHoy
import com.ignaherner.pawcare.utils.toFormattedString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VetWeightFormScreen(
    petFirestoreId: String,
    onNavigateBack: () -> Unit,
    viewModel: VetViewModel = hiltViewModel(),
    vetProfileViewModel: VetProfileViewModel = hiltViewModel()
) {
    var peso by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf(fechaHoy()) }
    var notas by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )
    var veterinario by remember { mutableStateOf("") }


    val vetState by vetProfileViewModel.vetState.collectAsStateWithLifecycle()

    LaunchedEffect(vetState) {
        if (vetState is VetState.Success && veterinario.isBlank()){
            val vet = (vetState as VetState.Success).vet
            veterinario = "Dr. ${vet.nombre} ${vet.apellido}"
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
                title = { Text("Registrar peso") },
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
                value = peso,
                onValueChange = { peso = it },
                label = { Text("Peso (kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),

                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = fecha,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                label = { Text("Fecha") },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Elegir fecha")
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }
            )

            OutlinedTextField(
                value = notas,
                onValueChange = { notas = it },
                label = { Text("Notas (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Button(
                onClick = {
                    val nuevoWeight = Weight(
                        id = 0L,
                        firestoreId = "",
                        petId = 0L,
                        peso = peso.toDoubleOrNull() ?: 0.0,
                        fecha = fecha,
                        notas = notas.ifBlank { null }
                    )
                    viewModel.guardarPeso(nuevoWeight, petFirestoreId)
                    onNavigateBack()
                },
                enabled = peso.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Guardar") }
        }
    }
}