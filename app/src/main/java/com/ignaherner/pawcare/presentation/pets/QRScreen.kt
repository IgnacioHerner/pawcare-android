package com.ignaherner.pawcare.presentation.pets

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ignaherner.pawcare.data.local.QRGenerator
import com.ignaherner.pawcare.domain.model.MedicationStatus
import com.ignaherner.pawcare.presentation.components.InfoRow
import com.ignaherner.pawcare.presentation.medications.MedicationUiState
import com.ignaherner.pawcare.presentation.medications.MedicationViewModel
import com.ignaherner.pawcare.presentation.owners.OwnerState
import com.ignaherner.pawcare.presentation.owners.OwnerViewModel
import okhttp3.internal.notify

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRScreen(
    petId: Long,
    onNavigateBack: () -> Unit,
    petViewModel: PetViewModel = hiltViewModel(),
    ownerViewModel: OwnerViewModel = hiltViewModel(),
    medicationViewModel: MedicationViewModel = hiltViewModel()
){
    val detailState by petViewModel.detailState.collectAsStateWithLifecycle()
    val ownerState by ownerViewModel.ownerState.collectAsStateWithLifecycle()
    val medicationState by medicationViewModel.medicationDetailState.collectAsStateWithLifecycle()

    LaunchedEffect(petId) {
        petViewModel.loadPetById(petId)
        ownerViewModel.loadOwner()
        medicationViewModel.loadMedications(petId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Codigo QR") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
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
                            firestoreId = pet.firestoreId,
                            nombreDueno = "${owner.nombre}${owner.apellido}",
                            telefono = owner.telefono,
                            medicamentosActivos = medicamentosActivos
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(24.dp),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = pet.nombre,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Image(
                            bitmap = qrBitmap.asImageBitmap(),
                            contentDescription = "QR de ${pet.nombre}",
                            modifier = Modifier
                                .size(280.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )

                        Text(
                            text = "Escaneá este código para ver \nla información de ${pet.nombre}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                InfoRow("🐾 Mascota", pet.nombre)
                                InfoRow("👤 Dueño", "${owner.nombre}${owner.apellido}")
                                InfoRow("📞 Teléfono", owner.telefono)
                                if (medicamentosActivos.isNotEmpty()) {
                                    InfoRow(
                                        "💊 Medicamentos",
                                        medicamentosActivos.joinToString(", ")
                                    )
                                }
                            }
                        }
                    }
                }
                else -> CircularProgressIndicator()
            }
        }
    }
}