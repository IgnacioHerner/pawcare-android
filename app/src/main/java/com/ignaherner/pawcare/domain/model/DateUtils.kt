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

fun calcularProximaDosis(fecha: String) : String =
    fecha.toLocalDate().plusYears(1).toFormattedString()

fun fechaHoy(): String = LocalDate.now().toFormattedString()

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

fun calcularDiaActual(fechaInicio: String, duracionDias: Int): String {
    val inicio = fechaInicio.toLocalDate()
    val hoy = LocalDate.now()
    val diasPasados = ChronoUnit.DAYS.between(inicio, hoy).toInt() + 1
    val diaActual = diasPasados.coerceIn(1, duracionDias)
    return "Dia $diaActual de $duracionDias"
}