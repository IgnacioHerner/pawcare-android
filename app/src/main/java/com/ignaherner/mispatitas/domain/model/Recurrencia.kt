package com.ignaherner.mispatitas.domain.model

// Pieza 1: Frecuencia base — conjunto cerrado, enum
enum class FrecuenciaBase(val displayName: String) {
    DIA("Día"),
    SEMANA("Semana"),
    MES("Mes"),
    ANIO("Año")
}

// Pieza 2: Condición de fin — variantes con datos distintos, sealed class
sealed class FinRecurrencia {
    object Nunca : FinRecurrencia()
    data class EnFecha(val fecha: String) : FinRecurrencia()
    data class DespuesDe(val ocurrencias: Int) : FinRecurrencia()
}

// Las 3 piezas juntas: data class
data class Recurrencia(
    val frecuencia: FrecuenciaBase,
    val intervalo: Int,              // "cada X [frecuencia]"
    val fin: FinRecurrencia
)

sealed class TipoRecurrencia {
    object NoSeRepite : TipoRecurrencia()
    data class Recurrente(val recurrencia: Recurrencia) : TipoRecurrencia()
}
