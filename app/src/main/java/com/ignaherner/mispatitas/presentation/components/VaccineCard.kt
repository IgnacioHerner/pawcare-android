package com.ignaherner.mispatitas.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Vaccines
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.ignaherner.mispatitas.domain.model.Vaccine
import com.ignaherner.mispatitas.domain.model.VaccineStatus
import com.ignaherner.mispatitas.ui.theme.Danger
import com.ignaherner.mispatitas.ui.theme.DangerSoft
import com.ignaherner.mispatitas.ui.theme.Info
import com.ignaherner.mispatitas.ui.theme.InfoSoft
import com.ignaherner.mispatitas.utils.toFriendlyDate
import com.ignaherner.mispatitas.ui.theme.PawRadio
import com.ignaherner.mispatitas.ui.theme.PawSpace
import com.ignaherner.mispatitas.ui.theme.Success
import com.ignaherner.mispatitas.ui.theme.SuccessSoft

@Composable
fun VaccineCard(
    vaccine: Vaccine,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val (toneBg, toneFg) = when (vaccine.status) {
        is VaccineStatus.Aplicada -> SuccessSoft to Success
        is VaccineStatus.Programada -> InfoSoft to Info
        is VaccineStatus.Vencida -> DangerSoft to Danger
    }

    PawCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PawSpace.lg, vertical = PawSpace.xs),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PawSpace.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(PawSpace.md)
        ) {
            // Tile ícono
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(PawRadio.sm))
                    .background(toneBg),
                contentAlignment = Alignment.Center
            ) {
                PawCareIcon(
                    icon = Icons.Outlined.Vaccines,
                    contentDescription = null,
                    size = PawIconSize.medium,
                    tint = toneFg
                )
            }

            // Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = vaccine.tipo.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = vaccine.fechaAplicacion.toFriendlyDate(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                vaccine.proximaDosis?.let {
                    Text(
                        text = "Próxima: ${it.toFriendlyDate()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = toneFg
                    )
                }
            }

            // Status pill
            Surface(
                shape = RoundedCornerShape(PawRadio.xs),
                color = toneBg
            ) {
                Text(
                    text = vaccine.status.displayName(),
                    style = MaterialTheme.typography.labelSmall,
                    color = toneFg,
                    modifier = Modifier.padding(horizontal = PawSpace.sm, vertical = 4.dp)
                )
            }
        }
    }
}
