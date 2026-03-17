package com.ignaherner.pawcare.domain.model

data class Weight(
    val id: Long = 0,
    val petId: Long,
    val peso: Double,
    val fecha: String,
    val notas: String?
)
