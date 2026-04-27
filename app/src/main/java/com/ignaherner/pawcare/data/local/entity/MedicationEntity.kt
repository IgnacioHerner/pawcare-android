package com.ignaherner.pawcare.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "medications",
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
data class MedicationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val firestoreId: String,
    val petId: Long,
    val nombre: String,
    val dosisCantidad: Double,
    val dosisUnidad: String,
    val viaAdministracion: String,
    val esUnicaDosis: Int = 0,
    val fechaInicio: String,
    val duracionDias: Int,
    val intervaloHoras: Int,
    val recetadoPor: String?,
    val notas: String?
)