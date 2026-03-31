package com.ignaherner.pawcare.data.local

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ignaherner.pawcare.data.repository.MedicationRepository
import com.ignaherner.pawcare.domain.model.MedicationStatus
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class MedicationFinishWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val medicationRepository: MedicationRepository
): CoroutineWorker(context, workerParams) {

    companion object {
        const val KEY_MEDICAITON_ID = "medication_id"
        const val KEY_PET_ID = "pet_id"
    }

    override suspend fun doWork(): Result {
        val medicationId = inputData.getLong(KEY_MEDICAITON_ID, -1L)
        val petID = inputData.getLong(KEY_PET_ID, -1L)

        if (medicationId == -1L) return Result.failure()

        return try {
            val medication = medicationRepository.getMedicationById(medicationId)
            medication?.let {
                val medicationFinalizada = it.copy(
                    status = MedicationStatus.FINALIZADO
                )
                medicationRepository.updateMedication(medicationFinalizada)
            }
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}