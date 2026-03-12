package com.ignaherner.pawcare.domain.model

import androidx.compose.ui.graphics.Color

fun VaccineStatus.displayName() : String = when (this) {
    is VaccineStatus.Pendiente -> "Pendiente"
    is VaccineStatus.Programada -> "Programada"
    is VaccineStatus.Aplicada -> "Aplicada"
}

fun VaccineStatus.color(): Color = when (this) {
    is VaccineStatus.Pendiente -> Color(0xFFFF9800)
    is VaccineStatus.Programada -> Color(0xFF2196F3)
    is VaccineStatus.Aplicada -> Color(0xFF4CAF50)
}