package com.ignaherner.pawcare.data.repository

import com.ignaherner.pawcare.data.local.dao.MedicationDao
import com.ignaherner.pawcare.data.local.mapper.toDomain
import com.ignaherner.pawcare.data.local.mapper.toEntity
import com.ignaherner.pawcare.domain.model.Medication
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MedicationRepository @Inject constructor(
    private val medicationDao: MedicationDao
) {

    fun getMedicationByPetId(petId: Long): Flow<List<Medication>> =
        medicationDao.getMedicationsByPetId(petId)
            .map { entities ->
                entities.map { it.toDomain() }
            }

    suspend fun insertMedication(medication: Medication) : Long =
        medicationDao.insertMedication(medication.toEntity())

    suspend fun updateMedication(medication: Medication) =
        medicationDao.updateMedication(medication.toEntity())

    suspend fun deleteMedication(medication: Medication) =
        medicationDao.deleteMedication(medication.toEntity())
}