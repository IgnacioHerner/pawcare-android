 package com.ignaherner.mispatitas.presentation.auth

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material.icons.outlined.Vaccines
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ignaherner.mispatitas.presentation.components.PawCareIcon
import com.ignaherner.mispatitas.presentation.components.PawIconSize
import com.ignaherner.mispatitas.ui.theme.PawRadio
import com.ignaherner.mispatitas.ui.theme.PawSpace

@Composable
fun WelcomeScreen(
    nombreUsuario: String,
    onNavigateToAddPet: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = PawSpace.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Checkmark
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(PawRadio.lg))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                PawCareIcon(
                    icon = Icons.Outlined.Check,
                    contentDescription = null,
                    size = PawIconSize.xlarge,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(PawSpace.xl))

            // Título personalizado
            Text(
                text = "¡Listo, ${nombreUsuario.ifBlank { "usuario" }}!",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(PawSpace.md))

            // Subtítulo
            Text(
                text = "Tu cuenta está creada. Ahora agregá la primera mascota para empezar su libreta.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(PawSpace.xxl))

            // Info rows
            Column(verticalArrangement = Arrangement.spacedBy(PawSpace.sm)) {
                WelcomeInfoRow(
                    icon = Icons.Outlined.Pets,
                    text = "Registrá cada una de tus mascotas"
                )
                WelcomeInfoRow(
                    icon = Icons.Outlined.Vaccines,
                    text = "Cargá vacunas, medicamentos y controles"
                )
                WelcomeInfoRow(
                    icon = Icons.Outlined.QrCode2,
                    text = "Compartí el QR en cualquier consulta"
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botón
            Button(
                onClick = onNavigateToAddPet,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(PawRadio.md),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                PawCareIcon(
                    icon = Icons.Outlined.Add,
                    contentDescription = null,
                    size = PawIconSize.default,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(PawSpace.sm))
                Text(
                    text = "Agregar mi primera mascota",
                    style = MaterialTheme.typography.titleSmall
                )
            }

            Spacer(modifier = Modifier.height(PawSpace.xxl))
        }
    }
}

@Composable
private fun WelcomeInfoRow(
    icon: ImageVector,
    text: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(PawRadio.md),
        color = MaterialTheme.colorScheme.surface
    ) {
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
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
