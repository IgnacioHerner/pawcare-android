package com.ignaherner.pawcare.data.repository

import com.ignaherner.pawcare.data.local.dao.WeightDao
import com.ignaherner.pawcare.data.local.mapper.toDomain
import com.ignaherner.pawcare.data.local.mapper.toEntity
import com.ignaherner.pawcare.domain.model.Weight
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeightRepository @Inject constructor(
    private val weightDao: WeightDao
) {
    fun getWeightByPetId(petId: Long): Flow<List<Weight>> =
        weightDao.getWeightsByPetId(petId)
            .map { entities ->
                entities.map { it.toDomain() }
            }

    suspend fun insertWeight(weight: Weight) =
        weightDao.insertWeight(weight.toEntity())

    suspend fun updateWeight(weight: Weight) =
        weightDao.updateWeight(weight.toEntity())

    suspend fun deleteWeight(weight: Weight) =
        weightDao.deleteWeight(weight.toEntity())
}