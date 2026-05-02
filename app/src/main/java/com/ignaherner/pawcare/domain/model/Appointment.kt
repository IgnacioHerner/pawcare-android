package com.ignaherner.pawcare.domain.model

data class Appointment (
    val id: Long = 0,
    val firestoreId: String = "",
    val petId: Long,
    val fecha: String,
    val motivo: String,
    val veterinario: String?,
    val clinica: String?,
    val diagnostico: String?,
    val notas: String?
)
