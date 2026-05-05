package com.ignaherner.pawcare.presentation.pets

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ignaherner.pawcare.domain.model.Especie
import com.ignaherner.pawcare.domain.model.FechaNacimientoTipo
import com.ignaherner.pawcare.domain.model.Pet
import com.ignaherner.pawcare.domain.model.Sex
import com.ignaherner.pawcare.presentation.components.PawCard
import com.ignaherner.pawcare.presentation.components.PawCareIcon
import com.ignaherner.pawcare.presentation.components.PawIconSize
import com.ignaherner.pawcare.ui.theme.PawRadii
import com.ignaherner.pawcare.ui.theme.PawSpace
import com.ignaherner.pawcare.utils.toFormattedString
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetFormScreen(
    petId: Long?,
    onNavigateBack: () -> Unit,
    viewModel: PetViewModel = hiltViewModel()
) {
    var nombre by remember { mutableStateOf("") }
    var especieSeleccionada by remember { mutableStateOf(Especie.PERRO) }
    var raza by remember { mutableStateOf("") }
    var sexoSeleccionado by remember { mutableStateOf(Sex.MACHO) }
    var fechaNacimiento by remember { mutableStateOf("") }
    var fotoUri by remember { mutableStateOf("") }
    var castrado by remember { mutableStateOf(false) }
    var fechaCastracion by remember { mutableStateOf("") }
    var firestoreId by remember { mutableStateOf("") }
    var codigo by remember { mutableStateOf("") }
    var ownerId by remember { mutableStateOf("") }

    var showFechaNacimientoPicker by remember { mutableStateOf(false) }
    var showFechaCastracionPicker by remember { mutableStateOf(false) }

    val fechaNacimientoPickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )
    val fechaCastracionPickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    val context = LocalContext.current
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { sourceUri ->
            val savedUri = copyImageToInternalStorage(context, sourceUri)
            savedUri?.let { fotoUri = it.toString() }
        }
    }

    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            tempCameraUri?.let { fotoUri = it.toString() }
        }
    }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = createImageUri(context)
            tempCameraUri = uri
            cameraLauncher.launch(uri)
        }
    }

    LaunchedEffect(petId) {
        petId?.let { viewModel.loadPetById(it) }
    }

    val detailState by viewModel.detailState.collectAsStateWithLifecycle()

    LaunchedEffect(detailState) {
        if (detailState is PetDetailState.Success) {
            val pet = (detailState as PetDetailState.Success).pet
            nombre = pet.nombre
            especieSeleccionada = pet.especie
            castrado = pet.castrado
            raza = pet.raza ?: ""
            sexoSeleccionado = pet.sexo ?: Sex.MACHO
            fechaNacimiento = pet.fechaNacimiento ?: ""
            fechaCastracion = pet.fechaCastracion ?: ""
            fotoUri = pet.fotoUri ?: ""
            firestoreId = pet.firestoreId
            codigo = pet.codigo
            ownerId = pet.ownerId
        }
    }

    // DatePicker dialogs
    if (showFechaNacimientoPicker) {
        DatePickerDialog(
            onDismissRequest = { showFechaNacimientoPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    fechaNacimientoPickerState.selectedDateMillis?.let { millis ->
                        val localDate = java.time.Instant
                            .ofEpochMilli(millis)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                        fechaNacimiento = localDate.toFormattedString()
                    }
                    showFechaNacimientoPicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showFechaNacimientoPicker = false }) { Text("Cancelar") }
            }
        ) { DatePicker(state = fechaNacimientoPickerState) }
    }

    if (showFechaCastracionPicker) {
        DatePickerDialog(
            onDismissRequest = { showFechaCastracionPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    fechaCastracionPickerState.selectedDateMillis?.let { millis ->
                        val localDate = java.time.Instant
                            .ofEpochMilli(millis)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                        fechaCastracion = localDate.toFormattedString()
                    }
                    showFechaCastracionPicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showFechaCastracionPicker = false }) { Text("Cancelar") }
            }
        ) { DatePicker(state = fechaCastracionPickerState) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (petId == null) "Nueva mascota" else "Editar mascota",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        PawCareIcon(
                            icon = if (petId == null) Icons.Outlined.Close else Icons.Outlined.ArrowBack,
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
                .padding(horizontal = PawSpace.xl)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(PawSpace.lg)
        ) {
            Spacer(modifier = Modifier.height(PawSpace.sm))

            // Foto
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        shape = CircleShape
                    )
                    .align(Alignment.CenterHorizontally)
                    .clickable { galleryLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (fotoUri.isNotBlank()) {
                    AsyncImage(
                        model = fotoUri,
                        contentDescription = "Foto de $nombre",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        PawCareIcon(
                            icon = Icons.Outlined.CameraAlt,
                            contentDescription = null,
                            size = PawIconSize.large,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "AGREGAR FOTO",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            // Nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                placeholder = { Text("Nombre") },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                shape = RoundedCornerShape(PawRadii.md),
                modifier = Modifier.fillMaxWidth()
            )

            // Especie
            Column(verticalArrangement = Arrangement.spacedBy(PawSpace.sm)) {
                Text(
                    text = "ESPECIE",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(PawSpace.sm),
                    verticalArrangement = Arrangement.spacedBy(PawSpace.sm)
                ) {
                    Especie.entries.forEach { especie ->
                        FilterChip(
                            selected = especieSeleccionada == especie,
                            onClick = { especieSeleccionada = especie },
                            label = { Text(especie.displayName) }
                        )
                    }
                }
            }

            // Raza
            OutlinedTextField(
                value = raza,
                onValueChange = { raza = it },
                placeholder = { Text("Raza") },
                supportingText = { Text("Opcional") },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                shape = RoundedCornerShape(PawRadii.md),
                modifier = Modifier.fillMaxWidth()
            )

            // Sexo
            Column(verticalArrangement = Arrangement.spacedBy(PawSpace.sm)) {
                Text(
                    text = "SEXO",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(PawSpace.sm)
                ) {
                    Sex.entries.forEach { sexo ->
                        FilterChip(
                            selected = sexoSeleccionado == sexo,
                            onClick = { sexoSeleccionado = sexo },
                            label = { Text(sexo.displayName) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Fecha de nacimiento
            OutlinedTextField(
                value = fechaNacimiento,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                placeholder = { Text("Fecha de nacimiento") },
                supportingText = { Text("Si no la sabés, podés omitirla") },
                leadingIcon = {
                    PawCareIcon(
                        icon = Icons.Outlined.CalendarMonth,
                        contentDescription = null,
                        size = PawIconSize.medium,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (fechaNacimiento.isNotBlank()) {
                        IconButton(onClick = { fechaNacimiento = "" }) {
                            PawCareIcon(
                                icon = Icons.Outlined.Close,
                                contentDescription = "Limpiar",
                                size = PawIconSize.default,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(PawRadii.md),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showFechaNacimientoPicker = true },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            // Castración
            PawCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(PawSpace.lg),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Castrado/a",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
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
                            if (!it) fechaCastracion = ""
                        }
                    )
                }
            }

            // Fecha castración
            if (castrado) {
                OutlinedTextField(
                    value = fechaCastracion,
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    placeholder = { Text("Fecha de castración") },
                    leadingIcon = {
                        PawCareIcon(
                            icon = Icons.Outlined.CalendarMonth,
                            contentDescription = null,
                            size = PawIconSize.medium,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    shape = RoundedCornerShape(PawRadii.md),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showFechaCastracionPicker = true },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            Spacer(modifier = Modifier.height(PawSpace.sm))

            // Botón guardar
            Button(
                onClick = {
                    val fechaNacimientoTipo = when {
                        fechaNacimiento.isBlank() -> FechaNacimientoTipo.DESCONOCIDA
                        else -> FechaNacimientoTipo.EXACTA
                    }

                    val nuevaMascota = Pet(
                        id = petId ?: 0L,
                        firestoreId = firestoreId,
                        ownerId = ownerId,
                        codigo = codigo,
                        nombre = nombre.trim(),
                        especie = especieSeleccionada,
                        raza = raza.trim().ifBlank { null },
                        sexo = sexoSeleccionado,
                        fechaNacimientoTipo = fechaNacimientoTipo,
                        fechaNacimiento = fechaNacimiento.ifBlank { null },
                        fotoUri = fotoUri.ifBlank { null },
                        castrado = castrado,
                        fechaCastracion = if (castrado) fechaCastracion.ifBlank { null } else null
                    )
                    if (petId == null) {
                        viewModel.insertPet(nuevaMascota)
                    } else {
                        viewModel.updatePet(nuevaMascota)
                    }
                    onNavigateBack()
                },
                enabled = nombre.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(PawRadii.md),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                PawCareIcon(
                    icon = Icons.Outlined.Check,
                    contentDescription = null,
                    size = PawIconSize.default,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(PawSpace.sm))
                Text(
                    text = if (petId == null) "Guardar mascota" else "Actualizar mascota",
                    style = MaterialTheme.typography.titleSmall
                )
            }

            Spacer(modifier = Modifier.height(PawSpace.xl))
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

fun copyImageToInternalStorage(context: Context, sourceUri: Uri): Uri? {
    return try {
        val inputStream = context.contentResolver.openInputStream(sourceUri)
        val fileName = "pet_${System.currentTimeMillis()}.jpg"
        val outputFile = File(context.filesDir, fileName)

        inputStream?.use { input ->
            outputFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        Uri.fromFile(outputFile)
    } catch (e: Exception) {
        null
    }
}