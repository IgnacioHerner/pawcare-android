package com.ignaherner.pawcare.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ignaherner.pawcare.data.local.entity.VaccineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VaccineDao {

    @Query("SELECT * FROM vaccines WHERE petId = :petId ORDER BY fecha DESC")
    fun getVaccinesByPetId(petId: Long): Flow<List<VaccineEntity>>

    @Query("SELECT * FROM vaccines WHERE id = :vaccineId")
    suspend fun getVaccineById(vaccineId: Long): VaccineEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVaccine(vaccine: VaccineEntity) : Long

    @Update
    suspend fun updateVaccine(vaccine: VaccineEntity)

    @Delete
    suspend fun deleteVaccine(vaccine: VaccineEntity)

}