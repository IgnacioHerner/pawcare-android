package com.ignaherner.mispatitas.presentation.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ignaherner.mispatitas.ui.theme.*


// ═══════════════════════════════════════════════════════════
// CATEGORY ROW
// Fila de categoría con ícono tonal + título + count + hint
// Reemplaza los SeccionCard del PetDetailScreen
// ═══════════════════════════════════════════════════════════

enum class Tone { OK, INFO, WARN, MUTED }

@Composable
fun CategoryRow(
    icon: ImageVector,
    title: String,
    count: Int,
    hint: String,
    color: Color,
    colorSoft: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    PawCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PawSpace.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(PawSpace.md)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(PawRadio.sm))
                    .background(colorSoft),
                contentAlignment = Alignment.Center
            ) {
                PawCareIcon(
                    icon = icon,
                    contentDescription = title,
                    size = PawIconSize.medium,
                    tint = color
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(PawSpace.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (count > 0) {
                        Surface(
                            shape = RoundedCornerShape(PawRadio.xs),
                            color = colorSoft
                        ) {
                            Text(
                                text = count.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = color,
                                modifier = Modifier.padding(
                                    horizontal = 6.dp,
                                    vertical = 2.dp
                                )
                            )
                        }
                    }
                }
                Text(
                    text = hint,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            PawCareIcon(
                icon = Icons.Outlined.ChevronRight,
                contentDescription = null,
                size = PawIconSize.medium,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
