package com.ignaherner.pawcare.data.repository

import com.ignaherner.pawcare.data.local.dao.AppointmentDao
import com.ignaherner.pawcare.data.local.mapper.toDomain
import com.ignaherner.pawcare.data.local.mapper.toEntity
import com.ignaherner.pawcare.domain.model.Appointment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppointmentRepository @Inject constructor(
    private val appointmentDao: AppointmentDao
) {
    fun getAppointmentsByPetId(petId: Long) : Flow<List<Appointment>> =
        appointmentDao.getAppointmentsByPetId(petId)
            .map { entities ->
                entities.map { it.toDomain() }
            }

    suspend fun getAppointmentById(id: Long) : Appointment? =
        appointmentDao.getAppointmentById(id)?.toDomain()
    suspend fun insertAppointment(appointment: Appointment) =
        appointmentDao.insertAppointment(appointment.toEntity())

    suspend fun updateAppointment(appointment: Appointment) =
        appointmentDao.updateAppointment(appointment.toEntity())

    suspend fun deleteAppointment(appointment: Appointment) =
        appointmentDao.deleteAppointment(appointment.toEntity())
}