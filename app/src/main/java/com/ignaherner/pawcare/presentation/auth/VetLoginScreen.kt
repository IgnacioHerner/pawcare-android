package com.ignaherner.pawcare.presentation.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PersonOutline
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ignaherner.pawcare.presentation.components.PawCareIcon
import com.ignaherner.pawcare.presentation.components.PawIconSize
import com.ignaherner.pawcare.ui.theme.PawRadii
import com.ignaherner.pawcare.ui.theme.PawSpace
import com.ignaherner.pawcare.ui.theme.VetPrimary
import com.ignaherner.pawcare.ui.theme.VetPrimaryInk
import com.ignaherner.pawcare.ui.theme.VetPrimarySoft

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VetLoginScreen(
    onNavigateToVetRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsStateWithLifecycle()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onLoginSuccess()
            viewModel.resetState()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {},
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = PawSpace.xl)
                .verticalScroll(rememberScrollState())
        ) {
            // Badge "MODO VETERINARIO"
            Surface(
                shape = RoundedCornerShape(PawRadii.xl),
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
                        text = "MODO VETERINARIO",
                        style = MaterialTheme.typography.labelSmall,
                        color = VetPrimaryInk
                    )
                }
            }

            Spacer(modifier = Modifier.height(PawSpace.xl))

            // Título
            Text(
                text = "Acceso profesional",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(PawSpace.sm))

            // Subtítulo
            Text(
                text = "Ingresá con tu matrícula y credenciales para acceder a los historiales clínicos.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(PawSpace.xxl))

            // Email profesional
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email profesional") },
                leadingIcon = {
                    PawCareIcon(
                        icon = Icons.Outlined.PersonOutline,
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
                shape = RoundedCornerShape(PawRadii.md),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(PawSpace.md))

            // Contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Contraseña") },
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
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
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
                shape = RoundedCornerShape(PawRadii.md),
                modifier = Modifier.fillMaxWidth()
            )

            // Olvidaste contraseña
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { /* TODO */ }) {
                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Error
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

            // Aviso profesional
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(PawRadii.md),
                color = VetPrimarySoft
            ) {
                Row(
                    modifier = Modifier.padding(PawSpace.md),
                    horizontalArrangement = Arrangement.spacedBy(PawSpace.md),
                    verticalAlignment = Alignment.Top
                ) {
                    PawCareIcon(
                        icon = Icons.Outlined.Shield,
                        contentDescription = null,
                        size = PawIconSize.medium,
                        tint = VetPrimaryInk
                    )
                    Text(
                        text = "Al ingresar como profesional, confirmás que los datos que modifiques quedarán registrados con tu identidad y matrícula.",
                        style = MaterialTheme.typography.bodySmall,
                        color = VetPrimaryInk
                    )
                }
            }

            Spacer(modifier = Modifier.height(PawSpace.xl))

            // Botón login
            Button(
                onClick = { viewModel.login(email, password) },
                enabled = email.isNotBlank() && password.isNotBlank() &&
                        authState !is AuthState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(PawRadii.md),
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
                    Text(
                        text = "Ingresar como profesional",
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(PawSpace.lg))

            // Ir a registro vet
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¿Primera vez? ",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = onNavigateToVetRegister) {
                    Text(
                        text = "Registrarme como veterinario",
                        style = MaterialTheme.typography.bodySmall,
                        color = VetPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(PawSpace.xl))
        }
    }
}