package com.ignaherner.pawcare.data.local.mapper

import com.ignaherner.pawcare.data.local.entity.VaccineEntity
import com.ignaherner.pawcare.domain.model.Vaccine
import com.ignaherner.pawcare.domain.model.VaccineStatus

fun VaccineEntity.toDomain(): Vaccine = Vaccine(
    id = id,
    firestoreId = firestoreId,
    petId = petId,
    nombre = nombre,
    fecha = fecha,
    esAnual = esAnual == 1,
    proximaDosis = proximaDosis,
    veterinario = veterinario,
    notas = notas,
    status = when (status) {
        "PROGRAMADA" -> VaccineStatus.Programada(fecha ?: "")
        "APLICADA" -> VaccineStatus.Aplicada(fecha ?: "")
        else -> VaccineStatus.Pendiente
    }
)

fun Vaccine.toEntity(): VaccineEntity = VaccineEntity(
    id = id,
    firestoreId = firestoreId,
    petId = petId,
    nombre = nombre,
    fecha = fecha,
    esAnual = if (esAnual) 1 else 0,
    proximaDosis = proximaDosis,
    veterinario = veterinario,
    notas = notas,
    status = when (status) {
        is VaccineStatus.Pendiente -> "PENDIENTE"
        is VaccineStatus.Programada -> "PROGRAMADA"
        is VaccineStatus.Aplicada -> "APLICADA"
    }
)

fun VaccineStatus.toFirestoreString(): String = when (this) {
    is VaccineStatus.Aplicada -> "Aplicada"
    is VaccineStatus.Pendiente -> "Pendiente"
    is VaccineStatus.Programada -> "Programada"
}

fun String.toVaccineStatus(): VaccineStatus = when (this) {
    "Aplicada" -> VaccineStatus.Aplicada(fechaAplicacion = "")
    "Programada" -> VaccineStatus.Programada(fechaProgramada = "")
    else -> VaccineStatus.Pendiente
}