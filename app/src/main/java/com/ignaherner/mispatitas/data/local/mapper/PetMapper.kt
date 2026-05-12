package com.ignaherner.mispatitas.data.local.mapper

import com.ignaherner.mispatitas.data.local.entity.PetEntity
import com.ignaherner.mispatitas.domain.model.Especie
import com.ignaherner.mispatitas.domain.model.FechaNacimientoTipo
import com.ignaherner.mispatitas.domain.model.Pet
import com.ignaherner.mispatitas.domain.model.Sex

fun PetEntity.toDomain(): Pet = Pet(
    id = id,
    firestoreId = firestoreId,
    codigo = codigo,
    ownerId = ownerId,
    nombre = nombre,
    especie = Especie.valueOf(especie),
    raza = raza,
    sexo = sexo?.let {
        when(it) {
            "MACHO" -> Sex.MACHO
            else -> Sex.HEMBRA
        }
    },
    fechaNacimiento = fechaNacimiento,
    fechaNacimientoTipo = when(fechaNacimientoTipo) {
        "EXACTA" -> FechaNacimientoTipo.EXACTA
        "APROXIMADA" -> FechaNacimientoTipo.APROXIMADA
        else -> FechaNacimientoTipo.DESCONOCIDA
    },
    fotoUri = fotoUri,
    castrado = castrado,
    fechaCastracion = fechaCastracion,
    fechaUltimaDesparasitacion = fechaUltimaDesparasitacion,
    proximaDesparasitacion = proximaDesparasitacion
)

fun Pet.toEntity(): PetEntity = PetEntity(
    id = id,
    firestoreId = firestoreId,
    codigo = codigo,
    ownerId = ownerId,
    nombre = nombre,
    especie = especie.name,
    raza = raza,
    sexo = sexo?.name,
    fechaNacimiento = fechaNacimiento,
    fechaNacimientoTipo = fechaNacimientoTipo.name,
    fotoUri = fotoUri,
    castrado = castrado,
    fechaCastracion = fechaCastracion,
    fechaUltimaDesparasitacion = fechaUltimaDesparasitacion,
    proximaDesparasitacion = proximaDesparasitacion
)
