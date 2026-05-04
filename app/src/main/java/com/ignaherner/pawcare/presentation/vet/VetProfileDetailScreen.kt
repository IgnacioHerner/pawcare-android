package com.ignaherner.pawcare.presentation.vet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.auth.FirebaseAuth
import com.ignaherner.pawcare.domain.model.Veterinario
import com.ignaherner.pawcare.presentation.components.EmptyState
import com.ignaherner.pawcare.presentation.components.PawCard
import com.ignaherner.pawcare.presentation.components.PawCareAvatar
import com.ignaherner.pawcare.presentation.components.PawCareIcon
import com.ignaherner.pawcare.presentation.components.PawIconSize
import com.ignaherner.pawcare.ui.theme.PawRadii
import com.ignaherner.pawcare.ui.theme.PawSpace
import com.ignaherner.pawcare.ui.theme.VetPrimaryInk
import com.ignaherner.pawcare.ui.theme.VetPrimarySoft

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VetProfileDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEdit: () -> Unit,
    viewModel: VetProfileViewModel = hiltViewModel()
) {
    val vetState by viewModel.vetState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadVeterinario()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "PERFIL PROFESIONAL",
                        style = MaterialTheme.typography.labelMedium,
                        letterSpacing = 1.sp
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
                actions = {
                    IconButton(onClick = onNavigateToEdit) {
                        PawCareIcon(
                            icon = Icons.Outlined.Edit,
                            contentDescription = "Editar",
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = vetState) {
                is VetState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is VetState.Error -> {
                    Text(text = state.mensaje, modifier = Modifier.align(Alignment.Center))
                }
                is VetState.Empty -> {
                    EmptyState(
                        icon = Icons.Outlined.PersonOutline,
                        title = "Sin perfil profesional",
                        body = "Completá tu perfil para que los dueños vean tu información"
                    )
                }
                is VetState.Success -> {
                    VetProfileContent(
                        vet = state.vet,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun VetProfileContent(
    vet: Veterinario,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = PawSpace.xl, vertical = PawSpace.lg),
        verticalArrangement = Arrangement.spacedBy(PawSpace.lg)
    ) {
        // Hero — avatar + nombre + matrícula
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PawSpace.sm)
        ) {
            PawCareAvatar(
                fotoUri = vet.fotoUri,
                nombre = vet.nombre,
                modifier = Modifier.size(92.dp),
                textStyle = MaterialTheme.typography.displayMedium
            )

            Text(
                text = "Dr. ${vet.nombre} ${vet.apellido}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Pills
            Row(
                horizontalArrangement = Arrangement.spacedBy(PawSpace.sm)
            ) {
                Surface(
                    shape = RoundedCornerShape(PawRadii.xs),
                    color = VetPrimarySoft
                ) {
                    Text(
                        text = "MP ${vet.matricula}",
                        style = MaterialTheme.typography.labelMedium,
                        color = VetPrimaryInk,
                        modifier = Modifier.padding(horizontal = PawSpace.md, vertical = PawSpace.xs)
                    )
                }
                vet.especialidad?.let {
                    Surface(
                        shape = RoundedCornerShape(PawRadii.xs),
                        color = VetPrimarySoft
                    ) {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.labelMedium,
                            color = VetPrimaryInk,
                            modifier = Modifier.padding(horizontal = PawSpace.md, vertical = PawSpace.xs)
                        )
                    }
                }
            }
        }

        // Info cards
        Column(verticalArrangement = Arrangement.spacedBy(PawSpace.md)) {
            if (vet.telefono.isNotBlank()) {
                VetInfoCard(
                    icon = Icons.Outlined.Phone,
                    label = "Teléfono",
                    value = vet.telefono
                )
            }

            vet.clinica?.let {
                VetInfoCard(
                    icon = Icons.Outlined.LocalHospital,
                    label = "Clínica / Consultorio",
                    value = it
                )
            }

            val ubicacion = buildList {
                vet.ciudad?.let { add(it) }
                vet.direccion?.let { add(it) }
            }.joinToString(", ")

            if (ubicacion.isNotBlank()) {
                VetInfoCard(
                    icon = Icons.Outlined.LocationOn,
                    label = "Ubicación",
                    value = ubicacion
                )
            }

            // Email del Firebase Auth
            FirebaseAuth.getInstance().currentUser?.email?.let {
                VetInfoCard(
                    icon = Icons.Outlined.Email,
                    label = "Email",
                    value = it
                )
            }
        }
    }
}

@Composable
private fun VetInfoCard(
    icon: ImageVector,
    label: String,
    value: String
) {
    PawCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PawSpace.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(PawSpace.md)
        ) {
            PawCareIcon(
                icon = icon,
                contentDescription = null,
                size = PawIconSize.medium,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}