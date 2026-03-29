package com.ignaherner.pawcare.data.repository

import com.ignaherner.pawcare.data.local.dao.OwnerDao
import com.ignaherner.pawcare.data.local.mapper.toDomain
import com.ignaherner.pawcare.data.local.mapper.toEntity
import com.ignaherner.pawcare.domain.model.Owner
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OwnerRepository @Inject constructor(
    private val ownerDao: OwnerDao
){

    suspend fun getOwner(): Owner? =
        ownerDao.getOwner()?.toDomain()

    suspend fun getOwnerById(id: Long) : Owner? =
        ownerDao.getOwnerById(id)?.toDomain()

    suspend fun insertOwner(owner: Owner) =
        ownerDao.insertOwner(owner.toEntity())

    suspend fun updateOwner(owner: Owner) =
        ownerDao.updateOwner(owner.toEntity())

    suspend fun deleteOwner(owner: Owner) =
        ownerDao.deleteOwner(owner.toEntity())
}