package com.ignaherner.pawcare.presentation.vet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ignaherner.pawcare.domain.model.Deworming
import com.ignaherner.pawcare.domain.model.DewormingTipo
import com.ignaherner.pawcare.domain.model.FrecuenciaDeworming
import com.ignaherner.pawcare.ui.theme.PawRadii
import com.ignaherner.pawcare.ui.theme.PawSpace
import com.ignaherner.pawcare.utils.calcularProximaDosisDeworming
import com.ignaherner.pawcare.utils.fechaHoy
import com.ignaherner.pawcare.utils.toFormattedString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VetDewormingFormScreen(
    petFirestoreId: String,
    onNavigateBack: () -> Unit,
    viewModel: VetViewModel = hiltViewModel()
) {
    var producto by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf(DewormingTipo.INTERNA) }
    var fechaAplicacion by remember { mutableStateOf(fechaHoy()) }
    var frecuencia by remember { mutableStateOf(FrecuenciaDeworming.TRIMESTRAL) }
    var veterinario by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

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
                        fechaAplicacion = localDate.toFormattedString()
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
                title = { Text("Nueva desparasitación") },
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
            OutlinedTextField(
                value = producto,
                onValueChange = { producto = it },
                label = { Text("Producto") },
                placeholder = { Text("Ej: NexGard, Frontline") },
                modifier = Modifier.fillMaxWidth()
            )

            // Tipo
            Column(verticalArrangement = Arrangement.spacedBy(PawSpace.sm)) {
                Text(text = "Tipo", style = MaterialTheme.typography.titleSmall)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(PawSpace.sm),
                    verticalArrangement = Arrangement.spacedBy(PawSpace.sm)
                ) {
                    DewormingTipo.values().forEach { t ->
                        FilterChip(
                            selected = tipo == t,
                            onClick = { tipo = t },
                            label = { Text(t.displayName) }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = fechaAplicacion,
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha de aplicación") },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Fecha")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }
            )

            // Frecuencia
            Column(verticalArrangement = Arrangement.spacedBy(PawSpace.sm)) {
                Text(text = "Frecuencia", style = MaterialTheme.typography.titleSmall)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(PawSpace.sm),
                    verticalArrangement = Arrangement.spacedBy(PawSpace.sm)
                ) {
                    FrecuenciaDeworming.values().forEach { freq ->
                        FilterChip(
                            selected = frecuencia == freq,
                            onClick = { frecuencia = freq },
                            label = { Text(freq.displayName) }
                        )
                    }
                }
            }

            // Próxima dosis calculada
            if (frecuencia != FrecuenciaDeworming.UNICA && fechaAplicacion.isNotBlank()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(PawRadii.sm)
                ) {
                    Row(
                        modifier = Modifier.padding(PawSpace.md),
                        horizontalArrangement = Arrangement.spacedBy(PawSpace.sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Schedule,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Column {
                            Text(
                                text = "Próxima dosis",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = calcularProximaDosisDeworming(fechaAplicacion, frecuencia),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = veterinario,
                onValueChange = { veterinario = it },
                label = { Text("Veterinario (opcional)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = notas,
                onValueChange = { notas = it },
                label = { Text("Notas (opcional)") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val proximaDosis = if (frecuencia != FrecuenciaDeworming.UNICA) {
                        calcularProximaDosisDeworming(fechaAplicacion, frecuencia)
                    } else null

                    val nuevaDesparasitacion = Deworming(
                        id = 0L,
                        firestoreId = "",
                        petId = 0L,
                        producto = producto,
                        tipo = tipo,
                        fechaAplicacion = fechaAplicacion,
                        frecuencia = frecuencia,
                        proximaDosis = proximaDosis,
                        veterinario = veterinario.ifBlank { null },
                        notas = notas.ifBlank { null }
                    )
                    viewModel.guardarDesparasitacion(nuevaDesparasitacion, petFirestoreId)
                    onNavigateBack()
                },
                enabled = producto.isNotBlank() && fechaAplicacion.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) { Text("Guardar") }
        }
    }
}