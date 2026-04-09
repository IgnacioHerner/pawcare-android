package com.ignaherner.pawcare.data.local.mapper

import com.ignaherner.pawcare.data.local.entity.DewormingEntity
import com.ignaherner.pawcare.domain.model.Deworming

fun DewormingEntity.toDomain(): Deworming = Deworming(
    id = id,
    petId = petId,
    fecha = fecha,
    producto = producto,
    proximaFecha = proximaFecha,
    notas = notas
)

fun Deworming.toEntity(): DewormingEntity = DewormingEntity(
    id = id,
    petId = petId,
    fecha = fecha,
    producto = producto,
    proximaFecha = proximaFecha,
    notas = notas
)