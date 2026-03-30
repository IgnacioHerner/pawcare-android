package com.ignaherner.pawcare.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.room.util.splitToIntList
import coil.compose.AsyncImage
import com.ignaherner.pawcare.domain.model.PetSummary
import com.ignaherner.pawcare.domain.model.calcularDiaActual
import com.ignaherner.pawcare.domain.model.calcularDiaNumero
import com.ignaherner.pawcare.domain.model.calcularEdad
import com.ignaherner.pawcare.domain.model.toFriendlyDate

@Composable
fun PetSummaryCard(
    summary: PetSummary,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header foto + nombre + especie
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Foto circular
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    if(summary.pet.fotoUri != null) {
                        AsyncImage(
                            model = summary.pet.fotoUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }else {
                        Text(
                            text = summary.pet.nombre.first().uppercaseChar().toString(),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                Column {
                    Text(
                        text = summary.pet.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${summary.pet.especie.displayName} · ${calcularEdad(summary.pet.fechaNacimiento)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider()

            // Proxima vacuna
            summary.proximaVacuna?.let { vacuna ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("💉", style = MaterialTheme.typography.bodyMedium)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = vacuna.nombre,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        vacuna.proximaDosis?.let {
                            Text(
                                text = it.toFriendlyDate(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } ?: Text(
                text = "\uD83D\uDC89 Sin vacunas registradas",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Medicamento activo con barra de progreso
            summary.medicamentoActivo?.let { medication ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("💊", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = medication.nombre,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = calcularDiaActual(medication.fechaInicio, medication.duracionDias),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    // Barra de progreso
                    val diaActual = calcularDiaNumero(medication.fechaInicio, medication.duracionDias)
                    LinearProgressIndicator(
                        progress = { diaActual.toFloat() / medication.duracionDias.toFloat()},
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } ?: Text(
                text ="\uD83D\uDC8A Sin medicamentos activos",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Ultimo peso
            summary.ultimoPeso?.let { peso ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⚖️", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = "${peso.peso} kg · ${peso.fecha.toFriendlyDate()}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } ?: Text(
                text = "⚖️ Sin registros de peso",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}