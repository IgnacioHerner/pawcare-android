package com.ignaherner.pawcare.domain.model

data class Pet(
    val id: Long = 0,
    val nombre: String,
    val especie: Especie,
    val raza: String?,
    val sexo: Sex?,
    val fechaNacimiento: String?,
    val peso: Double?,
    val fotoUri: String?,
    val castrado: Boolean = false,
    val fechaCastracion: String? = null,
    val fechaUltimaDesparasitacion : String? = null,
    val proximaDesparasitacion : String? = null
)

enum class Especie(val displayName: String) {
    PERRO("Perro"),
    GATO("Gato"),
    CONEJO("Conejo"),
    AVE("Ave"),
    PEZ("Pez"),
    OTRO("Otro");

    fun emoji(): String = when (this) {
        PERRO -> "🐶"
        GATO -> "🐱"
        CONEJO -> "🐰"
        AVE -> "🐦"
        PEZ -> "🐟"
        OTRO -> "🐾"
    }
}
