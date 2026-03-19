package com.ignaherner.pawcare.domain.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter

val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

fun LocalDate.toFormattedString(): String = format(dateFormatter)

fun String.toLocalDate(): LocalDate = LocalDate.parse(this, dateFormatter)

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