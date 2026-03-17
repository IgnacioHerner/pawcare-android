package com.ignaherner.pawcare.data.local.mapper

import com.ignaherner.pawcare.data.local.entity.PetEntity
import com.ignaherner.pawcare.domain.model.Especie
import com.ignaherner.pawcare.domain.model.Pet
import com.ignaherner.pawcare.domain.model.Sex

fun PetEntity.toDomain(): Pet = Pet(
    id = id,
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
    peso = peso,
    fotoUri = fotoUri
)

fun Pet.toEntity(): PetEntity = PetEntity(
    id = id,
    nombre = nombre,
    especie = especie.name,
    raza = raza,
    sexo = sexo?.name,
    fechaNacimiento = fechaNacimiento,
    peso = peso,
    fotoUri = fotoUri
)