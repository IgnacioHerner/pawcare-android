package com.ignaherner.pawcare.di

import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.room.Room
import androidx.work.Configuration
import com.ignaherner.pawcare.data.local.PawCareDatabase
import com.ignaherner.pawcare.data.local.SettingsDataStore
import com.ignaherner.pawcare.data.local.dao.AppointmentDao
import com.ignaherner.pawcare.data.local.dao.MedicationDao
import com.ignaherner.pawcare.data.local.dao.PetDao
import com.ignaherner.pawcare.data.local.dao.VaccineDao
import com.ignaherner.pawcare.data.local.dao.WeightDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    companion object {
        @Provides
        @Singleton
        fun providePawCareDatabase(
            @ApplicationContext context: Context
        ): PawCareDatabase = Room.databaseBuilder(
            context,
            PawCareDatabase::class.java,
            "pawcare_database"
        )
            .fallbackToDestructiveMigration()
            .build()

        @Provides
        @Singleton
        fun providePetDao(database: PawCareDatabase): PetDao =
            database.petDao()

        @Provides
        @Singleton
        fun provideVaccineDao(database: PawCareDatabase): VaccineDao =
            database.vaccineDao()

        @Provides
        @Singleton
        fun provideAppointmentDao(database: PawCareDatabase): AppointmentDao =
            database.appointmentDao()

        @Provides
        @Singleton
        fun provideMedicationDao(database: PawCareDatabase): MedicationDao =
            database.medicationDao()

        @Provides
        @Singleton
        fun provideWeightDao(database: PawCareDatabase): WeightDao =
            database.weightDao()

        @Provides
        @Singleton
        fun provideSettingsDataStore(
            @ApplicationContext context: Context
        ): SettingsDataStore = SettingsDataStore(context)

        @Provides
        @Singleton
        fun provideWorkManagerConfiguration(
            workerFactory: HiltWorkerFactory
        ): Configuration = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }
}