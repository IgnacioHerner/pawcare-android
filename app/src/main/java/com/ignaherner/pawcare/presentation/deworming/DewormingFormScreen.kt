package com.ignaherner.pawcare.presentation.deworming

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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ignaherner.pawcare.domain.model.Deworming
import com.ignaherner.pawcare.domain.model.DewormingTipo
import com.ignaherner.pawcare.domain.model.FrecuenciaDeworming
import com.ignaherner.pawcare.presentation.components.PawCareIcon
import com.ignaherner.pawcare.presentation.components.PawIconSize
import com.ignaherner.pawcare.presentation.vet.VetProfileViewModel
import com.ignaherner.pawcare.presentation.vet.VetState
import com.ignaherner.pawcare.ui.theme.PawRadio
import com.ignaherner.pawcare.ui.theme.PawSpace
import com.ignaherner.pawcare.utils.calcularProximaDosisDeworming
import com.ignaherner.pawcare.utils.fechaHoy
import com.ignaherner.pawcare.utils.toFormattedString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DewormingFormScreen(
    onNavigateBack: () -> Unit,
    onSave: (Deworming) -> Unit,
    dewormingId: Long? = null,
    isVetMode: Boolean = false,
    dewormingViewModel: DewormingViewModel? = null,
    vetProfileViewModel: VetProfileViewModel? = null
) {
    var producto by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf(DewormingTipo.INTERNA) }
    var fechaAplicacion by remember { mutableStateOf(fechaHoy()) }
    var frecuencia by remember { mutableStateOf(FrecuenciaDeworming.TRIMESTRAL) }
    var veterinario by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }
    var firestoreId by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    val dewormingDetailState = dewormingViewModel?.dewormingDetailState?.collectAsStateWithLifecycle()


    LaunchedEffect(dewormingId) {
        dewormingId?.let { dewormingViewModel?.loadDewormingById(it) }
    }

    LaunchedEffect(dewormingDetailState?.value) {
        if (dewormingDetailState?.value is DewormingDetailState.Success) {
            val deworming = (dewormingDetailState.value as DewormingDetailState.Success).deworming
            producto = deworming.producto
            tipo = deworming.tipo
            fechaAplicacion = deworming.fechaAplicacion
            frecuencia = deworming.frecuencia
            veterinario = deworming.veterinario ?: ""
            notas = deworming.notas ?: ""
        }
    }

    val vetState = vetProfileViewModel?.vetState?.collectAsStateWithLifecycle()

    LaunchedEffect(vetState?.value) {
        if (vetState?.value is VetState.Success && veterinario.isBlank()) {
            val vet = (vetState.value as VetState.Success).vet
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
                            .atZone(java.time.ZoneId.of("UTC"))
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

    val titleText = when{
        isVetMode -> "Nueva desparasitacion"
        dewormingId == null -> "Nueva desparasitacion"
        else -> "Editar desparasitacion"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {Text(titleText)},
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
            // Producto
            OutlinedTextField(
                value = producto,
                onValueChange = { producto = it },
                label = { Text("Producto") },
                placeholder = { Text("Ej: NexGard, Frontline") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

            // Tipo
            Column(verticalArrangement = Arrangement.spacedBy(PawSpace.sm)) {
                Text(text = "Tipo", style = MaterialTheme.typography.titleSmall)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(PawSpace.sm),
                    verticalArrangement = Arrangement.spacedBy(PawSpace.sm)
                ) {
                    DewormingTipo.entries.forEach { t ->
                        FilterChip(
                            selected = tipo == t,
                            onClick = { tipo = t },
                            label = { Text(t.displayName) }
                        )
                    }
                }
            }

            // Fecha de aplicación
            OutlinedTextField(
                value = fechaAplicacion,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                label = { Text("Fecha de aplicacion") },
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

            // Frecuencia
            Column(verticalArrangement = Arrangement.spacedBy(PawSpace.sm)) {
                Text(text = "Frecuencia", style = MaterialTheme.typography.titleSmall)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(PawSpace.sm),
                    verticalArrangement = Arrangement.spacedBy(PawSpace.sm)
                ) {
                    FrecuenciaDeworming.entries.forEach { freq ->
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
                    shape = RoundedCornerShape(PawRadio.sm)
                ) {
                    Row(
                        modifier = Modifier.padding(PawSpace.md),
                        horizontalArrangement = Arrangement.spacedBy(PawSpace.sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PawCareIcon(
                            icon = Icons.Outlined.Schedule,
                            contentDescription = null,
                            size = PawIconSize.medium,
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

            // Veterinario
            OutlinedTextField(
                value = veterinario,
                onValueChange = { veterinario = it },
                label = { Text("Veterinario (opcional)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

            // Notas
            OutlinedTextField(
                value = notas,
                onValueChange = { notas = it },
                label = { Text("Notas (opcional)") },
                minLines = 2,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val proximaDosis = if (frecuencia != FrecuenciaDeworming.UNICA) {
                        calcularProximaDosisDeworming(fechaAplicacion, frecuencia)
                    } else null

                    val deworming = Deworming(
                        id = dewormingId ?: 0L,
                        firestoreId = firestoreId,
                        petId = 0L,
                        producto = producto.trim(),
                        tipo = tipo,
                        fechaAplicacion = fechaAplicacion,
                        frecuencia = frecuencia,
                        proximaDosis = proximaDosis,
                        veterinario = veterinario.trim().ifBlank { null },
                        notas = notas.trim().ifBlank { null }
                    )
                    onSave(deworming)
                    onNavigateBack()
                },
                enabled = producto.isNotBlank() && fechaAplicacion.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(PawRadio.md)
            ) {
                Text(
                    text = if (dewormingId == null) "Guardar" else "Actualizar",
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}