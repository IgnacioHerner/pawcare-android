package com.ignaherner.pawcare.data.local.mapper

import com.ignaherner.pawcare.data.local.entity.WeightEntity
import com.ignaherner.pawcare.domain.model.Weight

fun WeightEntity.toDomain(): Weight = Weight(
    id = id,
    petId = petId,
    peso = peso,
    fecha = fecha,
    notas = notas
)
fun Weight.toEntity(): WeightEntity = WeightEntity(
    id = id,
    petId = petId,
    peso = peso,
    fecha = fecha,
    notas = notas
)