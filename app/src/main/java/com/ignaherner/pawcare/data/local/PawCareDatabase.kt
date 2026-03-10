package com.ignaherner.pawcare.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ignaherner.pawcare.data.local.dao.PetDao
import com.ignaherner.pawcare.data.local.entity.PetEntity

@Database(
    entities = [PetEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PawCareDatabase : RoomDatabase() {
    abstract fun petDao(): PetDao
}