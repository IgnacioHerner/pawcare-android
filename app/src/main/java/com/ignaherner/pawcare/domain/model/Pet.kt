package com.ignaherner.pawcare.domain.model

data class Pet(
    val id: Long = 0,
    val nombre: String,
    val especie: Especie,
    val fechaNacimiento: String?,
    val peso: Double?,
    val fotoUri: String?
)

enum class Especie(val displayName: String) {
    PERRO("Perro"),
    GATO("Gato"),
    CONEJO("Conejo"),
    AVE("Ave"),
    PEZ("Pez"),
    OTRO("Otro")
}
