package com.ignaherner.mispatitas.data.local.mapper

import com.ignaherner.mispatitas.data.local.entity.WeightEntity
import com.ignaherner.mispatitas.domain.model.Weight

fun WeightEntity.toDomain(): Weight = Weight(
    id = id,
    firestoreId = firestoreId,
    petId = petId,
    peso = peso,
    fecha = fecha,
    notas = notas
)
fun Weight.toEntity(): WeightEntity = WeightEntity(
    id = id,
    firestoreId = firestoreId,
    petId = petId,
    peso = peso,
    fecha = fecha,
    notas = notas
)
