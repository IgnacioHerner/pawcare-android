package com.ignaherner.pawcare.domain.model

// ═══════════════════════════════════════════════════════════
// SEVERIDAD DE LA CONDICIÓN
// ═══════════════════════════════════════════════════════════
enum class Severidad(val displayName: String) {
    LEVE("Leve"),
    MODERADA("Moderada"),
    GRAVE("Grave")
}

// ═══════════════════════════════════════════════════════════
// ESTADO DE LA CONDICIÓN
// ═══════════════════════════════════════════════════════════
enum class ConditionEstado(val displayName: String) {
    ACTIVA("Activa"),
    EN_TRATAMIENTO("En tratamiento"),
    RESUELTA("Resuelta")
}
