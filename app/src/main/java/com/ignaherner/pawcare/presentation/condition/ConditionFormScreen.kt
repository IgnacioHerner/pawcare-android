package com.ignaherner.pawcare.presentation.condition

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.ignaherner.pawcare.utils.toFormattedString
import com.ignaherner.pawcare.domain.model.Condition
import com.ignaherner.pawcare.domain.model.ConditionEstado
import com.ignaherner.pawcare.domain.model.Severidad
import com.ignaherner.pawcare.ui.theme.Danger
import com.ignaherner.pawcare.ui.theme.DangerSoft
import com.ignaherner.pawcare.ui.theme.PawSpace
import com.ignaherner.pawcare.ui.theme.Success
import com.ignaherner.pawcare.ui.theme.SuccessSoft
import com.ignaherner.pawcare.ui.theme.Warn
import com.ignaherner.pawcare.ui.theme.WarnSoft
import com.ignaherner.pawcare.utils.fechaHoy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConditionFormScreen(
    petId: Long,
    petName: String,
    conditionId: Long? = null,
    onNavigateBack: () -> Unit,
    viewModel: ConditionViewModel
) {
    var nombre by remember { mutableStateOf("") }
    var fechaDiagnostico by remember { mutableStateOf(fechaHoy()) }
    var severidad by remember { mutableStateOf(Severidad.LEVE) }
    var estado by remember { mutableStateOf(ConditionEstado.ACTIVA) }
    var veterinario by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    val conditionDetailState by viewModel.conditionDetailState.collectAsStateWithLifecycle()

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
            fechaDiagnostico = condition.fechaDiagnostico
            severidad = condition.severidad
            estado = condition.estado
            veterinario = condition.veterinario ?: ""
            notas = condition.notas ?: ""
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
                        fechaDiagnostico = localDate.toFormattedString()
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
                title = {
                    Text(if (conditionId == null) "Nueva condición" else "Editar condición")
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(PawSpace.lg)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(PawSpace.lg)
        ) {
            // Nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre de la condición") },
                placeholder = { Text("Ej: Displasia de cadera") },
                modifier = Modifier.fillMaxWidth()
            )

            // Fecha de diagnóstico
            OutlinedTextField(
                value = fechaDiagnostico,
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha de diagnóstico") },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Elegir fecha")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }
            )

            // Severidad
            Column(verticalArrangement = Arrangement.spacedBy(PawSpace.sm)) {
                Text(
                    text = "Severidad",
                    style = MaterialTheme.typography.titleSmall
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(PawSpace.sm),
                    verticalArrangement = Arrangement.spacedBy(PawSpace.sm)
                ) {
                    Severidad.values().forEach { sev ->
                        FilterChip(
                            selected = severidad == sev,
                            onClick = { severidad = sev },
                            label = { Text(sev.displayName) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = when (sev) {
                                    Severidad.LEVE -> SuccessSoft
                                    Severidad.MODERADA -> WarnSoft
                                    Severidad.GRAVE -> DangerSoft
                                },
                                selectedLabelColor = when (sev) {
                                    Severidad.LEVE -> Success
                                    Severidad.MODERADA -> Warn
                                    Severidad.GRAVE -> Danger
                                }
                            )
                        )
                    }
                }
            }

            // Estado
            Column(verticalArrangement = Arrangement.spacedBy(PawSpace.sm)) {
                Text(
                    text = "Estado",
                    style = MaterialTheme.typography.titleSmall
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(PawSpace.sm),
                    verticalArrangement = Arrangement.spacedBy(PawSpace.sm)
                ) {
                    ConditionEstado.values().forEach { est ->
                        FilterChip(
                            selected = estado == est,
                            onClick = { estado = est },
                            label = { Text(est.displayName) }
                        )
                    }
                }
            }

            // Veterinario
            OutlinedTextField(
                value = veterinario,
                onValueChange = { veterinario = it },
                label = { Text("Veterinario (opcional)") },
                modifier = Modifier.fillMaxWidth()
            )

            // Notas
            OutlinedTextField(
                value = notas,
                onValueChange = { notas = it },
                label = { Text("Notas (opcional)") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val nuevaCondicion = Condition(
                        id = conditionId ?: 0L,
                        firestoreId = when (val state = conditionDetailState) {
                            is ConditionDetailState.Success -> state.condition.firestoreId
                            else -> ""
                        },
                        petId = petId,
                        nombre = nombre,
                        fechaDiagnostico = fechaDiagnostico,
                        severidad = severidad,
                        estado = estado,
                        veterinario = veterinario.ifBlank { null },
                        notas = notas.ifBlank { null }
                    )
                    if (conditionId == null) {
                        viewModel.insertCondition(nuevaCondicion)
                    } else {
                        viewModel.updateCondition(nuevaCondicion)
                    }
                    onNavigateBack()
                },
                enabled = nombre.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(if (conditionId == null) "Guardar" else "Actualizar")
            }
        }
    }
}