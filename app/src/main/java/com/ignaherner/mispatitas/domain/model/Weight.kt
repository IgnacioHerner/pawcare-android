package com.ignaherner.mispatitas.domain.model

data class Weight(
    val id: Long = 0,
    val firestoreId: String = "",
    val petId: Long,
    val peso: Double,
    val fecha: String,
    val notas: String?
)
