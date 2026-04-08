package com.ignaherner.pawcare.data.local.mapper

import com.ignaherner.pawcare.data.local.entity.ConditionEntity
import com.ignaherner.pawcare.presentation.components.Condition


fun ConditionEntity.toDomain(): Condition = Condition(
    id = id,
    petId = petId,
    nombre = nombre,
    fechaDiagnostico = fechaDiagnostico,
    notas = notas
)

fun Condition.toEntity(): ConditionEntity = ConditionEntity(
    id = id,
    petId = petId,
    nombre = nombre,
    fechaDiagnostico = fechaDiagnostico,
    notas = notas
)
