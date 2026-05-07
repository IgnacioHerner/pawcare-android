package com.ignaherner.pawcare.presentation.auth

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
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material.icons.outlined.Shield
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ignaherner.pawcare.presentation.components.PawCareIcon
import com.ignaherner.pawcare.presentation.components.PawIconSize
import com.ignaherner.pawcare.ui.theme.PawRadio
import com.ignaherner.pawcare.ui.theme.PawSpace
import com.ignaherner.pawcare.ui.theme.VetPrimary
import com.ignaherner.pawcare.ui.theme.VetPrimaryInk
import com.ignaherner.pawcare.ui.theme.VetPrimarySoft

@Composable
fun VetWelcomeScreen(
    nombreVet: String,
    onStart: () -> Unit
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
                    .background(VetPrimarySoft),
                contentAlignment = Alignment.Center
            ) {
                PawCareIcon(
                    icon = Icons.Outlined.LocalHospital,
                    contentDescription = null,
                    size = PawIconSize.xlarge,
                    tint = VetPrimary
                )
            }

            Spacer(modifier = Modifier.height(PawSpace.xl))

            Text(
                text = "¡Bienvenido, Dr. ${nombreVet.ifBlank { "Veterinario" }}!",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(PawSpace.md))

            Text(
                text = "Tu cuenta profesional está creada. Así funciona PawCare para vos:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(PawSpace.xxl))

            Column(verticalArrangement = Arrangement.spacedBy(PawSpace.sm)) {
                VetWelcomeRow(
                    icon = Icons.Outlined.QrCode2,
                    text = "Buscá mascotas por código QR o ID"
                )
                VetWelcomeRow(
                    icon = Icons.Outlined.Vaccines,
                    text = "Registrá vacunas, medicamentos y controles"
                )
                VetWelcomeRow(
                    icon = Icons.Outlined.Shield,
                    text = "Todo queda vinculado a tu matrícula"
                )
                VetWelcomeRow(
                    icon = Icons.Outlined.People,
                    text = "Los dueños ven tus registros en tiempo real"
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(PawRadio.md),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VetPrimary,
                    contentColor = Color.White
                )
            ) {
                PawCareIcon(
                    icon = Icons.Outlined.ArrowForward,
                    contentDescription = null,
                    size = PawIconSize.default,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(PawSpace.sm))
                Text(
                    text = "Empezar a trabajar",
                    style = MaterialTheme.typography.titleSmall
                )
            }

            Spacer(modifier = Modifier.height(PawSpace.xxl))
        }
    }
}

@Composable
private fun VetWelcomeRow(
    icon: ImageVector,
    text: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(PawRadio.md),
        color = VetPrimarySoft
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
                tint = VetPrimary
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = VetPrimaryInk
            )
        }
    }
}