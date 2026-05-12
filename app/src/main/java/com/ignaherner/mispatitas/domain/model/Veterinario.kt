package com.ignaherner.mispatitas.domain.model

data class Veterinario(
    val id: String = "",
    val nombre: String,
    val apellido: String,
    val matricula: String,
    val especialidad: String? = null,
    val telefono: String = "",
    val clinica: String? = null,
    val direccion: String? = null,
    val ciudad: String? = null,
    val fotoUri: String? = null
)
