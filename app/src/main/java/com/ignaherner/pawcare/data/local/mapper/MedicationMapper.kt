package com.ignaherner.pawcare.data.local.mapper

import com.ignaherner.pawcare.data.local.entity.MedicationEntity
import com.ignaherner.pawcare.domain.model.DosisUnidad
import com.ignaherner.pawcare.domain.model.Medication
import com.ignaherner.pawcare.domain.model.MedicationStatus
import com.ignaherner.pawcare.domain.model.ViaAdministracion

// ═══════════════════════════════════════════════════════════
// MEDICATION MAPPER
// El status NO se guarda — se calcula dinámicamente
// ═══════════════════════════════════════════════════════════

fun MedicationEntity.toDomain(): Medication = Medication(
    id = id,
    firestoreId = firestoreId,
    petId = petId,
    nombre = nombre,
    dosisCantidad = dosisCantidad,
    dosisUnidad = try {
        DosisUnidad.valueOf(dosisUnidad)
    } catch (e: Exception) {
        DosisUnidad.COMPRIMIDO
    },
    viaAdministracion = try {
        ViaAdministracion.valueOf(viaAdministracion)
    } catch (e: Exception) {
        ViaAdministracion.ORAL
    },
    esUnicaDosis = esUnicaDosis == 1,
    fechaInicio = fechaInicio,
    duracionDias = duracionDias,
    intervaloHoras = intervaloHoras,
    recetadoPor = recetadoPor,
    notas = notas
)

fun Medication.toEntity(): MedicationEntity = MedicationEntity(
    id = id,
    firestoreId = firestoreId,
    petId = petId,
    nombre = nombre,
    dosisCantidad = dosisCantidad,
    dosisUnidad = dosisUnidad.name,
    viaAdministracion = viaAdministracion.name,
    esUnicaDosis = if (esUnicaDosis) 1 else 0,
    fechaInicio = fechaInicio,
    duracionDias = duracionDias,
    intervaloHoras = intervaloHoras,
    recetadoPor = recetadoPor,
    notas = notas
)