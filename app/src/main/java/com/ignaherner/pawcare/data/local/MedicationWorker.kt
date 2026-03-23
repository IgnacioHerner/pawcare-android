package com.ignaherner.pawcare.data.local

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class MedicationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, workerParams){

    companion object {
        const val KEY_PET_NAME = "pet_name"
        const val KEY_MEDICATION_NAME = "medication_name"
        const val KEY_DOSIS = "dosis"
        const val KEY_MEDICATION_ID = "medication_id"
    }

    override suspend fun doWork(): Result {
        val petName = inputData.getString(KEY_PET_NAME) ?: return Result.failure()
        val medicationName = inputData.getString(KEY_MEDICATION_NAME) ?: return Result.failure()
        val dosis = inputData.getString(KEY_DOSIS) ?: return Result.failure()
        val medicationId = inputData.getInt(KEY_MEDICATION_ID, -1)

        notificationHelper.showMedicationNotification(
            notificationId = medicationId,
            petName = petName,
            medicationName = medicationName,
            dosis = dosis
        )

        return Result.success()
    }

}