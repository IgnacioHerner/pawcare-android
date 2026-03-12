package com.ignaherner.pawcare.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ignaherner.pawcare.data.local.dao.PetDao
import com.ignaherner.pawcare.data.local.entity.PetEntity
import com.ignaherner.pawcare.data.local.entity.VaccineEntity

@Database(
    entities = [
        PetEntity::class,
        VaccineEntity::class,
        ],
    version = 2,
    exportSchema = false
)
abstract class PawCareDatabase : RoomDatabase() {
    abstract fun petDao(): PetDao
    abstract fun vaccineDao(): VaccineDao
}

class VaccineDao {

}

