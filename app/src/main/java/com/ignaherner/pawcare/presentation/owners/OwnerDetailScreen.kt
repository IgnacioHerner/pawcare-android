package com.ignaherner.pawcare.presentation.owners

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ignaherner.pawcare.domain.model.Owner
import com.ignaherner.pawcare.domain.model.Pet
import com.ignaherner.pawcare.presentation.components.PawCard
import com.ignaherner.pawcare.presentation.components.PawCareAvatar
import com.ignaherner.pawcare.presentation.components.PawCareIcon
import com.ignaherner.pawcare.presentation.components.PawIconSize
import com.ignaherner.pawcare.presentation.home.HomeUiState
import com.ignaherner.pawcare.presentation.home.HomeViewModel
import com.ignaherner.pawcare.ui.theme.PawRadii
import com.ignaherner.pawcare.ui.theme.PawSpace
import com.ignaherner.pawcare.utils.calcularEdad

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEdit: () -> Unit,
    onNavigateToPetDetail: (Long) -> Unit,
    viewModel: OwnerViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val ownerState by viewModel.ownerState.collectAsStateWithLifecycle()
    val homeState by homeViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadOwner()
        homeViewModel.loadHome()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "TUTOR",
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
            when (val state = ownerState) {
                is OwnerState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is OwnerState.Error -> {
                    Text(text = state.mensaje, modifier = Modifier.align(Alignment.Center))
                }
                is OwnerState.Success -> {
                    val cantidadMascotas = when (val hs = homeState) {
                        is HomeUiState.Success -> hs.summaries.size
                        else -> 0
                    }

                    OwnerDetailContent(
                        owner = state.owner,
                        cantidadMascotas = cantidadMascotas,
                        mascotas = when (val hs = homeState) {
                            is HomeUiState.Success -> hs.summaries.map { it.pet }
                            else -> emptyList()
                        },
                        onNavigateToPetDetail = onNavigateToPetDetail,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun OwnerDetailContent(
    owner: Owner,
    cantidadMascotas: Int,
    mascotas: List<Pet>,
    onNavigateToPetDetail: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = PawSpace.xl,
            end = PawSpace.xl,
            top = PawSpace.lg,
            bottom = PawSpace.xxl
        ),
        verticalArrangement = Arrangement.spacedBy(PawSpace.lg)
    ) {
        // Hero
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(PawSpace.sm)
            ) {
                PawCareAvatar(
                    fotoUri = owner.fotoUri,
                    nombre = owner.nombre,
                    modifier = Modifier.size(92.dp),
                    textStyle = MaterialTheme.typography.displayMedium
                )

                Text(
                    text = "${owner.nombre} ${owner.apellido}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = when (cantidadMascotas) {
                        0 -> "Sin mascotas registradas"
                        1 -> "Dueño de 1 mascota"
                        else -> "Dueño de $cantidadMascotas mascotas"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Info cards
        item {
            Column(verticalArrangement = Arrangement.spacedBy(PawSpace.md)) {
                OwnerInfoCard(
                    icon = Icons.Outlined.Phone,
                    label = "Teléfono",
                    value = owner.telefono
                )

                OwnerInfoCard(
                    icon = Icons.Outlined.LocationOn,
                    label = "Ubicación",
                    value = buildList {
                        add(owner.ciudad)
                        owner.direccion?.let { add(it) }
                    }.joinToString(", ")
                )

                owner.email?.let {
                    OwnerInfoCard(
                        icon = Icons.Outlined.Email,
                        label = "Email",
                        value = it
                    )
                }
            }
        }

        // Mascotas
        if (mascotas.isNotEmpty()) {
            item {
                Text(
                    text = "MASCOTAS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )
            }

            items(mascotas, key = { it.id }) { pet ->
                PawCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onNavigateToPetDetail(pet.id) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(PawSpace.md),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(PawSpace.md)
                    ) {
                        PawCareAvatar(
                            fotoUri = pet.fotoUri,
                            nombre = pet.nombre,
                            modifier = Modifier.size(48.dp)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = pet.nombre,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${pet.especie.displayName} · ${calcularEdad(pet.fechaNacimiento, pet.fechaNacimientoTipo)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        PawCareIcon(
                            icon = Icons.Outlined.ChevronRight,
                            contentDescription = null,
                            size = PawIconSize.medium,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OwnerInfoCard(
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