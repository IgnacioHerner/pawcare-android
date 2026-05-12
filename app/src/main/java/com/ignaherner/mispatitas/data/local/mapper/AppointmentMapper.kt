package com.ignaherner.mispatitas.data.local.mapper

import com.ignaherner.mispatitas.data.local.entity.AppointmentEntity
import com.ignaherner.mispatitas.domain.model.Appointment

fun AppointmentEntity.toDomain(): Appointment = Appointment(
    id = id,
    firestoreId = firestoreId,
    petId = petId,
    fecha = fecha,
    motivo = motivo,
    veterinario = veterinario,
    clinica = clinica,
    diagnostico = diagnostico,
    notas = notas
)

fun Appointment.toEntity(): AppointmentEntity = AppointmentEntity(
    id = id,
    firestoreId = firestoreId,
    petId = petId,
    fecha = fecha,
    motivo = motivo,
    veterinario = veterinario,
    clinica = clinica,
    diagnostico = diagnostico,
    notas = notas
)
