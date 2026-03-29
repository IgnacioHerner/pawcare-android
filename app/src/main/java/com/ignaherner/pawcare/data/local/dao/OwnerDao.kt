package com.ignaherner.pawcare.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ignaherner.pawcare.data.local.entity.OwnerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OwnerDao {

    @Query("SELECT * FROM owners LIMIT 1")
    suspend fun getOwner(): OwnerEntity?

    @Query("SELECT * FROM owners WHERE id = :ownerId")
    suspend fun getOwnerById(ownerId: Long): OwnerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOwner(owner: OwnerEntity)

    @Update
    suspend fun updateOwner(owner: OwnerEntity)

     @Delete
     suspend fun deleteOwner(owner: OwnerEntity)
}