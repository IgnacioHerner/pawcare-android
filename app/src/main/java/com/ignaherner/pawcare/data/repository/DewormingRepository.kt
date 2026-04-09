package com.ignaherner.pawcare.data.repository

import com.ignaherner.pawcare.data.local.dao.DewormingDao
import com.ignaherner.pawcare.data.local.mapper.toDomain
import com.ignaherner.pawcare.data.local.mapper.toEntity
import com.ignaherner.pawcare.domain.model.Deworming
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DewormingRepository @Inject constructor(
    private val dewormingDao: DewormingDao
) {
    fun getDewormingsByPetId(petId: Long) : Flow<List<Deworming>> =
        dewormingDao.getDewormingsByPetId(petId)
            .map { entities ->
                entities.map { it.toDomain() }
            }

    suspend fun getDewormingById(id: Long) : Deworming? =
        dewormingDao.getDewormingById(id)?.toDomain()

    suspend fun insertDeworming(deworming: Deworming): Long =
        dewormingDao.insertDeworming(deworming.toEntity())

    suspend fun updateDeworming(deworming: Deworming) =
        dewormingDao.updateDeworming(deworming.toEntity())

    suspend fun deleteDeworming(deworming: Deworming) =
        dewormingDao.deleteDeworming(deworming.toEntity())
}