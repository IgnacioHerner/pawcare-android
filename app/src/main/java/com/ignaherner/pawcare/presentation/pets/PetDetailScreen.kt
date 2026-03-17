package com.ignaherner.pawcare.presentation.pets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material3.Button
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
import com.ignaherner.pawcare.ui.theme.AppointmentColor
import com.ignaherner.pawcare.ui.theme.MedicationColor
import com.ignaherner.pawcare.ui.theme.VaccineColor
import com.ignaherner.pawcare.ui.theme.WeightColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetDetailScreen(
    petId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onNavigateToVaccines: (Long) -> Unit,
    onNavigateToAppointments: (Long) -> Unit,
    onNavigateToWeight: (Long) ->  Unit,
    onNavigateToMedication: (Long) -> Unit,
    viewModel: PetViewModel = hiltViewModel()
) {
    val detailState by viewModel.detailState.collectAsStateWithLifecycle()

    // Carga la mascota cuando aparece en la pantalla
    LaunchedEffect(petId) {
        viewModel.loadPetById(petId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = {onNavigateToEdit(petId) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when(val state = detailState) {
                is PetDetailState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is PetDetailState.Error -> {
                    Text(
                        text = state.mensaje,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is PetDetailState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Foto circular
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            if (state.pet.fotoUri != null) {
                                AsyncImage(
                                    model = state.pet.fotoUri,
                                    contentDescription = "Foto de ${state.pet.nombre}",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text(
                                    text = state.pet.nombre.first().uppercaseChar().toString(),
                                    style = MaterialTheme.typography.displayMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }

                        // Nombre
                        Text(
                            text = state.pet.nombre,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )

                        // Info de la mascota
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            state.pet.especie.let {
                                InfoRow("Especie", it.displayName)
                            }
                            state.pet.raza?.let {
                                InfoRow("Raza", it)
                            }
                            state.pet.sexo?.let {
                                InfoRow("Sexo", it.displayName)
                            }
                            state.pet.peso?.let {
                                InfoRow("Peso", "$it kg")
                            }
                            state.pet.fechaNacimiento?.let {
                                InfoRow("Nacimiento", it)
                            }
                        }

                        // Grilla de secciones
                        val secciones = listOf(
                            SeccionItem(
                                titulo = "Vacunas",
                                icono = Icons.Default.Favorite,
                                color = VaccineColor,
                                onClick = { onNavigateToVaccines(petId)}
                            ),
                            SeccionItem(
                                titulo = "Medicamentos",
                                icono = Icons.Default.LocalPharmacy,
                                color = MedicationColor,
                                onClick = { onNavigateToMedication(petId) }
                            ),
                            SeccionItem(
                                titulo = "Visitas",
                                icono = Icons.Default.CalendarMonth,
                                color = AppointmentColor,
                                onClick = { onNavigateToAppointments(petId) }
                            ),
                            SeccionItem(
                                titulo = "Peso",
                                icono = Icons.Default.FitnessCenter,
                                color = WeightColor,
                                onClick = { onNavigateToWeight(petId) }
                            )
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            SeccionCard(
                                secciones[0],
                                Modifier.weight(1f)
                            )
                            SeccionCard(
                                secciones[1],
                                Modifier.weight(1f)
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            SeccionCard(
                                secciones[2],
                                Modifier.weight(1f)
                            )
                            SeccionCard(
                                secciones[3],
                                Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}


// Data class para cada card de seccion
data class SeccionItem(
    val titulo: String,
    val icono: ImageVector,
    val color: Color,
    val onClick: () -> Unit
)

// Card de seccion
@Composable
private fun SeccionCard(
    seccion: SeccionItem,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = seccion.onClick,
        modifier = modifier
            .fillMaxSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = seccion.color.copy(alpha = 0.15f)
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
                imageVector = seccion.icono,
                contentDescription = seccion.titulo,
                modifier = Modifier.size(40.dp),
                tint = seccion.color
            )
            Text(
                text = seccion.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = seccion.color
            )
        }
    }
}

// Fila de informacion
@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}