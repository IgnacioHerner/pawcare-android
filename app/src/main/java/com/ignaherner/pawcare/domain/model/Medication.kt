package com.ignaherner.pawcare.domain.model

data class Medication(
    val id: Long,
    val petId: Long,
    val nombre: String,
    val fechaInicio: String,
    val duracionDias: Int,
    val intervaloHoras: Int,
    val recetadoPor: String?,
    val dosis: String,
    val esUnicaDosis: Boolean = false,
    val notas: String?,
    val status: MedicationStatus
)

enum class MedicationStatus(val displayName: String) {
    ACTIVO("Activo"),
    FINALIZADO("Finalizado")
}
