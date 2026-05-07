package com.ignaherner.pawcare.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ignaherner.pawcare.presentation.components.PawCareIcon
import com.ignaherner.pawcare.presentation.components.PawIconSize
import com.ignaherner.pawcare.ui.theme.PawRadio
import com.ignaherner.pawcare.ui.theme.PawSpace
import com.ignaherner.pawcare.ui.theme.VetPrimary
import com.ignaherner.pawcare.ui.theme.VetPrimaryInk
import com.ignaherner.pawcare.ui.theme.VetPrimarySoft

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VetRegisterScreen(
    onNavigateToVetLogin: () -> Unit,
    onRegisterSuccess: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var step by remember { mutableIntStateOf(1) }

// Step 1
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }

// Step 2
    var matricula by remember { mutableStateOf("") }
    var clinica by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsStateWithLifecycle()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onRegisterSuccess(nombre)
            viewModel.resetState()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = {
                        if (step == 2) step = 1 else onNavigateBack()
                    }) {
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = PawSpace.xl)
                .verticalScroll(rememberScrollState())
        ) {
            // Barra de progreso + indicador
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(PawSpace.sm)
            ) {
                // Barra 1
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(VetPrimary)
                )
                // Barra 2
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            if (step == 2) VetPrimary
                            else MaterialTheme.colorScheme.outlineVariant
                        )
                )
                Text(
                    text = "$step/2",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(PawSpace.xl))

            // Badge
            Surface(
                shape = RoundedCornerShape(PawRadio.xl),
                color = VetPrimarySoft
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = PawSpace.md, vertical = PawSpace.sm),
                    horizontalArrangement = Arrangement.spacedBy(PawSpace.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PawCareIcon(
                        icon = Icons.Outlined.LocalHospital,
                        contentDescription = null,
                        size = PawIconSize.small,
                        tint = VetPrimaryInk
                    )
                    Text(
                        text = "REGISTRO PROFESIONAL",
                        style = MaterialTheme.typography.labelSmall,
                        color = VetPrimaryInk
                    )
                }
            }

            Spacer(modifier = Modifier.height(PawSpace.xl))

            if (step == 1) {
                VetRegisterStep1(
                    nombre = nombre,
                    onNombreChange = { nombre = it },
                    apellido = apellido,
                    onApellidoChange = { apellido = it },
                    email = email,
                    onEmailChange = { email = it },
                    telefono = telefono,
                    onTelefonoChange = { telefono = it },
                    onContinuar = { step = 2 }
                )
            } else {
                VetRegisterStep2(
                    matricula = matricula,
                    onMatriculaChange = { matricula = it },
                    clinica = clinica,
                    onClinicaChange = { clinica = it },
                    ciudad = ciudad,
                    onCiudadChange = { ciudad = it },
                    direccion = direccion,
                    onDireccionChange = { direccion = it },
                    password = password,
                    onPasswordChange = { password = it },
                    passwordVisible = passwordVisible,
                    onTogglePassword = { passwordVisible = !passwordVisible },
                    authState = authState,
                    onRegister = {
                        viewModel.registerVet(
                            email = email,
                            password = password,
                            nombre = nombre,
                            apellido = apellido,
                            matricula = matricula,
                            especialidad = null,
                            telefono = telefono,
                            clinica = clinica.ifBlank { null },
                            ciudad = ciudad.ifBlank { null },
                            direccion = direccion.ifBlank { null }
                        )
                    },
                    onNavigateToVetLogin = onNavigateToVetLogin
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// STEP 1 — Datos personales
// ═══════════════════════════════════════════════════════════
@Composable
private fun ColumnScope.VetRegisterStep1(
    nombre: String,
    onNombreChange: (String) -> Unit,
    apellido: String,
    onApellidoChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    telefono: String,
    onTelefonoChange: (String) -> Unit,
    onContinuar: () -> Unit
) {
    Text(
        text = "Datos personales",
        style = MaterialTheme.typography.displayMedium,
        color = MaterialTheme.colorScheme.onBackground
    )

    Spacer(modifier = Modifier.height(PawSpace.sm))

    Text(
        text = "Tu nombre aparecerá en los registros que cargues.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(modifier = Modifier.height(PawSpace.xxl))

    OutlinedTextField(
        value = nombre,
        onValueChange = onNombreChange,
        placeholder = { Text("Nombre") },
        leadingIcon = {
            PawCareIcon(
                icon = Icons.Outlined.PersonOutline,
                contentDescription = null,
                size = PawIconSize.medium,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            imeAction = ImeAction.Next
        ),
        singleLine = true,
        shape = RoundedCornerShape(PawRadio.md),
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = VetPrimary,
            focusedLabelColor = VetPrimary,
            cursorColor = VetPrimary
        )
    )

    Spacer(modifier = Modifier.height(PawSpace.md))

    OutlinedTextField(
        value = apellido,
        onValueChange = onApellidoChange,
        placeholder = { Text("Apellido") },
        leadingIcon = {
            PawCareIcon(
                icon = Icons.Outlined.PersonOutline,
                contentDescription = null,
                size = PawIconSize.medium,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            imeAction = ImeAction.Next
        ),
        singleLine = true,
        shape = RoundedCornerShape(PawRadio.md),
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = VetPrimary,
            focusedLabelColor = VetPrimary,
            cursorColor = VetPrimary
        )
    )

    Spacer(modifier = Modifier.height(PawSpace.md))

    OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        placeholder = { Text("Email profesional") },
        supportingText = { Text("Usá tu email de trabajo si es posible") },
        leadingIcon = {
            PawCareIcon(
                icon = Icons.Outlined.Email,
                contentDescription = null,
                size = PawIconSize.medium,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        singleLine = true,
        shape = RoundedCornerShape(PawRadio.md),
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = VetPrimary,
            focusedLabelColor = VetPrimary,
            cursorColor = VetPrimary
        )
    )

    Spacer(modifier = Modifier.height(PawSpace.md))

    OutlinedTextField(
        value = telefono,
        onValueChange = onTelefonoChange,
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
            imeAction = ImeAction.Done
        ),
        singleLine = true,
        shape = RoundedCornerShape(PawRadio.md),
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = VetPrimary,
            focusedLabelColor = VetPrimary,
            cursorColor = VetPrimary
        )
    )

    Spacer(modifier = Modifier.weight(1f))

    Button(
        onClick = onContinuar,
        enabled = nombre.isNotBlank() && apellido.isNotBlank() && email.isNotBlank(),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(PawRadio.md),
        colors = ButtonDefaults.buttonColors(
            containerColor = VetPrimary,
            contentColor = Color.White
        )
    ) {
        Text(
            text = "Continuar",
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(modifier = Modifier.width(PawSpace.sm))
        PawCareIcon(
            icon = Icons.Outlined.ArrowForward,
            contentDescription = null,
            size = PawIconSize.default,
            tint = Color.White
        )
    }

    Spacer(modifier = Modifier.height(PawSpace.xxl))
}

// ═══════════════════════════════════════════════════════════
// STEP 2 — Credenciales profesionales
// ═══════════════════════════════════════════════════════════
@Composable
private fun ColumnScope.VetRegisterStep2(
    matricula: String,
    onMatriculaChange: (String) -> Unit,
    clinica: String,
    onClinicaChange: (String) -> Unit,
    ciudad: String,
    onCiudadChange: (String) -> Unit,
    direccion: String,
    onDireccionChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onTogglePassword: () -> Unit,
    authState: AuthState,
    onRegister: () -> Unit,
    onNavigateToVetLogin: () -> Unit
) {
    Text(
        text = "Credenciales\nprofesionales",
        style = MaterialTheme.typography.displayMedium,
        color = MaterialTheme.colorScheme.onBackground
    )

    Spacer(modifier = Modifier.height(PawSpace.sm))

    Text(
        text = "Necesitamos validar tu matrícula antes de darte acceso a los historiales.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(modifier = Modifier.height(PawSpace.xxl))

    OutlinedTextField(
        value = matricula,
        onValueChange = onMatriculaChange,
        placeholder = { Text("Matrícula profesional (M.N.)") },
        supportingText = { Text("Número de matrícula nacional del COLVEMA") },
        leadingIcon = {
            PawCareIcon(
                icon = Icons.Outlined.Shield,
                contentDescription = null,
                size = PawIconSize.medium,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(PawRadio.md),
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = VetPrimary,
            focusedLabelColor = VetPrimary,
            cursorColor = VetPrimary
        )
    )

    Spacer(modifier = Modifier.height(PawSpace.md))

    OutlinedTextField(
        value = clinica,
        onValueChange = onClinicaChange,
        placeholder = { Text("Clínica o centro") },
        supportingText = { Text("Opcional") },
        leadingIcon = {
            PawCareIcon(
                icon = Icons.Outlined.LocalHospital,
                contentDescription = null,
                size = PawIconSize.medium,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(PawRadio.md),
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = VetPrimary,
            focusedLabelColor = VetPrimary,
            cursorColor = VetPrimary
        )
    )

    Spacer(modifier = Modifier.height(PawSpace.md))

    OutlinedTextField(
        value = ciudad,
        onValueChange = onCiudadChange,
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
        shape = RoundedCornerShape(PawRadio.md),
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = VetPrimary,
            focusedLabelColor = VetPrimary,
            cursorColor = VetPrimary
        )
    )

    Spacer(modifier = Modifier.height(PawSpace.md))

    OutlinedTextField(
        value = direccion,
        onValueChange = onDireccionChange,
        placeholder = { Text("Dirección del consultorio (opcional)") },
        leadingIcon = {
            PawCareIcon(
                icon = Icons.Outlined.Home,
                contentDescription = null,
                size = PawIconSize.medium,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(PawRadio.md),
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = VetPrimary,
            focusedLabelColor = VetPrimary,
            cursorColor = VetPrimary
        )
    )

    Spacer(modifier = Modifier.height(PawSpace.md))

    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        placeholder = { Text("Contraseña") },
        supportingText = { Text("Mínimo 8 caracteres, con número y símbolo") },
        leadingIcon = {
            PawCareIcon(
                icon = Icons.Outlined.Lock,
                contentDescription = null,
                size = PawIconSize.medium,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        visualTransformation = if (passwordVisible)
            VisualTransformation.None
        else
            PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onTogglePassword) {
                PawCareIcon(
                    icon = if (passwordVisible)
                        Icons.Outlined.Visibility
                    else
                        Icons.Outlined.VisibilityOff,
                    contentDescription = "Ver contraseña",
                    size = PawIconSize.medium,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        singleLine = true,
        shape = RoundedCornerShape(PawRadio.md),
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = VetPrimary,
            focusedLabelColor = VetPrimary,
            cursorColor = VetPrimary
        )
    )

    Spacer(modifier = Modifier.height(PawSpace.lg))

    val errorState = authState as? AuthState.Error
    if (errorState != null) {
        Text(
            text = errorState.mensaje,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(PawSpace.sm))
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(PawRadio.md),
        color = VetPrimarySoft
    ) {
        Row(
            modifier = Modifier.padding(PawSpace.md),
            horizontalArrangement = Arrangement.spacedBy(PawSpace.md),
            verticalAlignment = Alignment.Top
        ) {
            PawCareIcon(
                icon = Icons.Outlined.Info,
                contentDescription = null,
                size = PawIconSize.medium,
                tint = VetPrimaryInk
            )
            Text(
                text = "Verificaremos tu matrícula en 24-48hs. Mientras tanto podrás usar la app con funciones limitadas.",
                style = MaterialTheme.typography.bodySmall,
                color = VetPrimaryInk
            )
        }
    }

    Spacer(modifier = Modifier.height(PawSpace.xl))

    Button(
        onClick = onRegister,
        enabled = matricula.trim().isNotBlank() && password.length >= 6 &&
                authState !is AuthState.Loading,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(PawRadio.md),
        colors = ButtonDefaults.buttonColors(
            containerColor = VetPrimary,
            contentColor = Color.White
        )
    ) {
        if (authState is AuthState.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            PawCareIcon(
                icon = Icons.Outlined.Check,
                contentDescription = null,
                size = PawIconSize.default,
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(PawSpace.sm))
            Text(
                text = "Crear cuenta profesional",
                style = MaterialTheme.typography.titleSmall
            )
        }
    }

    Spacer(modifier = Modifier.height(PawSpace.lg))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "¿Ya tenés cuenta? ",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        TextButton(onClick = onNavigateToVetLogin) {
            Text(
                text = "Ingresá",
                style = MaterialTheme.typography.bodySmall,
                color = VetPrimary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }

    Spacer(modifier = Modifier.height(PawSpace.xl))
}