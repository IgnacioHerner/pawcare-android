package com.ignaherner.mispatitas.data.repository

import com.ignaherner.mispatitas.data.local.dao.WeightDao
import com.ignaherner.mispatitas.data.local.mapper.toDomain
import com.ignaherner.mispatitas.data.local.mapper.toEntity
import com.ignaherner.mispatitas.domain.model.Weight
import com.ignaherner.mispatitas.utils.toLocalDate
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
                entities
                    .map { it.toDomain() }
                    .sortedByDescending { it.fecha.toLocalDate() }
            }

    suspend fun getWeightById(id: Long): Weight? =
        weightDao.getWeightById(id)?.toDomain()

    suspend fun insertWeight(weight: Weight): Long =
        weightDao.insertWeight(weight.toEntity())

    suspend fun updateWeight(weight: Weight) =
        weightDao.updateWeight(weight.toEntity())

    suspend fun deleteWeight(weight: Weight) =
        weightDao.deleteWeight(weight.toEntity())
}
