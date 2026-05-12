package com.ignaherner.mispatitas.presentation.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ignaherner.mispatitas.R
import com.ignaherner.mispatitas.presentation.components.PawCard
import com.ignaherner.mispatitas.presentation.components.PawCareIcon
import com.ignaherner.mispatitas.presentation.components.PawIconSize
import com.ignaherner.mispatitas.ui.theme.PawRadio
import com.ignaherner.mispatitas.ui.theme.PawSpace
import com.ignaherner.mispatitas.ui.theme.VetPrimary
import com.ignaherner.mispatitas.ui.theme.VetPrimaryInk
import com.ignaherner.mispatitas.ui.theme.VetPrimarySoft

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToVetRegister: () -> Unit = {},
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isVetMode by remember { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsStateWithLifecycle()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onLoginSuccess()
            viewModel.resetState()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = PawSpace.xl)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(PawSpace.xxl))
            Spacer(modifier = Modifier.height(PawSpace.xxl))

            // Toggle Dueño / Veterinario
            PawCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(PawSpace.xs)
                ) {
                    // Dueño tab
                    Surface(
                        onClick = { isVetMode = false },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(PawRadio.sm),
                        color = if (!isVetMode)
                            MaterialTheme.colorScheme.primary
                        else
                            Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = PawSpace.md),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_paw),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (!isVetMode)
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(PawSpace.sm))
                            Text(
                                text = "Dueño",
                                style = MaterialTheme.typography.titleSmall,
                                color = if (!isVetMode)
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Veterinario tab
                    Surface(
                        onClick = { isVetMode = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(PawRadio.sm),
                        color = if (isVetMode)
                            VetPrimary
                        else
                            Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = PawSpace.md),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            PawCareIcon(
                                icon = Icons.Outlined.LocalHospital,
                                contentDescription = null,
                                size = PawIconSize.small,
                                tint = if (isVetMode)
                                    Color.White
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(PawSpace.sm))
                            Text(
                                text = "Veterinario",
                                style = MaterialTheme.typography.titleSmall,
                                color = if (isVetMode)
                                    Color.White
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(PawSpace.xxl))

            // Título dinámico
            Text(
                text = if (isVetMode) "Acceso profesional" else "Bienvenido de vuelta",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(PawSpace.sm))

            Text(
                text = if (isVetMode)
                    "Ingresá con tus credenciales para acceder a los historiales clínicos."
                else
                    "Ingresá para ver la libreta de tus mascotas.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(PawSpace.xxl))

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text(if (isVetMode) "Email profesional" else "Email") },
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
                shape = RoundedCornerShape(PawRadio.md),
                colors = if (isVetMode) OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VetPrimary,
                    focusedLabelColor = VetPrimary,
                    cursorColor = VetPrimary
                ) else OutlinedTextFieldDefaults.colors(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(PawSpace.md))

            // Password
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
                shape = RoundedCornerShape(PawRadio.md),
                colors = if (isVetMode) OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VetPrimary,
                    focusedLabelColor = VetPrimary,
                    cursorColor = VetPrimary
                ) else OutlinedTextFieldDefaults.colors(),
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

            // Banner vet
            if (isVetMode) {
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
                            icon = Icons.Outlined.Shield,
                            contentDescription = null,
                            size = PawIconSize.medium,
                            tint = VetPrimaryInk
                        )
                        Text(
                            text = "Al ingresar como profesional, los datos que modifiques quedarán registrados con tu identidad y matrícula.",
                            style = MaterialTheme.typography.bodySmall,
                            color = VetPrimaryInk
                        )
                    }
                }
                Spacer(modifier = Modifier.height(PawSpace.lg))
            }

            Spacer(modifier = Modifier.height(PawSpace.lg))

            // Botón login
            Button(
                onClick = { viewModel.login(email, password, isVetMode) },
                enabled = email.isNotBlank() && password.isNotBlank() &&
                        authState !is AuthState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(PawRadio.md),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isVetMode) VetPrimary else MaterialTheme.colorScheme.primary,
                    contentColor = if (isVetMode) Color.White else MaterialTheme.colorScheme.onPrimary
                )
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = if (isVetMode) Color.White else MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = if (isVetMode) "Ingresar como profesional" else "Ingresar",
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(PawSpace.xl))

            // Divider
            if (!isVetMode) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(PawSpace.md)
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    Text(
                        text = "o",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }

                Spacer(modifier = Modifier.height(PawSpace.xl))

                // Google button
                OutlinedButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(PawRadio.md),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Text(
                        text = "Continuar con Google",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(PawSpace.lg))
            }

            // Ir a registro
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isVetMode) "¿Primera vez? " else "¿No tenés cuenta? ",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(
                    onClick = {
                        if (isVetMode) onNavigateToVetRegister() else onNavigateToRegister()
                    }
                ) {
                    Text(
                        text = if (isVetMode) "Registrarme como vet" else "Registrate",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isVetMode) VetPrimary else MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(PawSpace.xl))
        }
    }
}
