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
    val petId: Long,
    val nombre: String,
    val fecha: String?,
    val proximaDosis: String?,
    val veterinario: String?,
    val notas: String?,
    val status: String
)
