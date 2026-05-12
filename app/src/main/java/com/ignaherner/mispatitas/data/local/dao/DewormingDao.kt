package com.ignaherner.mispatitas.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ignaherner.mispatitas.data.local.entity.DewormingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DewormingDao {

    @Query("SELECT * FROM dewormings WHERE petId = :petId ORDER BY fechaAplicacion DESC")
    fun getDewormingsByPetId(petId: Long): Flow<List<DewormingEntity>>
    @Query("SELECT * FROM dewormings WHERE id = :dewormingId")
    suspend fun getDewormingById(dewormingId: Long): DewormingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeworming(deworming: DewormingEntity): Long

    @Update
    suspend fun updateDeworming(deworming: DewormingEntity)

    @Delete
    suspend fun deleteDeworming(deworming: DewormingEntity)
}
