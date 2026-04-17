package com.ignaherner.pawcare.domain.model

data class Deworming(
    val id: Long = 0,
    val firestoreId: String = "",
    val petId: Long,
    val fecha: String,
    val producto: String?,
    val proximaFecha: String?,
    val notas: String?
)
