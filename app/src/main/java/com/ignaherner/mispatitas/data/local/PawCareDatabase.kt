package com.ignaherner.mispatitas.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ignaherner.mispatitas.data.local.dao.AppointmentDao
import com.ignaherner.mispatitas.data.local.dao.ConditionDao
import com.ignaherner.mispatitas.data.local.dao.DewormingDao
import com.ignaherner.mispatitas.data.local.dao.MedicationDao
import com.ignaherner.mispatitas.data.local.dao.PetDao
import com.ignaherner.mispatitas.data.local.dao.VaccineDao
import com.ignaherner.mispatitas.data.local.dao.WeightDao
import com.ignaherner.mispatitas.data.local.entity.AppointmentEntity
import com.ignaherner.mispatitas.data.local.entity.ConditionEntity
import com.ignaherner.mispatitas.data.local.entity.DewormingEntity
import com.ignaherner.mispatitas.data.local.entity.MedicationEntity
import com.ignaherner.mispatitas.data.local.entity.PetEntity
import com.ignaherner.mispatitas.data.local.entity.VaccineEntity
import com.ignaherner.mispatitas.data.local.entity.WeightEntity

@Database(
    entities = [
        PetEntity::class,
        VaccineEntity::class,
        AppointmentEntity::class,
        MedicationEntity::class,
        WeightEntity::class,
        ConditionEntity::class,
        DewormingEntity::class
        ],
    version = 32,
    exportSchema = true
)
abstract class PawCareDatabase : RoomDatabase() {
    abstract fun petDao(): PetDao
    abstract fun vaccineDao(): VaccineDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun medicationDao(): MedicationDao
    abstract fun weightDao(): WeightDao
    abstract fun conditionDao() : ConditionDao
    abstract fun dewormingDao() : DewormingDao
}


