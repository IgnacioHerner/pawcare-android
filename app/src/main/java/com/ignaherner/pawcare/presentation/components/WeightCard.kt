package com.ignaherner.pawcare.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ignaherner.pawcare.domain.model.Weight
import com.ignaherner.pawcare.ui.theme.PawCareTheme

@Composable
fun WeightCard(
    weight: Weight,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
){
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Peso: ${weight.peso}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = weight.fecha,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                weight.notas?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar turno"
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
fun WeightCardPreview() {
    PawCareTheme {
        WeightCard(
            weight = Weight (
                id = 1,
                petId = 1,
                peso = 30.4,
                fecha = "17-03-2026",
                notas = "Peso actual"
            ),
            onClick = {},
            onDeleteClick = {}
        )
    }
}