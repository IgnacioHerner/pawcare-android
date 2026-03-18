package com.ignaherner.pawcare.domain.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter

val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

fun LocalDate.toFormattedString(): String = format(dateFormatter)

fun String.toLocalDate(): LocalDate = LocalDate.parse(this, dateFormatter)

fun calcularProximaDosis(fecha: String) : String =
    fecha.toLocalDate().plusYears(1).toFormattedString()

fun fechaHoy(): String = LocalDate.now().toFormattedString()