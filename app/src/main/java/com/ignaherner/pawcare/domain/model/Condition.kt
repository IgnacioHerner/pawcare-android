package com.ignaherner.pawcare.domain.model

data class Condition(
    val id: Long = 0,
    val firestoreId: String = "",
    val petId: Long,
    val nombre: String,
    val fechaDiagnostico: String?,
    val notas: String?
)