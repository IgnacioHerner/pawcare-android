package com.ignaherner.pawcare.domain.model

data class Appointment (
    val id: Long = 0,
    val petId: Long,
    val fecha: String,
    val veterinario: String?,
    val motivo: String?,
    val notas: String?,
    val status: AppointmentStatus
)

enum class AppointmentStatus(val displayName: String) {
    PENDIENTE("Pendiente"),
    AGENDADO("Agendado"),
    REALIZADO("Realizado")
}
