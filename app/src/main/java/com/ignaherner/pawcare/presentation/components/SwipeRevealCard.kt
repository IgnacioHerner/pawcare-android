package com.ignaherner.pawcare.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SwipeRevealCard(
    onDelete: () -> Unit,
    onEdit: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when(value) {
                SwipeToDismissBoxValue.EndToStart -> {
                    onDelete()
                    false // esperamos confirmacion del AlertDialog
                }
                SwipeToDismissBoxValue.StartToEnd -> {
                    onEdit?.invoke()
                    false
                }
                else -> false
            }
        }
    )

    val backgroundColor by animateColorAsState(
        targetValue = when (dismissState.targetValue) {
            SwipeToDismissBoxValue.EndToStart -> Color(0xFFF44336)
            SwipeToDismissBoxValue.StartToEnd -> Color(0xFF2196F3)
            else -> Color.Transparent
        },
        label = "swipe_color"
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .padding(horizontal = 24.dp)
            ) {
                // Icono izquierda - editar
                if (dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = Color.White,
                        modifier = Modifier
                            .size(28.dp)
                            .align(Alignment.CenterStart)
                    )
                }
                // Icono derecha - Eliminar
                if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = Color.White,
                        modifier = Modifier
                            .size(28.dp)
                            .align(Alignment.CenterEnd)
                    )
                }
            }
        },
        enableDismissFromStartToEnd = onEdit != null // solo si tiene que editar
    ) {
        content()
    }
}