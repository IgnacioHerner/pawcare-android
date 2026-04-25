package com.ignaherner.pawcare.presentation.components

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.ignaherner.pawcare.ui.theme.PawRadii
import com.ignaherner.pawcare.ui.theme.PawSpace

// ═══════════════════════════════════════════════════════════
// SHIMMER BRUSH
// Gradiente animado que crea el efecto de "carga"
// Se mueve de izquierda a derecha repetidamente
// ═══════════════════════════════════════════════════════════
@Composable
fun shimmerBrush(targetValue: Float = 1000f): Brush {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = targetValue,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnimation.value, y = translateAnimation.value)
    )
}

// ═══════════════════════════════════════════════════════════
// PET CARD SKELETON
// Esqueleto de la PetSummaryCard mientras carga
// ═══════════════════════════════════════════════════════════
@Composable
fun PetCardSkeleton() {
    val brush = shimmerBrush()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PawSpace.lg, vertical = PawSpace.xs)
            .clip(RoundedCornerShape(PawRadii.md))
            .background(MaterialTheme.colorScheme.surface)
            .padding(PawSpace.lg)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(PawSpace.md)) {
            // Header skeleton
            Row(
                horizontalArrangement = Arrangement.spacedBy(PawSpace.md)
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
                            .fillMaxWidth(0.4f)
                            .height(16.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(brush)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(12.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(brush)
                    )
                }
            }
            // Stats skeleton
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(28.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(brush)
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// HOME SCREEN SKELETON
// Lista de placeholders mientras carga el HomeScreen
// ═══════════════════════════════════════════════════════════
@Composable
fun HomeScreenSkeleton() {
    LazyColumn(
        contentPadding = PaddingValues(vertical = PawSpace.md),
        verticalArrangement = Arrangement.spacedBy(PawSpace.sm)
    ) {
        // Saludo skeleton
        item {
            Column(
                modifier = Modifier.padding(horizontal = PawSpace.lg),
                verticalArrangement = Arrangement.spacedBy(PawSpace.sm)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(28.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(shimmerBrush())
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(shimmerBrush())
                )
            }
        }
        // Pet cards skeleton
        items(2) {
            PetCardSkeleton()
        }
    }
}