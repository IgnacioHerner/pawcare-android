package com.ignaherner.pawcare.presentation.condition

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ignaherner.pawcare.domain.model.fechaHoy
import com.ignaherner.pawcare.domain.model.toFormattedString
import com.ignaherner.pawcare.presentation.components.Condition

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConditionFormScreen(
    petId: Long,
    petName: String,
    conditionId: Long? = null,
    onNavigateBack: () -> Unit,
    viewModel: ConditionViewModel
){
    var nombre by remember { mutableStateOf("") }
    var fechaDiagnostico by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }

    val conditionDetailState by viewModel.conditionDetailState.collectAsStateWithLifecycle()
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    LaunchedEffect(conditionId) {
        conditionId?.let { viewModel.loadConditionById(it) }
    }

    LaunchedEffect(conditionDetailState) {
        if (conditionDetailState is ConditionDetailState.Success) {
            val condition = (conditionDetailState as ConditionDetailState.Success).condition
            nombre = condition.nombre
            fechaDiagnostico = condition.fechaDiagnostico ?: ""
            notas = condition.notas ?: ""
        }
    }

    // Dialog de date
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
                            fechaDiagnostico = localDate.toFormattedString()
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
                    Text(if (conditionId == null) "Nueva condicion" else "Editar condicion")
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
                label = { Text("Nombre de la condicion")},
                modifier = Modifier.fillMaxWidth()
            )

            // Fecha de diagnostico
            OutlinedTextField(
                value = fechaDiagnostico,
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha de diagnóstico (opcional)") },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Elegir fecha")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }
            )

            // Notas
            OutlinedTextField(
                value = notas,
                onValueChange = { notas = it},
                label = { Text("Notas")}
            )

            Button(
                onClick = {
                    val nuevaCondicion = Condition(
                        id = conditionId ?: 0L,
                        petId = petId,
                        nombre = nombre,
                        fechaDiagnostico = fechaDiagnostico,
                        notas = notas
                    )
                    if (conditionId == null) {
                        viewModel.insertCondition(nuevaCondicion)
                    } else {
                        viewModel.updateCondition(nuevaCondicion)
                    }
                    onNavigateBack()
                },
                enabled = nombre.isNotBlank()
            ) {
                Text(if (conditionId == null) "Guardar" else "Actualizar")
            }
        }
    }
}