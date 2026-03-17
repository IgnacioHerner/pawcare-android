package com.ignaherner.pawcare.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ignaherner.pawcare.data.local.entity.WeightEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightDao {

    @Query("SELECT * FROM weight WHERE petId = :petId ORDER BY fecha DESC")
    fun getWeightsByPetId(petId: Long): Flow<List<WeightEntity>>

    @Query("SELECT * FROM weight WHERE id = :weightId")
    suspend fun getWeightById(weightId: Long): WeightEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeight(weight: WeightEntity)

    @Update
    suspend fun updateWeight(weight: WeightEntity)

    @Delete
    suspend fun deleteWeight(weight: WeightEntity)
}