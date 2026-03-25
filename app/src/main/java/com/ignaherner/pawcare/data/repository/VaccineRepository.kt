package com.ignaherner.pawcare.data.repository

import com.ignaherner.pawcare.data.local.dao.VaccineDao
import com.ignaherner.pawcare.data.local.mapper.toDomain
import com.ignaherner.pawcare.data.local.mapper.toEntity
import com.ignaherner.pawcare.domain.model.Vaccine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VaccineRepository @Inject constructor(
    private val vaccineDao: VaccineDao
){

    fun getVaccinesByPetId(petId: Long): Flow<List<Vaccine>> =
        vaccineDao.getVaccinesByPetId(petId)
            .map { entities ->
                entities.map { it.toDomain() }
            }

    suspend fun insertVaccine(vaccine: Vaccine) : Long =
        vaccineDao.insertVaccine(vaccine.toEntity())

    suspend fun updateVaccine(vaccine: Vaccine) =
        vaccineDao.updateVaccine(vaccine.toEntity())

    suspend fun deleteVaccine(vaccine: Vaccine) =
        vaccineDao.deleteVaccine(vaccine.toEntity())
}