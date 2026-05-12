package com.ignaherner.mispatitas.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ignaherner.mispatitas.ui.theme.*

// ═══════════════════════════════════════════════════════════
// ALERT BANNER
// Aviso accionable con ícono + título + descripción + chevron
// Tones: INFO (azul), WARN (amarillo), DANGER (rojo)
// ═══════════════════════════════════════════════════════════

enum class AlertTone { INFO, WARN, DANGER }

@Composable
fun AlertBanner(
    icon: ImageVector,
    title: String,
    body: String,
    tone: AlertTone = AlertTone.WARN,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val (toneBg, toneFg) = when (tone) {
        AlertTone.INFO -> InfoSoft to Info
        AlertTone.WARN -> WarnSoft to Warn
        AlertTone.DANGER -> DangerSoft to Danger
    }

    val baseModifier = modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(PawRadio.md))
        .background(toneBg)

    val finalModifier = if (onClick != null) {
        baseModifier.clickable(onClick = onClick)
    } else {
        baseModifier
    }

    Row(
        modifier = finalModifier.padding(PawSpace.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PawSpace.md)
    ) {
        // Ícono tonal
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(PawRadio.sm))
                .background(toneFg.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            PawCareIcon(
                icon = icon,
                contentDescription = null,
                size = PawIconSize.medium,
                tint = toneFg
            )
        }

        // Título + descripción
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Chevron solo si es clickeable
        if (onClick != null) {
            PawCareIcon(
                icon = Icons.Outlined.ChevronRight,
                contentDescription = null,
                size = PawIconSize.medium,
                tint = toneFg
            )
        }
    }
}
