package com.ignaherner.mispatitas.data.local.mapper

import com.ignaherner.mispatitas.data.local.entity.VaccineEntity
import com.ignaherner.mispatitas.domain.model.FrecuenciaVacuna
import com.ignaherner.mispatitas.domain.model.TipoVacuna
import com.ignaherner.mispatitas.domain.model.Vaccine

// ═══════════════════════════════════════════════════════════
// VACCINE MAPPER
// Convierte entre Entity (Room) y Domain (Vaccine)
// El status NO se guarda — se calcula dinámicamente
// ═══════════════════════════════════════════════════════════

fun VaccineEntity.toDomain(): Vaccine = Vaccine(
    id = id,
    firestoreId = firestoreId,
    petId = petId,
    tipo = try {
        TipoVacuna.valueOf(tipo)
    } catch (e: Exception) {
        TipoVacuna.OTRA
    },
    nombreComercial = nombreComercial,
    fechaAplicacion = fechaAplicacion,
    frecuencia = try {
        FrecuenciaVacuna.valueOf(frecuencia)
    } catch (e: Exception) {
        FrecuenciaVacuna.UNICA
    },
    proximaDosis = proximaDosis,
    veterinario = veterinario,
    notas = notas
)

fun Vaccine.toEntity(): VaccineEntity = VaccineEntity(
    id = id,
    firestoreId = firestoreId,
    petId = petId,
    tipo = tipo.name,
    nombreComercial = nombreComercial,
    fechaAplicacion = fechaAplicacion,
    frecuencia = frecuencia.name,
    proximaDosis = proximaDosis,
    veterinario = veterinario,
    notas = notas
)
