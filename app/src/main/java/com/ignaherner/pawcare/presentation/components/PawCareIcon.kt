package com.ignaherner.pawcare.presentation.components

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ═══════════════════════════════════════════════════════════
// PAWCARE ICON
// Wrapper de Icon con tamaños consistentes
// Tamaños: small (16), default (20), medium (24), large (28)
// ═══════════════════════════════════════════════════════════

object PawIconSize {
    val small: Dp = 16.dp
    val default: Dp = 20.dp
    val medium: Dp = 24.dp
    val large: Dp = 28.dp
    val xlarge: Dp = 40.dp
}

@Composable
fun PawCareIcon(
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = PawIconSize.default,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        modifier = modifier.size(size),
        tint = tint
    )
}