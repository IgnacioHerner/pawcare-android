package com.ignaherner.mispatitas.presentation.condition

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ignaherner.mispatitas.domain.model.CondicionComun
import com.ignaherner.mispatitas.utils.toFormattedString
import com.ignaherner.mispatitas.domain.model.Condition
import com.ignaherner.mispatitas.domain.model.ConditionEstado
import com.ignaherner.mispatitas.domain.model.Severidad
import com.ignaherner.mispatitas.presentation.components.PawCareIcon
import com.ignaherner.mispatitas.presentation.components.PawIconSize
import com.ignaherner.mispatitas.presentation.vet.VetProfileViewModel
import com.ignaherner.mispatitas.presentation.vet.VetState
import com.ignaherner.mispatitas.ui.theme.Danger
import com.ignaherner.mispatitas.ui.theme.DangerSoft
import com.ignaherner.mispatitas.ui.theme.PawRadio
import com.ignaherner.mispatitas.ui.theme.PawSpace
import com.ignaherner.mispatitas.ui.theme.Success
import com.ignaherner.mispatitas.ui.theme.SuccessSoft
import com.ignaherner.mispatitas.ui.theme.Warn
import com.ignaherner.mispatitas.ui.theme.WarnSoft
import com.ignaherner.mispatitas.utils.fechaHoy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConditionFormScreen(
    onNavigateBack: () -> Unit,
    onSave: (Condition) -> Unit,
    conditionId: Long? = null,
    isVetMode: Boolean = false,
    conditionViewModel: ConditionViewModel? = null,
    vetProfileViewModel: VetProfileViewModel? = null
) {
    var nombre by remember { mutableStateOf("") }
    var fechaDiagnostico by remember { mutableStateOf(fechaHoy()) }
    var severidad by remember { mutableStateOf(Severidad.LEVE) }
    var estado by remember { mutableStateOf(ConditionEstado.ACTIVA) }
    var veterinario by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }
    var firestoreId by remember { mutableStateOf("") }
    var condicionDropdownExpanded by remember { mutableStateOf(false) }
    var condicionSeleccionada by remember { mutableStateOf<CondicionComun?>(null) }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    // Cargar condición existente (solo dueño editando)
    val conditionDetailState = conditionViewModel?.conditionDetailState?.collectAsStateWithLifecycle()

    LaunchedEffect(conditionId) {
        conditionId?.let { conditionViewModel?.loadConditionById(it) }
    }

    LaunchedEffect(conditionDetailState?.value) {
        if (conditionDetailState?.value is ConditionDetailState.Success) {
            val condition = (conditionDetailState.value as ConditionDetailState.Success).condition
            nombre = condition.nombre
            fechaDiagnostico = condition.fechaDiagnostico
            severidad = condition.severidad
            estado = condition.estado
            veterinario = condition.veterinario ?: ""
            notas = condition.notas ?: ""
            firestoreId = condition.firestoreId
            condicionSeleccionada = CondicionComun.entries.find {
                it.displayName == condition.nombre
            } ?: CondicionComun.OTRA
        }
    }

    // Auto-fill veterinario (solo vet)
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

    val titleText = when {
        isVetMode -> "Nueva condición"
        conditionId == null -> "Nueva condición"
        else -> "Editar condición"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titleText) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        PawCareIcon(
                            icon = Icons.Outlined.ArrowBack,
                            contentDescription = "Volver",
                            size = PawIconSize.medium
                        )
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
            // Condición predefinida
            ExposedDropdownMenuBox(
                expanded = condicionDropdownExpanded,
                onExpandedChange = { condicionDropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = condicionSeleccionada?.displayName ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Condición") },
                    placeholder = { Text("Seleccioná una condición") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = condicionDropdownExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = condicionDropdownExpanded,
                    onDismissRequest = { condicionDropdownExpanded = false }
                ) {
                    CondicionComun.entries.forEach { condicion ->
                        DropdownMenuItem(
                            text = { Text(condicion.displayName) },
                            onClick = {
                                condicionSeleccionada = condicion
                                if (condicion == CondicionComun.OTRA) {
                                    nombre = ""
                                } else {
                                    nombre = condicion.displayName
                                    severidad = condicion.severidadDefault
                                }
                                condicionDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Nombre manual — solo si eligió "Otra"
            if (condicionSeleccionada == CondicionComun.OTRA) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre de la condición") },
                    placeholder = { Text("Ej: Luxación de rótula") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Fecha de diagnóstico
            OutlinedTextField(
                value = fechaDiagnostico,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                label = { Text("Fecha de diagnóstico") },
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
                    Severidad.entries.forEach { sev ->
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
                    ConditionEstado.entries.forEach { est ->
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
                    val condition = Condition(
                        id = conditionId ?: 0L,
                        firestoreId = firestoreId,
                        petId = 0L,
                        nombre = nombre.trim(),
                        fechaDiagnostico = fechaDiagnostico,
                        severidad = severidad,
                        estado = estado,
                        veterinario = veterinario.trim().ifBlank { null },
                        notas = notas.trim().ifBlank { null }
                    )
                    onSave(condition)
                    onNavigateBack()
                },
                enabled = nombre.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(PawRadio.md)
            ) {
                Text(
                    text = if (conditionId == null) "Guardar" else "Actualizar",
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}
