package com.ignaherner.pawcare.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ignaherner.pawcare.domain.model.Especie
import com.ignaherner.pawcare.domain.model.Pet
import com.ignaherner.pawcare.domain.model.Sex
import com.ignaherner.pawcare.ui.theme.PawCareTheme

@Composable
fun PetCard(
    pet: Pet,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pet.nombre,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = pet.especie.displayName,
                    style = MaterialTheme.typography.bodyMedium
                )
                // Con let - idiomatico, una expresion limpia
                pet.peso?.let {
                    Text(
                        text = "$it kg",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar ${pet.nombre}"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PetCardPreviewLight() {
    PawCareTheme {
        PetCard(
            pet = Pet(
                id = 1,
                nombre = "Rex",
                especie = Especie.PERRO,
                fechaNacimiento = "2021-03-15",
                peso = 12.5,
                fotoUri = null,
                raza = "Border Collie",
                sexo = Sex.MACHO
            ),
            onClick = {},
            onDeleteClick = {}
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PetCardPreviewDark() {
    PawCareTheme {
        PetCard(
            pet = Pet(
                id = 1,
                nombre = "Rex",
                especie = Especie.PERRO,
                fechaNacimiento = "2021-03-15",
                peso = 12.5,
                fotoUri = null,
                raza = "Border Collie",
                sexo = Sex.MACHO
            ),
            onClick = {},
            onDeleteClick = {}
        )
    }
}