package com.ignaherner.mispatitas.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "conditions",
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
data class ConditionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val firestoreId: String = "",
    val petId: Long,
    val nombre: String,
    val fechaDiagnostico: String,
    val severidad: String,
    val estado: String,
    val veterinario: String?,
    val notas: String?
)
