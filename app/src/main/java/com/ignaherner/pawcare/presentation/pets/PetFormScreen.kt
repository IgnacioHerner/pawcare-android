package com.ignaherner.pawcare.presentation.pets

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ignaherner.pawcare.domain.model.Especie
import com.ignaherner.pawcare.domain.model.FechaNacimientoTipo
import com.ignaherner.pawcare.domain.model.Pet
import com.ignaherner.pawcare.domain.model.Sex
import com.ignaherner.pawcare.domain.model.toFormattedString
import java.io.File
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetFormScreen(
    petId: Long?,
    onNavigateBack: () -> Unit,
    viewModel: PetViewModel = hiltViewModel()
) {
    // Estado local del formulario
    var nombre by remember { mutableStateOf("") }
    var especieSeleccionada by remember { mutableStateOf(Especie.PERRO) }
    var raza by remember { mutableStateOf("") }
    var sexoSeleccionado by remember { mutableStateOf(Sex.MACHO) }
    var fechaNacimiento by remember { mutableStateOf("") }
    var fechaNacimientoTipo by remember {
        mutableStateOf(FechaNacimientoTipo.DESCONOCIDA)
    }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var sexoDropdownExpanded by remember { mutableStateOf(false) }
    var fotoUri by remember { mutableStateOf("") }
    var castrado by remember { mutableStateOf(false) }
    var fechaCastracion by remember { mutableStateOf("") }


    var showFechaCastracionPicker by remember { mutableStateOf(false) }
    val fechaCastracionPickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )
    if (showFechaCastracionPicker) {
        DatePickerDialog(
            onDismissRequest = { showFechaCastracionPicker = false},
            confirmButton = {
                TextButton(
                    onClick = {
                        fechaCastracionPickerState.selectedDateMillis?.let { millis ->
                            val localDate = java.time.Instant
                                .ofEpochMilli(millis)
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate()
                            fechaCastracion = localDate.toFormattedString()
                        }
                        showFechaCastracionPicker = false
                    }
                ) { Text("Aceptar")}
            },
            dismissButton = {
                TextButton(onClick = { showFechaCastracionPicker = false}) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = fechaCastracionPickerState)
        }
    }

    var showFechaNacimientoPicker by remember { mutableStateOf(false) }
    val fechaNacimientoPickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )
    if (showFechaNacimientoPicker) {
        DatePickerDialog(
            onDismissRequest = { showFechaNacimientoPicker = false},
            confirmButton = {
                TextButton(
                    onClick = {
                        fechaNacimientoPickerState.selectedDateMillis?.let { millis ->
                            val localDate = java.time.Instant
                                .ofEpochMilli(millis)
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate()
                            fechaNacimiento = localDate.toFormattedString()
                        }
                        showFechaNacimientoPicker = false
                    }
                ) { Text("Aceptar")}
            },
            dismissButton = {
                TextButton(onClick = {showFechaNacimientoPicker = false}) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = fechaNacimientoPickerState)
        }
    }

    // Cargar las mascotas si estamos editando
    LaunchedEffect(petId) {
        petId?.let { viewModel.loadPetById(it) }
    }

    // Pre-llenar campos cuando los datos cargan
    val detailState by viewModel.detailState.collectAsStateWithLifecycle()

    // Cuando detailState cambia, actualizá los campos
    LaunchedEffect(detailState) {
        if(detailState is PetDetailState.Success){
            val pet = (detailState as PetDetailState.Success).pet
            nombre = pet.nombre
            especieSeleccionada = pet.especie
            castrado = pet.castrado
            fechaCastracion = pet.fechaCastracion ?: ""
            fechaNacimientoTipo = pet.fechaNacimientoTipo
        }
    }

    val context = LocalContext.current

    // Launcher para galeria
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {fotoUri = it.toString()}
    }

    // Uri temporal para la camara
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher para camara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if(success){
            tempCameraUri?.let { fotoUri = it.toString() }
        }
    }

    // Launcher para permiso de camara
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = createImageUri(context)
            tempCameraUri = uri
            cameraLauncher.launch(uri)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (petId == null) "Nueva Mascota" else "Editar Mascota")
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Foto de mascota
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .align(Alignment.CenterHorizontally)
                    .clickable{}
            ){
                if (fotoUri.isNotBlank()) {
                    AsyncImage(
                        model = fotoUri,
                        contentDescription = "Foto de ${nombre}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }else {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Agregar foto",
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.Center),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Botones galeria y camara
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ){
                OutlinedButton(
                    onClick = { galleryLauncher.launch("image/*")},
                    modifier = Modifier.weight(1f)
                ) {
                    Text("📷 Galería")
                }
                OutlinedButton(
                    onClick = {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("📸 Cámara")
                }
            }

            // Campo nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )

            // Campo raza
            OutlinedTextField(
                value = raza,
                onValueChange = { raza = it},
                label = { Text("Raza")},
                modifier = Modifier.fillMaxWidth()
            )

            // Dropdown sexo
            ExposedDropdownMenuBox(
                expanded = dropdownExpanded,
                onExpandedChange = { sexoDropdownExpanded = it}
            ) {
                OutlinedTextField(
                    value = sexoSeleccionado.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Sexo")},
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = sexoDropdownExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = sexoDropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false}
                ) {
                    Sex.entries.forEach { sexo ->
                        DropdownMenuItem(
                            text = { Text(sexo.displayName) },
                            onClick = {
                                sexoSeleccionado = sexo
                                sexoDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Tipo de fecha de nacimiento
            Text(
                text = "Fecha de nacimiento",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FechaNacimientoTipo.entries.forEach { tipo ->
                    FilterChip(
                        selected = fechaNacimientoTipo == tipo,
                        onClick = {
                            fechaNacimientoTipo = tipo
                            if (tipo  == FechaNacimientoTipo.DESCONOCIDA) {
                                fechaNacimiento = ""
                            }
                        },
                        label = {
                            Text(
                                text = when(tipo) {
                                    FechaNacimientoTipo.EXACTA -> "Exacta"
                                    FechaNacimientoTipo.APROXIMADA -> "Aproximada"
                                    FechaNacimientoTipo.DESCONOCIDA -> "Desconocida"
                                },
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    )
                }
            }

            // DatePicker solo si no es DESCONOCIDA
            if (fechaNacimientoTipo != FechaNacimientoTipo.DESCONOCIDA) {
                if (fechaNacimientoTipo == FechaNacimientoTipo.APROXIMADA) {
                    // Dos dropdowns - mes y año
                    var mesExpanded by remember { mutableStateOf(false) }
                    var anioExpanded by remember { mutableStateOf(false) }
                    var mesSeleccionado by remember { mutableStateOf("") }
                    var anioSeleccionado by remember { mutableStateOf("") }

                    val meses = listOf(
                        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
                    )

                    val anioActual = LocalDate.now().year
                    val anios = (anioActual downTo anioActual - 30).map { it.toString() }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Dropdown Mes
                        ExposedDropdownMenuBox(
                            expanded = mesExpanded,
                            onExpandedChange = { mesExpanded = it},
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = mesSeleccionado.ifBlank { "Mes" },
                                onValueChange = {},
                                readOnly = true,
                                label = { Text ("Mes aprox.")},
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = mesExpanded)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = mesExpanded,
                                onDismissRequest = { mesExpanded = false}
                            ) {
                                meses.forEachIndexed { index, mes ->
                                    DropdownMenuItem(
                                        text = { Text(mes)},
                                        onClick = {
                                            mesSeleccionado = mes
                                            mesExpanded = false
                                            // Actualizar fechaNacimiento
                                            if(anioSeleccionado.isNotBlank()){
                                                val mesNum = (index + 1).toString().padStart(2,'0')
                                                fechaCastracion = "01/$mesNum/$anioSeleccionado"
                                            }
                                        }
                                    )
                                }
                            }
                        }
                        // Dropdown año
                        ExposedDropdownMenuBox(
                            expanded = anioExpanded,
                            onExpandedChange = { anioExpanded = it},
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = anioSeleccionado.ifBlank { "Año" },
                                onValueChange = {},
                                readOnly = true,
                                label = { Text ("Año aprox.")},
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = mesExpanded)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = anioExpanded,
                                onDismissRequest = { anioExpanded = false}
                            ) {
                                anios.forEach { anio ->
                                    DropdownMenuItem(
                                        text = { Text(anio) },
                                        onClick = {
                                            anioSeleccionado = anio
                                            anioExpanded = false
                                            // Actualizar fechaNacimiento
                                            if (mesSeleccionado.isNotBlank()) {
                                                val mesNum = (meses.indexOf(mesSeleccionado) + 1)
                                                    .toString().padStart(2, '0')
                                                fechaNacimiento = "01/$mesNum/$anio"
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // EXACTA → DatePicker normal
                    OutlinedTextField(
                        value = fechaNacimiento,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Fecha de nacimiento") },
                        trailingIcon = {
                            IconButton(onClick = { showFechaNacimientoPicker = true }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Elegir fecha")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showFechaNacimientoPicker = true }
                    )
                }
            }

            // Dropdown especie
            ExposedDropdownMenuBox(
                expanded = dropdownExpanded,
                onExpandedChange = { dropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = especieSeleccionada.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Especie") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false }
                ) {
                    Especie.entries.forEach { especie ->
                        DropdownMenuItem(
                            text = { Text(especie.displayName) },
                            onClick = {
                                especieSeleccionada = especie
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Castración
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "¿Está castrado/a?",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Recomendamos la castración responsable",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = castrado,
                    onCheckedChange = {
                        castrado = it
                        if (!it) fechaCastracion = "" // limpiá la fecha si desactiva
                    }
                )
            }

            // Fecha castración — solo si está castrado
            if (castrado) {
                OutlinedTextField(
                    value = fechaCastracion,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fecha de castración") },
                    trailingIcon = {
                        IconButton(onClick = { showFechaCastracionPicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Elegir fecha")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showFechaCastracionPicker = true }
                )
            }

            // Botón guardar
            Button(
                onClick = {
                    val nuevaMascota = Pet(
                        id = petId ?: 0L,
                        nombre = nombre,
                        especie = especieSeleccionada,
                        raza = raza.ifBlank { null },
                        sexo = sexoSeleccionado,
                        fechaNacimientoTipo = fechaNacimientoTipo,
                        fechaNacimiento = if (fechaNacimientoTipo == FechaNacimientoTipo.DESCONOCIDA) null else fechaNacimiento.ifBlank { null },
                        fotoUri = null,
                        castrado = castrado,
                        fechaCastracion = if (castrado) fechaCastracion.ifBlank { null } else null,

                    )
                    if (petId == null) {
                        viewModel.insertPet(nuevaMascota)
                    } else {
                        viewModel.updatePet(nuevaMascota)
                    }
                    onNavigateBack()
                },
                enabled = nombre.isNotBlank() && (!castrado || fechaCastracion.isNotBlank()),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (petId == null) "Guardar" else "Actualizar")
            }
        }
    }
}

fun createImageUri(context: Context): Uri {
    val imageFile = File(
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
        "pawcare_${System.currentTimeMillis()}.jpg"
    )
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        imageFile
    )
}
