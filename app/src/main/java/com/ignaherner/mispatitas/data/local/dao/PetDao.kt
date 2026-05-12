package com.ignaherner.mispatitas.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ignaherner.mispatitas.data.local.entity.PetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PetDao {

    @Query("SELECT * FROM pets ORDER BY nombre ASC")
    fun getAllPets(): Flow<List<PetEntity>>

    @Query("SELECT * FROM pets WHERE id = :petId")
    fun getPetById(petId: Long): Flow<PetEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPet(pet: PetEntity) : Long

    @Update
    suspend fun updatePet(pet: PetEntity)

    @Delete
    suspend fun deletePet(pet: PetEntity)

    @Query("SELECT * FROM pets WHERE firestoreId = :firestoreId LIMIT 1")
    suspend fun getPetByFirestoreId(firestoreId: String): PetEntity?
}
