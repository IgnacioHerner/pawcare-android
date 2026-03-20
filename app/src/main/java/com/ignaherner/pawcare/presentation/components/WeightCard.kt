package com.ignaherner.pawcare.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ignaherner.pawcare.domain.model.Weight
import com.ignaherner.pawcare.domain.model.toFriendlyDate
import com.ignaherner.pawcare.ui.theme.PawCareTheme

@Composable
fun WeightCard(
    weight: Weight,
    pesoAnterior: Double?,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
){
    // pesoAnterior = null → variacion = null (primer registro, no hay anterior)
    // pesoAnterior = 32.0 → variacion = 34.0 - 32.0 = 2.0
    val variacion = pesoAnterior?.let { weight.peso - it }

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Peso principal
            Column {
                Text(
                    text = "${weight.peso} kg",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = weight.fecha.toFriendlyDate(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                weight.notas?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontStyle = FontStyle.Italic
                    )
                }
            }

            // Variacion respecto al anterior
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                variacion?.let {
                    val color = if (it >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                    val signo = if (it >= 0) "+" else ""
                    Text(
                        text = "$signo${"%.1f".format(it)} kg",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = color
                    )
                }

                TextButton(
                    onClick = onDeleteClick,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
fun WeightCardPreview() {
    PawCareTheme {
        WeightCard(
            weight = Weight(
                id = 1,
                petId = 1,
                peso = 30.4,
                fecha = "17-03-2026",
                notas = "Peso actual"
            ),
            onClick = {},
            onDeleteClick = {},
            pesoAnterior = 30.0
        )
    }
}