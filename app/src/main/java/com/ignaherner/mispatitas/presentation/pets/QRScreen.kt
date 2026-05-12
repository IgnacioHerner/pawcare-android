package com.ignaherner.mispatitas.presentation.pets

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ignaherner.mispatitas.R
import com.ignaherner.mispatitas.data.local.QRGenerator
import com.ignaherner.mispatitas.domain.model.MedicationStatus
import com.ignaherner.mispatitas.presentation.components.PawCard
import com.ignaherner.mispatitas.presentation.components.PawCareAvatar
import com.ignaherner.mispatitas.presentation.components.PawCareIcon
import com.ignaherner.mispatitas.presentation.components.PawIconSize
import com.ignaherner.mispatitas.presentation.medications.MedicationUiState
import com.ignaherner.mispatitas.presentation.medications.MedicationViewModel
import com.ignaherner.mispatitas.presentation.owners.OwnerState
import com.ignaherner.mispatitas.presentation.owners.OwnerViewModel
import com.ignaherner.mispatitas.ui.theme.PawRadio
import com.ignaherner.mispatitas.ui.theme.PawSpace

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRScreen(
    petId: Long,
    onNavigateBack: () -> Unit,
    petViewModel: PetViewModel = hiltViewModel(),
    ownerViewModel: OwnerViewModel = hiltViewModel(),
    medicationViewModel: MedicationViewModel = hiltViewModel()
) {
    val detailState by petViewModel.detailState.collectAsStateWithLifecycle()
    val ownerState by ownerViewModel.ownerState.collectAsStateWithLifecycle()
    val medicationState by medicationViewModel.medicationDetailState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(petId) {
        petViewModel.loadPetById(petId)
        ownerViewModel.loadOwner()
        medicationViewModel.loadMedications(petId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "TARJETA DE IDENTIDAD",
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
                    IconButton(onClick = {
                        val pet = (detailState as? PetDetailState.Success)?.pet ?: return@IconButton
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Libreta sanitaria de ${pet.nombre} en MisPatitas. Código: ${pet.codigo}"
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Compartir"))
                    }) {
                        PawCareIcon(
                            icon = Icons.Outlined.Share,
                            contentDescription = "Compartir",
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
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                detailState is PetDetailState.Success && ownerState is OwnerState.Success -> {
                    val pet = (detailState as PetDetailState.Success).pet
                    val owner = (ownerState as OwnerState.Success).owner

                    val medicamentosActivos = when (val ms = medicationState) {
                        is MedicationUiState.Success -> ms.medications
                            .filter { it.status == MedicationStatus.ACTIVO }
                            .map { it.nombre }
                        else -> emptyList()
                    }

                    val qrGenerator = remember { QRGenerator() }
                    val qrBitmap = remember(pet, owner, medicamentosActivos) {
                        qrGenerator.generarQR(
                            nombreMascota = pet.nombre,
                            codigo = pet.codigo,
                            nombreDueno = "${owner.nombre} ${owner.apellido}",
                            telefono = owner.telefono,
                            medicamentosActivos = medicamentosActivos
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = PawSpace.lg),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(PawSpace.sm))

                        // Tarjeta principal
                        PawCard(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Header verde
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.primary)
                                        .padding(PawSpace.md)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(PawSpace.sm),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Image(
                                                painter = painterResource(id = R.drawable.ic_mispatitas_logo),
                                                contentDescription = "MisPatitas logo",
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = "MISPATITAS · LIBRETA DIGITAL",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onPrimary,
                                                letterSpacing = 1.sp
                                            )
                                        }
                                        PawCareIcon(
                                            icon = Icons.Outlined.Shield,
                                            contentDescription = null,
                                            size = PawIconSize.default,
                                            tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(PawSpace.xl))

                                // Foto de la mascota
                                PawCareAvatar(
                                    fotoUri = pet.fotoUri,
                                    nombre = pet.nombre,
                                    modifier = Modifier.size(80.dp),
                                    textStyle = MaterialTheme.typography.displayMedium
                                )

                                Spacer(modifier = Modifier.height(PawSpace.md))

                                // Nombre
                                Text(
                                    text = pet.nombre,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                // Subtítulo
                                Text(
                                    text = buildList {
                                        pet.raza?.let { add(it) }
                                        pet.sexo?.let { add(it.displayName) }
                                    }.joinToString(" · "),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(PawSpace.xl))

                                // QR Code
                                Box(
                                    modifier = Modifier
                                        .size(200.dp)
                                        .clip(RoundedCornerShape(PawRadio.md))
                                        .border(
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.outlineVariant,
                                            shape = RoundedCornerShape(PawRadio.md)
                                        )
                                        .padding(PawSpace.md),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        bitmap = qrBitmap.asImageBitmap(),
                                        contentDescription = "QR de ${pet.nombre}",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }

                                Spacer(modifier = Modifier.height(PawSpace.md))

                                // Código amigable
                                Text(
                                    text = pet.codigo,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 2.sp
                                )

                                Spacer(modifier = Modifier.height(PawSpace.xl))
                            }
                        }

                        Spacer(modifier = Modifier.height(PawSpace.lg))

                        // Texto ayuda
                        Text(
                            text = "Mostrá este código al veterinario para acceder al historial completo de ${pet.nombre}.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = PawSpace.xl)
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        // Botón compartir
                        OutlinedButton(
                            onClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "Libreta sanitaria de ${pet.nombre} en PawCare.\nCódigo: ${pet.codigo}\nDueño: ${owner.nombre} ${owner.apellido}\nTel: ${owner.telefono}"
                                    )
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Compartir"))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(PawRadio.md),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                        ) {
                            PawCareIcon(
                                icon = Icons.Outlined.Share,
                                contentDescription = null,
                                size = PawIconSize.default,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(PawSpace.sm))
                            Text(
                                text = "Compartir",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(PawSpace.xxl))
                    }
                }
                else -> CircularProgressIndicator()
            }
        }
    }
}
