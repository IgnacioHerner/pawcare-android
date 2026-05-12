package com.ignaherner.mispatitas.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ignaherner.mispatitas.data.local.entity.ConditionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConditionDao {

    @Query("SELECT * FROM conditions WHERE petId = :petId ORDER BY fechaDiagnostico ASC")
    fun getConditionsByPetId(petId: Long): Flow<List<ConditionEntity>>

    @Query("SELECT * FROM conditions WHERE id = :conditionId")
    suspend fun getConditionById(conditionId: Long): ConditionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCondition(condition: ConditionEntity): Long

    @Update
    suspend fun updateCondition(condition: ConditionEntity)

    @Delete
    suspend fun deleteCondition(condition: ConditionEntity)
}
