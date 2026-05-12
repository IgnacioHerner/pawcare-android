package com.ignaherner.mispatitas.data.local.mapper

import com.ignaherner.mispatitas.data.local.entity.ConditionEntity
import com.ignaherner.mispatitas.domain.model.Condition
import com.ignaherner.mispatitas.domain.model.ConditionEstado
import com.ignaherner.mispatitas.domain.model.Severidad


fun ConditionEntity.toDomain(): Condition = Condition(
    id = id,
    firestoreId = firestoreId,
    petId = petId,
    nombre = nombre,
    fechaDiagnostico = fechaDiagnostico,
    severidad = try {
        Severidad.valueOf(severidad)
    } catch (e: Exception) {
        Severidad.LEVE
    },
    estado = try {
        ConditionEstado.valueOf(estado)
    } catch (e: Exception) {
        ConditionEstado.ACTIVA
    },
    veterinario = veterinario,
    notas = notas
)

fun Condition.toEntity(): ConditionEntity = ConditionEntity(
    id = id,
    firestoreId = firestoreId,
    petId = petId,
    nombre = nombre,
    fechaDiagnostico = fechaDiagnostico,
    severidad = severidad.name,
    estado = estado.name,
    veterinario = veterinario,
    notas = notas
)
