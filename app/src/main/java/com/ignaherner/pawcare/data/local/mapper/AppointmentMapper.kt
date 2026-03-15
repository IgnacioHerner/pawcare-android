package com.ignaherner.pawcare.data.local.mapper

import com.ignaherner.pawcare.data.local.entity.AppointmentEntity
import com.ignaherner.pawcare.domain.model.Appointment
import com.ignaherner.pawcare.domain.model.AppointmentStatus

fun AppointmentEntity.toDomain(): Appointment = Appointment(
    id = id,
    petId = petId,
    fecha = fecha,
    veterinario = veterinario,
    motivo = motivo,
    notas = notas,
    status = when (status) {
        "AGENDADO" -> AppointmentStatus.AGENDADO
        "REALIZADO" -> AppointmentStatus.REALIZADO
        else -> AppointmentStatus.PENDIENTE
    }
)

fun Appointment.toEntity(): AppointmentEntity = AppointmentEntity(
    id = id,
    petId = petId,
    fecha = fecha,
    veterinario = veterinario,
    motivo = motivo,
    notas = notas,
    status = when (status) {
        AppointmentStatus.PENDIENTE -> "PENDIENTE"
        AppointmentStatus.AGENDADO -> "AGENDADO"
        AppointmentStatus.REALIZADO -> "REALIZADO"
    }
)