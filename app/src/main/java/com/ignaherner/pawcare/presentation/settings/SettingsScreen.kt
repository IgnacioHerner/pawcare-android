package com.ignaherner.pawcare.presentation.settings

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ignaherner.pawcare.domain.model.Rol
import com.ignaherner.pawcare.presentation.auth.AuthViewModel
import com.ignaherner.pawcare.presentation.components.PawCard
import com.ignaherner.pawcare.presentation.components.PawCareAvatar
import com.ignaherner.pawcare.presentation.components.PawCareIcon
import com.ignaherner.pawcare.presentation.components.PawIconSize
import com.ignaherner.pawcare.presentation.home.HomeUiState
import com.ignaherner.pawcare.presentation.home.HomeViewModel
import com.ignaherner.pawcare.presentation.owners.OwnerState
import com.ignaherner.pawcare.presentation.owners.OwnerViewModel
import com.ignaherner.pawcare.presentation.vet.VetProfileViewModel
import com.ignaherner.pawcare.presentation.vet.VetState
import com.ignaherner.pawcare.ui.theme.Danger
import com.ignaherner.pawcare.ui.theme.PawSpace

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToOwnerDetail: () -> Unit,
    onNavigateToVetForm: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel(),
    authViewModel: AuthViewModel,
    ownerViewModel: OwnerViewModel = hiltViewModel(),
    vetProfileViewModel: VetProfileViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val ownerState by ownerViewModel.ownerState.collectAsStateWithLifecycle()
    val vetState by vetProfileViewModel.vetState.collectAsStateWithLifecycle()
    val homeState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsStateWithLifecycle()
    val rolActual by authViewModel.rol.collectAsStateWithLifecycle()

    val isVet = rolActual == Rol.VETERINARIO
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (isVet) {
            vetProfileViewModel.loadVeterinario()
        } else {
            ownerViewModel.loadOwner()
            homeViewModel.loadHome()
        }
    }

    val cantidadMascotas = when (val hs = homeState) {
        is HomeUiState.Success -> hs.summaries.size
        else -> 0
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Configuración",
                        style = MaterialTheme.typography.titleLarge
                    )
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(
                start = PawSpace.lg,
                end = PawSpace.lg,
                top = PawSpace.lg,
                bottom = PawSpace.xxl
            ),
            verticalArrangement = Arrangement.spacedBy(PawSpace.lg)
        ) {
            // Perfil — dueño o veterinario
            item {
                if (isVet) {
                    when (val state = vetState) {
                        is VetState.Success -> {
                            PawCard(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = onNavigateToVetForm
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(PawSpace.lg),
                                    horizontalArrangement = Arrangement.spacedBy(PawSpace.md),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    PawCareAvatar(
                                        fotoUri = state.vet.fotoUri,
                                        nombre = state.vet.nombre,
                                        modifier = Modifier.size(56.dp)
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Dr. ${state.vet.nombre} ${state.vet.apellido}",
                                            style = MaterialTheme.typography.titleSmall,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = buildList {
                                                add("Veterinario")
                                                state.vet.matricula.let { add("MP $it") }
                                            }.joinToString(" · "),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    PawCareIcon(
                                        icon = Icons.Outlined.Edit,
                                        contentDescription = "Editar perfil",
                                        size = PawIconSize.medium,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        else -> {}
                    }
                } else {
                    when (val state = ownerState) {
                        is OwnerState.Success -> {
                            PawCard(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = onNavigateToOwnerDetail
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(PawSpace.lg),
                                    horizontalArrangement = Arrangement.spacedBy(PawSpace.md),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    PawCareAvatar(
                                        fotoUri = state.owner.fotoUri,
                                        nombre = state.owner.nombre,
                                        modifier = Modifier.size(56.dp)
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${state.owner.nombre} ${state.owner.apellido}",
                                            style = MaterialTheme.typography.titleSmall,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = when (cantidadMascotas) {
                                                0 -> "Dueño"
                                                1 -> "Dueño · 1 mascota"
                                                else -> "Dueño · $cantidadMascotas mascotas"
                                            },
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    PawCareIcon(
                                        icon = Icons.Outlined.Edit,
                                        contentDescription = "Editar perfil",
                                        size = PawIconSize.medium,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        else -> {}
                    }
                }
            }

            // GENERAL
            item {
                Text(
                    text = "GENERAL",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )
            }

            item {
                PawCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(PawSpace.lg),
                        horizontalArrangement = Arrangement.spacedBy(PawSpace.md),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PawCareIcon(
                            icon = Icons.Outlined.Notifications,
                            contentDescription = null,
                            size = PawIconSize.medium,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Notificaciones",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { viewModel.setNotificationsEnabled(it) }
                        )
                    }
                }
            }

            // SOPORTE
            item {
                Text(
                    text = "SOPORTE",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )
            }

            item {
                PawCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "Probá PawCare, la libreta sanitaria digital para tu mascota. https://pawcare.app"
                                    )
                                }
                                context.startActivity(
                                    Intent.createChooser(shareIntent, "Compartir PawCare")
                                )
                            }
                            .padding(PawSpace.lg),
                        horizontalArrangement = Arrangement.spacedBy(PawSpace.md),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PawCareIcon(
                            icon = Icons.Outlined.Share,
                            contentDescription = null,
                            size = PawIconSize.medium,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Compartir PawCare",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        PawCareIcon(
                            icon = Icons.Outlined.ChevronRight,
                            contentDescription = null,
                            size = PawIconSize.medium,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // CUENTA
            item {
                Text(
                    text = "CUENTA",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )
            }

            item {
                PawCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.logout()
                                authViewModel.logout()
                                onNavigateToLogin()
                            }
                            .padding(PawSpace.lg),
                        horizontalArrangement = Arrangement.spacedBy(PawSpace.md),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PawCareIcon(
                            icon = Icons.Outlined.Logout,
                            contentDescription = null,
                            size = PawIconSize.medium,
                            tint = Danger
                        )
                        Text(
                            text = "Cerrar sesión",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Danger
                        )
                    }
                }
            }

            // Versión
            item {
                Text(
                    text = "PawCare v1.0.0",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = PawSpace.lg)
                )
            }
        }
    }
}