package com.ignaherner.pawcare.data.repository

import com.ignaherner.pawcare.data.local.dao.ConditionDao
import com.ignaherner.pawcare.data.local.mapper.toDomain
import com.ignaherner.pawcare.data.local.mapper.toEntity
import com.ignaherner.pawcare.domain.model.Condition
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConditionRepository @Inject constructor(
    private val conditionDao: ConditionDao
) {
    fun getConditionsByPetId(petId: Long) : Flow<List<Condition>> =
        conditionDao.getConditionsByPetId(petId)
            .map { entities ->
                entities.map {it.toDomain()}
            }

    suspend fun getConditionById(id: Long) : Condition? =
        conditionDao.getConditionById(id)?.toDomain()

    suspend fun insertCondition(condition: Condition): Long =
        conditionDao.insertCondition(condition.toEntity())
    suspend fun updateCondition(condition: Condition) =
        conditionDao.updateCondition(condition.toEntity())

    suspend fun deleteCondition(condition: Condition) =
        conditionDao.deleteCondition(condition.toEntity())
}