package com.ignaherner.mispatitas.data.local.mapper

import com.ignaherner.mispatitas.data.local.entity.DewormingEntity
import com.ignaherner.mispatitas.domain.model.Deworming
import com.ignaherner.mispatitas.domain.model.DewormingTipo
import com.ignaherner.mispatitas.domain.model.FrecuenciaDeworming

fun DewormingEntity.toDomain(): Deworming = Deworming(
    id = id,
    firestoreId = firestoreId,
    petId = petId,
    producto = producto,
    tipo = try {
        DewormingTipo.valueOf(tipo)
    } catch (e: Exception) {
        DewormingTipo.INTERNA
    },
    fechaAplicacion = fechaAplicacion,
    frecuencia = try {
        FrecuenciaDeworming.valueOf(frecuencia)
    } catch (e: Exception) {
        FrecuenciaDeworming.UNICA
    },
    proximaDosis = proximaDosis,
    veterinario = veterinario,
    notas = notas
)

fun Deworming.toEntity(): DewormingEntity = DewormingEntity(
    id = id,
    firestoreId = firestoreId,
    petId = petId,
    producto = producto,
    tipo = tipo.name,
    fechaAplicacion = fechaAplicacion,
    frecuencia = frecuencia.name,
    proximaDosis = proximaDosis,
    veterinario = veterinario,
    notas = notas
)
