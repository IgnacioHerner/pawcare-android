package com.ignaherner.pawcare.presentation.vet

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ignaherner.pawcare.domain.model.Veterinario
import com.ignaherner.pawcare.presentation.components.PawCareIcon
import com.ignaherner.pawcare.presentation.components.PawIconSize
import com.ignaherner.pawcare.presentation.pets.copyImageToInternalStorage
import com.ignaherner.pawcare.ui.theme.PawRadii
import com.ignaherner.pawcare.ui.theme.PawSpace
import com.ignaherner.pawcare.ui.theme.VetPrimary
import com.ignaherner.pawcare.ui.theme.VetPrimaryInk
import com.ignaherner.pawcare.ui.theme.VetPrimarySoft

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VetFormScreen(
    onNavigateBack: () -> Unit,
    viewModel: VetProfileViewModel = hiltViewModel()
) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var matricula by remember { mutableStateOf("") }
    var especialidad by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var clinica by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("") }
    var fotoUri by remember { mutableStateOf("") }

    val context = LocalContext.current
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { sourceUri ->
            val savedUri = copyImageToInternalStorage(context, sourceUri)
            savedUri?.let { fotoUri = it.toString() }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadVeterinario()
    }

    val vetState by viewModel.vetState.collectAsStateWithLifecycle()

    LaunchedEffect(vetState) {
        if (vetState is VetState.Success) {
            val vet = (vetState as VetState.Success).vet
            nombre = vet.nombre
            apellido = vet.apellido
            matricula = vet.matricula
            especialidad = vet.especialidad ?: ""
            telefono = vet.telefono
            clinica = vet.clinica ?: ""
            direccion = vet.direccion ?: ""
            ciudad = vet.ciudad ?: ""
            fotoUri = vet.fotoUri ?: ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (vetState is VetState.Success) "Editar perfil" else "Completá tu perfil")
                },
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
                    .background(VetPrimarySoft)
                    .align(Alignment.CenterHorizontally)
                    .clickable { galleryLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (fotoUri.isNotBlank()) {
                    AsyncImage(
                        model = fotoUri,
                        contentDescription = "Foto de perfil",
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
                            tint = VetPrimaryInk
                        )
                        Text(
                            text = "Foto",
                            style = MaterialTheme.typography.labelSmall,
                            color = VetPrimaryInk
                        )
                    }
                }
            }


            // Nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                placeholder = { Text("Nombre") },
                leadingIcon = {
                    PawCareIcon(
                        icon = Icons.Outlined.PersonOutline,
                        contentDescription = null,
                        size = PawIconSize.medium,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(PawRadii.md),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VetPrimary,
                    focusedLabelColor = VetPrimary,
                    cursorColor = VetPrimary
                )
            )

            // Apellido
            OutlinedTextField(
                value = apellido,
                onValueChange = { apellido = it },
                placeholder = { Text("Apellido") },
                leadingIcon = {
                    PawCareIcon(
                        icon = Icons.Outlined.PersonOutline,
                        contentDescription = null,
                        size = PawIconSize.medium,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(PawRadii.md),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VetPrimary,
                    focusedLabelColor = VetPrimary,
                    cursorColor = VetPrimary
                )
            )

            // Matrícula
            OutlinedTextField(
                value = matricula,
                onValueChange = { matricula = it },
                placeholder = { Text("Matrícula profesional") },
                leadingIcon = {
                    PawCareIcon(
                        icon = Icons.Outlined.Badge,
                        contentDescription = null,
                        size = PawIconSize.medium,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(PawRadii.md),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VetPrimary,
                    focusedLabelColor = VetPrimary,
                    cursorColor = VetPrimary
                )
            )

            // Especialidad
            OutlinedTextField(
                value = especialidad,
                onValueChange = { especialidad = it },
                placeholder = { Text("Especialidad (opcional)") },
                leadingIcon = {
                    PawCareIcon(
                        icon = Icons.Outlined.MedicalServices,
                        contentDescription = null,
                        size = PawIconSize.medium,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(PawRadii.md),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VetPrimary,
                    focusedLabelColor = VetPrimary,
                    cursorColor = VetPrimary
                )
            )

            // Teléfono
            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                placeholder = { Text("Teléfono") },
                leadingIcon = {
                    PawCareIcon(
                        icon = Icons.Outlined.Phone,
                        contentDescription = null,
                        size = PawIconSize.medium,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                shape = RoundedCornerShape(PawRadii.md),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VetPrimary,
                    focusedLabelColor = VetPrimary,
                    cursorColor = VetPrimary
                )
            )

            // Clínica
            OutlinedTextField(
                value = clinica,
                onValueChange = { clinica = it },
                placeholder = { Text("Clínica / Consultorio (opcional)") },
                leadingIcon = {
                    PawCareIcon(
                        icon = Icons.Outlined.LocalHospital,
                        contentDescription = null,
                        size = PawIconSize.medium,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(PawRadii.md),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VetPrimary,
                    focusedLabelColor = VetPrimary,
                    cursorColor = VetPrimary
                )
            )

            // Dirección
            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it },
                placeholder = { Text("Dirección (opcional)") },
                leadingIcon = {
                    PawCareIcon(
                        icon = Icons.Outlined.Home,
                        contentDescription = null,
                        size = PawIconSize.medium,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(PawRadii.md),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VetPrimary,
                    focusedLabelColor = VetPrimary,
                    cursorColor = VetPrimary
                )
            )

            // Ciudad
            OutlinedTextField(
                value = ciudad,
                onValueChange = { ciudad = it },
                placeholder = { Text("Ciudad (opcional)") },
                leadingIcon = {
                    PawCareIcon(
                        icon = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        size = PawIconSize.medium,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(PawRadii.md),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VetPrimary,
                    focusedLabelColor = VetPrimary,
                    cursorColor = VetPrimary
                )
            )

            Spacer(modifier = Modifier.height(PawSpace.sm))

            // Botón guardar
            Button(
                onClick = {
                    val nuevoVet = Veterinario(
                        nombre = nombre.trim(),
                        apellido = apellido.trim(),
                        matricula = matricula.trim(),
                        especialidad = especialidad.trim().ifBlank { null },
                        telefono = telefono.trim(),
                        clinica = clinica.trim().ifBlank { null },
                        direccion = direccion.trim().ifBlank { null },
                        ciudad = ciudad.trim().ifBlank { null },
                        fotoUri = fotoUri.ifBlank { null }
                    )
                    viewModel.guardarVeterinario(nuevoVet)
                    onNavigateBack()
                },
                enabled = nombre.isNotBlank() &&
                        apellido.isNotBlank() &&
                        matricula.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(PawRadii.md),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VetPrimary,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = if (vetState is VetState.Success) "Actualizar" else "Guardar",
                    style = MaterialTheme.typography.titleSmall
                )
            }

            Spacer(modifier = Modifier.height(PawSpace.xl))
        }
    }
}