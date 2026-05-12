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
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material.icons.outlined.Sync
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ignaherner.mispatitas.presentation.components.PawCareIcon
import com.ignaherner.mispatitas.presentation.components.PawIconSize
import com.ignaherner.mispatitas.ui.theme.PawRadio
import com.ignaherner.mispatitas.ui.theme.PawSpace

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit
) {
    val pages = listOf(
        OnboardingPage(
            icon = Icons.Outlined.Pets,
            title = "La salud de tu\nmascota, organizada",
            body = "Vacunas, medicamentos, peso, controles — todo el historial en un solo lugar, siempre accesible."
        ),
        OnboardingPage(
            icon = Icons.Outlined.QrCode2,
            title = "Un código único\npara cada mascota",
            body = "Compartí el código con tu veterinario y accedé al historial completo al instante. Sin papeles, sin trámites."
        ),
        OnboardingPage(
            icon = Icons.Outlined.Sync,
            title = "Dueños y veterinarios\nconectados",
            body = "Lo que registra el veterinario, el dueño lo ve en tiempo real. Toda la información sincronizada."
        ),
        OnboardingPage(
            icon = Icons.Outlined.NotificationsActive,
            title = "Recordatorios\ninteligentes",
            body = "Nunca más te olvides de una vacuna o desparasitación. PawCare te avisa cuando sea necesario."
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
                        .clip(RoundedCornerShape(PawRadio.xl))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    PawCareIcon(
                        icon = pages[currentPage].icon,
                        contentDescription = null,
                        size = 48.dp,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
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
                                        MaterialTheme.colorScheme.primary
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
                    shape = RoundedCornerShape(PawRadio.md),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
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
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Spacer(modifier = Modifier.height(PawSpace.xxl))
            }
        }
    }
}

private data class OnboardingPage(
    val icon: ImageVector,
    val title: String,
    val body: String
)
