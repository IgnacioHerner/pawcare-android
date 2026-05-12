package com.ignaherner.mispatitas.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "appointments",
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
data class AppointmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val firestoreId: String = "",
    val petId: Long,
    val fecha: String,
    val motivo: String,
    val veterinario: String?,
    val clinica: String?,
    val diagnostico: String?,
    val notas: String?
)
