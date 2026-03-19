package com.ignaherner.pawcare.presentation.weight

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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ignaherner.pawcare.domain.model.Weight
import com.ignaherner.pawcare.domain.model.fechaHoy
import com.ignaherner.pawcare.domain.model.toFormattedString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightFormScreen(
    petId: Long,
    weightId: Long? = null,
    onNavigateBack: () -> Unit,
    viewModel: WeightViewModel = hiltViewModel()
) {
    var peso by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf(fechaHoy()) }
    var notas by remember { mutableStateOf("") }

    // Estado para controlar si el dialog esta abierto
    var showDatePicker by remember { mutableStateOf(false) }

    // DatePickerState
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    // Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false},
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val localDate = java.time.Instant
                                .ofEpochMilli(millis)
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate()
                            fecha = localDate.toFormattedString()
                        }
                        showDatePicker = false
                    }
                ) { Text("Aceptar")}
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false}) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if(weightId == null) "Nuevo peso" else "Editar peso")
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
                value = peso,
                onValueChange = { peso = it},
                label = { Text("Peso")},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = fecha,
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha del peso")},
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true}) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Elegir fecha"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable{ showDatePicker = true}
            )

            OutlinedTextField(
                value = notas,
                onValueChange = {notas = it},
                label = { Text("Notas")},
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val nuevoPeso = Weight(
                        id = weightId ?: 0L,
                        petId = petId,
                        peso = peso.toDoubleOrNull() ?: 0.0,
                        fecha = fecha,
                        notas = notas.ifBlank { null }
                    )
                    if (weightId == null) {
                        viewModel.insertWeight(nuevoPeso)
                    }else {
                        viewModel.updateWeight(nuevoPeso)
                    }
                    onNavigateBack()
                },
                enabled = peso.isNotBlank() && fecha.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (weightId == null) "Guardar" else "Actualizar")
            }
        }
    }
}