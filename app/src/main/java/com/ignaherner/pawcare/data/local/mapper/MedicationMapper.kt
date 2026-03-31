package com.ignaherner.pawcare.data.local.mapper

import com.ignaherner.pawcare.data.local.entity.MedicationEntity
import com.ignaherner.pawcare.domain.model.Medication
import com.ignaherner.pawcare.domain.model.MedicationStatus

fun MedicationEntity.toDomain(): Medication = Medication(
    id = id,
    petId = petId,
    nombre = nombre,
    fechaInicio = fechaInicio,
    duracionDias = duracionDias,
    intervaloHoras = intervaloHoras,
    recetadoPor = recetadoPor,
    dosis = dosis,
    esUnicaDosis = esUnicaDosis == 1,
    notas = notas,
    status = when(status) {
        "ACTIVO" -> MedicationStatus.ACTIVO
        else -> MedicationStatus.FINALIZADO
    }
)

fun Medication.toEntity(): MedicationEntity = MedicationEntity(
    id = id,
    petId = petId,
    nombre = nombre,
    fechaInicio = fechaInicio,
    duracionDias = duracionDias,
    intervaloHoras = intervaloHoras,
    recetadoPor = recetadoPor,
    dosis = dosis,
    esUnicaDosis = if (esUnicaDosis) 1 else 0,
    notas = notas,
    status = when (status) {
        MedicationStatus.ACTIVO -> "ACTIVO"
        MedicationStatus.FINALIZADO -> "FINALIZADO"
    }
)