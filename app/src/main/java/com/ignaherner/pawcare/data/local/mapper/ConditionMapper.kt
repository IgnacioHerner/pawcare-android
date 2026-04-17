package com.ignaherner.pawcare.data.local.mapper

import com.ignaherner.pawcare.data.local.entity.ConditionEntity
import com.ignaherner.pawcare.domain.model.Condition


fun ConditionEntity.toDomain(): Condition = Condition(
    id = id,
    firestoreId = firestoreId,
    petId = petId,
    nombre = nombre,
    fechaDiagnostico = fechaDiagnostico,
    notas = notas
)

fun Condition.toEntity(): ConditionEntity = ConditionEntity(
    id = id,
    firestoreId = firestoreId,
    petId = petId,
    nombre = nombre,
    fechaDiagnostico = fechaDiagnostico,
    notas = notas
)
