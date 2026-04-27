package com.ignaherner.pawcare.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "vaccines",
    foreignKeys = [
        ForeignKey(
            entity = PetEntity::class,
            parentColumns = ["id"],
            childColumns = ["petId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("petId")]
)
data class VaccineEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val firestoreId: String = "",
    val petId: Long,
    val tipo: String, // TipoVacuna como String (ej: "ANTIRRABICA_CANINA")
    val nombreComercial: String?,
    val fechaAplicacion: String,
    val frecuencia: String, // FrecuenciaVacuna como String (ej: "ANUAL")
    val proximaDosis: String?,
    val veterinario: String?,
    val notas: String?
)
