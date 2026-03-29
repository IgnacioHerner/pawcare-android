package com.ignaherner.pawcare.domain.model

data class Owner(
    val id: Long = 0,
    val nombre: String,
    val apellido: String,
    val telefono: String,
    val email: String?,
    val ciudad: String,
    val direccion: String?
)
