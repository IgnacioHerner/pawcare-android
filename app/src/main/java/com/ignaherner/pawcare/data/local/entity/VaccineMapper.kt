package com.ignaherner.pawcare.data.local.entity

import com.ignaherner.pawcare.domain.model.Vaccine
import com.ignaherner.pawcare.domain.model.VaccineStatus

fun VaccineEntity.toDomain(): Vaccine = Vaccine(
    id = id,
    petId = petId,
    nombre = nombre,
    fecha = fecha,
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
    petId = petId,
    nombre = nombre,
    fecha = fecha,
    proximaDosis = proximaDosis,
    veterinario = veterinario,
    notas = notas,
    status = when (status) {
        is VaccineStatus.Pendiente -> "PENDIENTE"
        is VaccineStatus.Programada -> "PROGRAMADA"
        is VaccineStatus.Aplicada -> "APLICADA"
    }
)