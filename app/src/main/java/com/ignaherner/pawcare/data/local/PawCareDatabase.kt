package com.ignaherner.pawcare.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ignaherner.pawcare.data.local.dao.AppointmentDao
import com.ignaherner.pawcare.data.local.dao.MedicationDao
import com.ignaherner.pawcare.data.local.dao.PetDao
import com.ignaherner.pawcare.data.local.dao.VaccineDao
import com.ignaherner.pawcare.data.local.entity.AppointmentEntity
import com.ignaherner.pawcare.data.local.entity.MedicationEntity
import com.ignaherner.pawcare.data.local.entity.PetEntity
import com.ignaherner.pawcare.data.local.entity.VaccineEntity

@Database(
    entities = [
        PetEntity::class,
        VaccineEntity::class,
        AppointmentEntity::class,
        MedicationEntity::class
        ],
    version = 4,
    exportSchema = false
)
abstract class PawCareDatabase : RoomDatabase() {
    abstract fun petDao(): PetDao
    abstract fun vaccineDao(): VaccineDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun medicationDao(): MedicationDao
}


