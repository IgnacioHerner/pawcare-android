package com.ignaherner.mispatitas.domain.model

// ═══════════════════════════════════════════════════════════
// VACCINE — modelo de dominio
// El estado se calcula automáticamente desde fecha + frecuencia
// ═══════════════════════════════════════════════════════════
data class Vaccine(
    val id: Long = 0,
    val firestoreId: String = "",
    val petId: Long,
    val tipo: TipoVacuna,
    val nombreComercial: String?, // "Nobivac DHPPi" - opcional
    val fechaAplicacion: String, // dd/MM/yyyy
    val frecuencia: FrecuenciaVacuna,
    val proximaDosis: String?, // calculado automáticamente
    val veterinario: String?,
    val notas: String?
) {
    // Estado calculado dinámicamente
    val status: VaccineStatus
        get() = calcularStatus()

    private fun calcularStatus(): VaccineStatus {
        val hoy = java.time.LocalDate.now()
        val formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")

        return try {
            val fechaApp = java.time.LocalDate.parse(fechaAplicacion, formatter)

            when {
                // Aplicación futura → PROGRAMADA
                fechaApp.isAfter(hoy) -> VaccineStatus.Programada
                // Aplicada pero sin próxima dosis (única) → APLICADA
                proximaDosis == null -> VaccineStatus.Aplicada
                else -> {
                    val proxima = java.time.LocalDate.parse(proximaDosis, formatter)
                    when {
                        proxima.isBefore(hoy) -> VaccineStatus.Vencida
                        else -> VaccineStatus.Aplicada
                    }
                }
            }
        } catch (e: Exception) {
            VaccineStatus.Aplicada
        }
    }
}

sealed class VaccineStatus {
    object Programada : VaccineStatus()
    object Aplicada : VaccineStatus()
    object Vencida : VaccineStatus()

    fun displayName(): String = when (this) {
        is Programada -> "Programada"
        is Aplicada -> "Aplicada"
        is Vencida -> "Vencida"
    }
}
