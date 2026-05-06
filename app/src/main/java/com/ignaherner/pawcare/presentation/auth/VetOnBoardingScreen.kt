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
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ignaherner.pawcare.presentation.components.PawCareIcon
import com.ignaherner.pawcare.presentation.components.PawIconSize
import com.ignaherner.pawcare.ui.theme.PawRadii
import com.ignaherner.pawcare.ui.theme.PawSpace
import com.ignaherner.pawcare.ui.theme.VetPrimary
import com.ignaherner.pawcare.ui.theme.VetPrimarySoft

@Composable
fun VetOnboardingScreen(
    onFinished: () -> Unit
) {
    val pages = listOf(
        VetOnboardingPage(
            icon = Icons.Outlined.QrCode2,
            title = "Escaneá el código\nde cualquier mascota",
            body = "Cada mascota tiene un código único. Escanealo o ingresalo manualmente para acceder a su historial completo."
        ),
        VetOnboardingPage(
            icon = Icons.Outlined.MedicalServices,
            title = "Registrá todo\nen un solo lugar",
            body = "Vacunas, medicamentos, condiciones, desparasitación y visitas. Todo queda vinculado a tu matrícula profesional."
        ),
        VetOnboardingPage(
            icon = Icons.Outlined.People,
            title = "Conectá con\nlos dueños",
            body = "Los dueños pueden ver los registros que cargues en tiempo real. Sin papeles, sin trámites."
        )
    )

    var currentPage by remember { mutableIntStateOf(0) }
    val isLastPage = currentPage == pages.size - 1

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Botón "Saltar"
            TextButton(
                onClick = onFinished,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(PawSpace.lg)
            ) {
                Text(
                    text = "Saltar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = PawSpace.xxl),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.weight(1f))

                // Tile con ícono
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(PawRadii.xl))
                        .background(VetPrimarySoft),
                    contentAlignment = Alignment.Center
                ) {
                    PawCareIcon(
                        icon = pages[currentPage].icon,
                        contentDescription = null,
                        size = 48.dp,
                        tint = VetPrimary
                    )
                }

                Spacer(modifier = Modifier.height(PawSpace.xxl))

                // Título
                Text(
                    text = pages[currentPage].title,
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(PawSpace.lg))

                // Body
                Text(
                    text = pages[currentPage].body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.weight(1f))

                // Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(PawSpace.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    pages.forEachIndexed { index, _ ->
                        Box(
                            modifier = Modifier
                                .size(
                                    width = if (index == currentPage) 24.dp else 8.dp,
                                    height = 8.dp
                                )
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (index == currentPage)
                                        VetPrimary
                                    else
                                        MaterialTheme.colorScheme.outlineVariant
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(PawSpace.xxl))

                // Botón
                Button(
                    onClick = {
                        if (isLastPage) {
                            onFinished()
                        } else {
                            currentPage++
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(PawRadii.md),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VetPrimary,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = if (isLastPage) "Empezar" else "Siguiente",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.width(PawSpace.sm))
                    PawCareIcon(
                        icon = if (isLastPage) Icons.Outlined.Check else Icons.Outlined.ArrowForward,
                        contentDescription = null,
                        size = PawIconSize.default,
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(PawSpace.xxl))
            }
        }
    }
}

private data class VetOnboardingPage(
    val icon: ImageVector,
    val title: String,
    val body: String
)