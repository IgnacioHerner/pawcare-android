package com.ignaherner.pawcare.domain.model

data class Deworming(
    val id: Long = 0,
    val firestoreId: String = "",
    val petId: Long,
    val producto: String,
    val tipo: DewormingTipo,
    val fechaAplicacion: String, // dd/MM/yyyy
    val frecuencia: FrecuenciaDeworming,
    val proximaDosis: String?, // auto-calculada
    val veterinario: String?,
    val notas: String?
)