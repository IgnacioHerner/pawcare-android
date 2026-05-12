package com.ignaherner.mispatitas.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "dewormings",
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
data class DewormingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val firestoreId: String = "",
    val petId: Long,
    val producto: String,
    val tipo: String,
    val fechaAplicacion: String,
    val frecuencia: String,
    val proximaDosis: String?,
    val veterinario: String?,
    val notas: String?
)
