package com.ignaherner.mispatitas.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ignaherner.mispatitas.ui.theme.PawRadio
import com.ignaherner.mispatitas.ui.theme.PawSpace

// ═══════════════════════════════════════════════════════════
// SHIMMER BRUSH
// Gradiente animado que crea el efecto de "carga"
// Se mueve de izquierda a derecha repetidamente
// ═══════════════════════════════════════════════════════════
@Composable
fun shimmerBrush(show: Boolean = true, targetValue: Float = 1000f): Brush {
    return if (show) {
        val shimmerColors = listOf(
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
        )

        val transition = rememberInfiniteTransition(label = "shimmer")
        val translateAnimation = transition.animateFloat(
            initialValue = 0f,
            targetValue = targetValue,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 1200,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "shimmer"
        )

        Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(translateAnimation.value - 200f, 0f),
            end = Offset(translateAnimation.value, 0f)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color.Transparent, Color.Transparent),
            start = Offset.Zero,
            end = Offset.Zero
        )
    }
}

@Composable
fun PetCardSkeleton() {
    val brush = shimmerBrush()

    PawCard(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = PawSpace.lg, vertical = PawSpace.xs)
    ) {
        Column(
            modifier = Modifier.padding(PawSpace.lg),
            verticalArrangement = Arrangement.spacedBy(PawSpace.md)
        ) {
            // Header
            Row(
                horizontalArrangement = Arrangement.spacedBy(PawSpace.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(brush)
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(PawSpace.sm)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .height(18.dp)
                            .clip(RoundedCornerShape(PawRadio.xs))
                            .background(brush)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(14.dp)
                            .clip(RoundedCornerShape(PawRadio.xs))
                            .background(brush)
                    )
                }
            }

            // Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )

            // Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(3) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(PawSpace.xs)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(20.dp)
                                .clip(RoundedCornerShape(PawRadio.xs))
                                .background(brush)
                        )
                        Box(
                            modifier = Modifier
                                .width(56.dp)
                                .height(12.dp)
                                .clip(RoundedCornerShape(PawRadio.xs))
                                .background(brush)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreenSkeleton() {
    val brush = shimmerBrush()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = PawSpace.md),
        verticalArrangement = Arrangement.spacedBy(PawSpace.md)
    ) {
        // Saludo
        Column(
            modifier = Modifier.padding(horizontal = PawSpace.lg),
            verticalArrangement = Arrangement.spacedBy(PawSpace.sm)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.45f)
                    .height(32.dp)
                    .clip(RoundedCornerShape(PawRadio.xs))
                    .background(brush)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.65f)
                    .height(18.dp)
                    .clip(RoundedCornerShape(PawRadio.xs))
                    .background(brush)
            )
        }

        Spacer(modifier = Modifier.height(PawSpace.sm))

        // Header "Mascotas"
        Box(
            modifier = Modifier
                .padding(horizontal = PawSpace.lg)
                .fillMaxWidth(0.3f)
                .height(22.dp)
                .clip(RoundedCornerShape(PawRadio.xs))
                .background(brush)
        )

        // Cards
        repeat(2) {
            PetCardSkeleton()
        }
    }
}
