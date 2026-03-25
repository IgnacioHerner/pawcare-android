package com.ignaherner.pawcare.domain.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
private val dateFormatterAlt: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

fun LocalDate.toFormattedString(): String = format(dateFormatter)

fun String.toLocalDate(): LocalDate = try {
    LocalDate.parse(this, dateFormatter)
} catch (e: Exception) {
    try {
        LocalDate.parse(this, dateFormatterAlt)
    } catch (e2: Exception) {
        LocalDate.now() // fallback — si falla todo, usá hoy
    }
}
// Funcion para calcular la proxima dosis + un año
fun calcularProximaDosis(fecha: String) : String =
    fecha.toLocalDate().plusYears(1).toFormattedString()

// Funcion para la fecha de hoy
fun fechaHoy(): String = LocalDate.now().toFormattedString()

// Funcion para hacer mas linda la fecha
fun String.toFriendlyDate(): String {
    return try {
        val date = this.toLocalDate()
        val formatter = DateTimeFormatter.ofPattern(
            "dd MMM yyyy",
            java.util.Locale("es", "AR")
        )
        date.format(formatter)
    } catch (e: Exception) {
        this // si falla, devuelve el string original
    }
}

// fechaInicio + duracionDias
fun calcularFechaFin(fechaInicio: String, duracionDias: Int): String =
    fechaInicio.toLocalDate().plusDays(duracionDias.toLong()).toFormattedString()

// Funcion para calcular el dia actual de la dosis
fun calcularDiaActual(fechaInicio: String, duracionDias: Int): String {
    val inicio = fechaInicio.toLocalDate()
    val hoy = LocalDate.now()
    val diasPasados = ChronoUnit.DAYS.between(inicio, hoy).toInt() + 1
    val diaActual = diasPasados.coerceIn(1, duracionDias)
    return "Dia $diaActual de $duracionDias"
}

// Funcion para la WeightMetrics
data class WeightMetrics(
    val ultimoPeso: Double,
    val cambio30Dias: Double?, // null si no hay datos de hace 30 días
    val promedio: Double,
    val tendencia: String // "📈 En aumento", "📉 En descenso", etc.
)

// Funcion para calcular metricas y sacar promedio, la tendencia etc
fun calcularMetricas (registros: List<Weight>): WeightMetrics? {
    if (registros.isEmpty()) return null

    val ultimoPeso = registros.first().peso

    val hoy = LocalDate.now()
    val hace30Dias = hoy.minusDays(30)
    val pesoHace30Dias = registros
        .filter { it.fecha.toLocalDate().isBefore(hace30Dias) }
        .firstOrNull()?.peso
    val cambio = pesoHace30Dias?.let { ultimoPeso - it }

    val promedio = registros.map {it.peso}.average()

    val tendencia = when {
        cambio == null -> "➡️ Sin datos suficientes"
        cambio > 0.5 -> "📈 En aumento"
        cambio < -0.5 -> "📉 En descenso"
        else -> "➡️ Peso estable"
    }

    return WeightMetrics(ultimoPeso, cambio, promedio, tendencia)
}

// Calcualar cuántos días faltan desde hoy hasta "15/03/2027" ejmplo
fun diasHastaFecha(fecha: String): Long {
    val fechaDate = fecha.toLocalDate()
    val hoy = LocalDate.now()
    return ChronoUnit.DAYS.between(hoy, fechaDate)
}