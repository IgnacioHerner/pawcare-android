package com.ignaherner.pawcare.presentation.vet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material3.AssistChip
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ignaherner.pawcare.domain.model.Pet
import com.ignaherner.pawcare.domain.model.calcularEdad
import com.ignaherner.pawcare.ui.theme.AppointmentColor
import com.ignaherner.pawcare.ui.theme.MedicationColor
import com.ignaherner.pawcare.ui.theme.VaccineColor
import com.ignaherner.pawcare.ui.theme.WeightColor
import okhttp3.internal.wait

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VetPetDetailScreen(
    firestoreId: String,
    onNavigateBack: () -> Unit,
    viewModel: VetViewModel = hiltViewModel()
) {
    val searchState by viewModel.searchState.collectAsStateWithLifecycle()

    LaunchedEffect(firestoreId) {
        viewModel.buscarMascota(firestoreId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de la mascota") },
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
                .fillMaxWidth()
                .padding(paddingValues)
        ) {
            when(val state = searchState) {
                is VetSearchState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is VetSearchState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("😔", style = MaterialTheme.typography.displayMedium)
                        Text(
                            text = state.mensaje,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
                is VetSearchState.Success -> {
                    VetPetDetailContent(
                        pet = state.pet,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                is VetSearchState.Idle -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun VetPetDetailContent(
    pet: Pet,
    modifier: Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    if (pet.fotoUri != null) {
                        AsyncImage(
                            model = pet.fotoUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = pet.nombre.first().uppercaseChar().toString(),
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = pet.nombre,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AssistChip(onClick = {}, label = { Text(pet.especie.displayName)})
                        pet.raza?.let {
                            AssistChip(onClick = {}, label = {Text(it)})
                        }
                        pet.sexo?.let {
                            AssistChip(onClick = {}, label = {Text(it.displayName)})
                        }
                        AssistChip(
                            onClick = {},
                            label = {
                                Text(calcularEdad(pet.fechaNacimiento, pet.fechaNacimientoTipo))
                            }
                        )
                        if (pet.castrado) {
                            AssistChip(
                                onClick = {},
                                label = { Text("✂️ Castrado/a")}
                            )
                        }
                    }
                }
            }
        }

        // ID para referencia
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ID: ${pet.firestoreId}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Secciones
        item {
            Text(
                text = "Historial Clínico 🏥",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SeccionVetCard(
                    titulo = "Vacunas",
                    icono = Icons.Default.Favorite,
                    color = VaccineColor,
                    onClick = { },
                    modifier = Modifier.weight(1f)
                )
                SeccionVetCard(
                    titulo = "Medicamentos",
                    icono = Icons.Default.LocalPharmacy,
                    color = MedicationColor,
                    onClick = { },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SeccionVetCard(
                    titulo = "Condiciones",
                    icono = Icons.Default.MedicalServices,
                    color = Color(0xFFE91E63),
                    onClick = { },
                    modifier = Modifier.weight(1f)
                )
                SeccionVetCard(
                    titulo = "Desparasitación",
                    icono = Icons.Default.Vaccines,
                    color = Color(0xFF795548),
                    onClick = { },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Text(
                text = "Seguimiento 📊",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SeccionVetCard(
                    titulo = "Peso",
                    icono = Icons.Default.FitnessCenter,
                    color = WeightColor,
                    onClick = { },
                    modifier = Modifier.weight(1f)
                )
                SeccionVetCard(
                    titulo = "Visitas",
                    icono = Icons.Default.CalendarMonth,
                    color = AppointmentColor,
                    onClick = { },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SeccionVetCard(
    titulo: String,
    icono: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = titulo,
                modifier = Modifier.size(40.dp),
                tint = color
            )
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
        }
    }
}