package com.ignaherner.pawcare.data.local

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class VaccineWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, workerParams){

    companion object{
        const val KEY_PET_NAME = "pet_name"
        const val KEY_VACCINE_NAME = "vaccine_name"
        const val KEY_FECHA = "fecha"
        const val KEY_VACCINE_ID = "vaccine_id"
    }

    override suspend fun doWork(): Result {
        val petName = inputData.getString(KEY_PET_NAME) ?: return Result.failure()
        val vaccineName = inputData.getString(KEY_VACCINE_NAME) ?: return Result.failure()
        val fecha = inputData.getString(KEY_FECHA) ?: return Result.failure()
        val vaccineId = inputData.getInt(KEY_VACCINE_ID, -1)

        notificationHelper.showVaccineNotification(
            notificationId = vaccineId,
            petName = petName,
            vaccineName = vaccineName,
            fecha = fecha
        )
        return Result.success()
    }
}