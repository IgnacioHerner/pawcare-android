package com.ignaherner.pawcare.domain.model

data class Vaccine(
    val id: Long = 0,
    val petId: Long,
    val nombre: String,
    val fecha: String?,
    val esAnual: Boolean = false,
    val proximaDosis: String?,
    val veterinario: String?,
    val notas: String?,
    val status: VaccineStatus
)

sealed class VaccineStatus {
    object Pendiente: VaccineStatus()
    data class Programada(val fechaProgramada: String) : VaccineStatus()
    data class Aplicada(val fechaAplicacion: String) : VaccineStatus()
}
