package com.ignaherner.pawcare.data.local.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ignaherner.pawcare.data.remote.firestore.MedicationFirestoreRepository
import com.ignaherner.pawcare.data.repository.MedicationRepository
import com.ignaherner.pawcare.data.repository.PetRepository
import com.ignaherner.pawcare.domain.model.MedicationStatus
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull

@HiltWorker
class MedicationFinishWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val medicationRepository: MedicationRepository,
    private val medicationFirestoreRepository: MedicationFirestoreRepository,
    private val petRepository: PetRepository
): CoroutineWorker(context, workerParams) {

    companion object {
        const val KEY_MEDICAITON_ID = "medication_id"
        const val KEY_PET_ID = "pet_id"
    }

    override suspend fun doWork(): Result {
        val medicationId = inputData.getLong(KEY_MEDICAITON_ID, -1L)
        val petId = inputData.getLong(KEY_PET_ID, -1L)
        android.util.Log.d("WorkerDebug", "MedicationFinishWorker ejecutando para medicationId: $medicationId")

        if (medicationId == -1L) return Result.failure()

        return try {
            val medication = medicationRepository.getMedicationById(medicationId)
            medication?.let {
                val medicationFinalizada = it.copy(status = MedicationStatus.FINALIZADO)

                // 1. Actualizar Room
                medicationRepository.updateMedication(medicationFinalizada)

                // 2. Actualizar Firestore
                if (it.firestoreId.isNotBlank()) {
                    val pet = petRepository.getPetById(petId).firstOrNull()
                    val petFirestoreId = pet?.firestoreId ?: ""
                    if (petFirestoreId.isNotBlank()) {
                        medicationFirestoreRepository.actualizarMedicamento(
                            medicationFinalizada,
                            petFirestoreId
                        )
                    }
                }
            }
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry()
            else Result.failure()
        }
    }
}