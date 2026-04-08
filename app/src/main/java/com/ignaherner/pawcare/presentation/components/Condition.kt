package com.ignaherner.pawcare.presentation.components

data class Condition(
    val id: Long = 0,
    val petId: Long,
    val nombre: String,
    val fechaDiagnostico: String?,
    val notas: String?
)
