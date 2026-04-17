package com.ignaherner.pawcare.domain.model

data class Veterinario(
    val id: String = "",
    val nombre: String,
    val apellido: String,
    val matricula: String,
    val telefono: String,
    val direccionVet: String? = null,
    val ciudadVet: String? = null,
    val especialidad: String? = null
)