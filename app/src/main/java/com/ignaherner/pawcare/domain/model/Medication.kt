package com.ignaherner.pawcare.domain.model

// ═══════════════════════════════════════════════════════════
// MEDICATION — modelo de dominio
// El status se calcula automáticamente desde las fechas
// ═══════════════════════════════════════════════════════════
data class Medication(
    val id: Long = 0,
    val firestoreId: String = "",
    val petId: Long,
    val nombre: String,
    val dosisCantidad: Double,
    val dosisUnidad: DosisUnidad,
    val viaAdministracion: ViaAdministracion,
    val esUnicaDosis: Boolean = false,
    val fechaInicio: String, // dd/MM/yyyy
    val duracionDias: Int, // 0 si es única dosis
    val intervaloHoras: Int, // 0 si es única dosis
    val recetadoPor: String?,
    val notas: String?
) {
    // Dosis legible: "1 comprimido", "2.5 ml"
    val dosisDisplay: String
        get() {
            val cantidad = if (dosisCantidad == dosisCantidad.toInt().toDouble()) {
                dosisCantidad.toInt().toString()
            } else {
                dosisCantidad.toString()
            }
            return "$cantidad ${dosisUnidad.displayName}"
        }

    // Status calculado dinámicamente
    val status: MedicationStatus
        get() = calcularStatus()

    private fun calcularStatus(): MedicationStatus {
        val hoy = java.time.LocalDate.now()
        val formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")

        return try {
            val inicio = java.time.LocalDate.parse(fechaInicio, formatter)

            when {
                // Inicio futuro → PROGRAMADO
                inicio.isAfter(hoy) -> MedicationStatus.PROGRAMADO
                // Única dosis ya aplicada → FINALIZADO
                esUnicaDosis -> MedicationStatus.FINALIZADO
                else -> {
                    val fin = inicio.plusDays(duracionDias.toLong())
                    if (hoy.isAfter(fin) || hoy.isEqual(fin)) {
                        MedicationStatus.FINALIZADO
                    } else {
                        MedicationStatus.ACTIVO
                    }
                }
            }
        } catch (e: Exception) {
            MedicationStatus.ACTIVO
        }
    }
}

enum class MedicationStatus(val displayName: String) {
    PROGRAMADO("Programado"),
    ACTIVO("En curso"),
    FINALIZADO("Finalizado")
}
