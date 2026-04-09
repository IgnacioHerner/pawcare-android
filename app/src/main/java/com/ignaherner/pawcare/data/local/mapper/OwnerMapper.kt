package com.ignaherner.pawcare.data.local.mapper

import com.ignaherner.pawcare.data.local.entity.OwnerEntity
import com.ignaherner.pawcare.domain.model.Owner

fun OwnerEntity.toDomain(): Owner = Owner(
    id = id,
    nombre = nombre,
    apellido = apellido,
    telefono = telefono,
    email = email,
    ciudad = ciudad,
    direccion = direccion,
    fotoUri = fotoUri
)

fun Owner.toEntity() : OwnerEntity = OwnerEntity(
    id = id,
    nombre = nombre,
    apellido = apellido,
    telefono = telefono,
    email = email,
    ciudad = ciudad,
    direccion = direccion,
    fotoUri = fotoUri
)