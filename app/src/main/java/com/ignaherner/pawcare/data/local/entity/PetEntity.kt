package com.ignaherner.pawcare.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pets")
data class PetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val especie: String,
    val raza: String?,
    val sexo: String?,
    val fechaNacimiento: String?,
    val peso: Double?,
    val fotoUri: String?
)