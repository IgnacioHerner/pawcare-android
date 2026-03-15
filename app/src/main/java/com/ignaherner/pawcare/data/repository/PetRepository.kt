package com.ignaherner.pawcare.data.repository

import com.ignaherner.pawcare.data.local.dao.PetDao
import com.ignaherner.pawcare.data.local.mapper.toDomain
import com.ignaherner.pawcare.data.local.mapper.toEntity
import com.ignaherner.pawcare.domain.model.Pet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PetRepository @Inject constructor(
    private val petDao: PetDao
){
    fun getAllPets(): Flow<List<Pet>> =
        petDao.getAllPets()  // Flow<List<PetEntity>>
            .map { entities -> // Flow operator - transforma cada emision del Flow
                entities.map { // List operator - transforma cada elemento de la lista
                    it.toDomain()  // PetEntity -> Pet
                }
            }

    suspend fun getPetById(id: Long) : Pet? =
        petDao.getPetById(id)?.toDomain()

    suspend fun insertPet(pet: Pet) =
        petDao.insertPet(pet.toEntity())

    suspend fun updatePet(pet: Pet) =
        petDao.updatePet(pet.toEntity())

    suspend fun deletePet(pet: Pet) =
        petDao.deletePet(pet.toEntity())
}