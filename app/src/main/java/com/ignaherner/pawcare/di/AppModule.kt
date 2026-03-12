package com.ignaherner.pawcare.di

import android.content.Context
import androidx.room.Room
import com.ignaherner.pawcare.data.local.PawCareDatabase
import com.ignaherner.pawcare.data.local.VaccineDao
import com.ignaherner.pawcare.data.local.dao.PetDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePawCareDatabase(
        @ApplicationContext context: Context
    ) : PawCareDatabase = Room.databaseBuilder(
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
    fun provideVaccineDao(database: PawCareDatabase) : VaccineDao =
        database.vaccineDao()
}