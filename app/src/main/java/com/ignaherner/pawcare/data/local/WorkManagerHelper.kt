package com.ignaherner.pawcare.data.local

import android.content.Context
import androidx.work.Data
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.ignaherner.pawcare.domain.model.Medication
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkManagerHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val workManager = WorkManager.getInstance(context)

    fun programarRecordatorioMedicamento(
        medication: Medication,
        petName: String
    ) {
        val inputData = Data.Builder()
            .putString(MedicationWorker.KEY_PET_NAME, petName)
            .putString(MedicationWorker.KEY_MEDICATION_NAME, medication.nombre)
            .putString(MedicationWorker.KEY_DOSIS, medication.dosis)
            .putString(MedicationWorker.KEY_MEDICATION_ID, medication.id.toInt().toString())
            .build()

//        val workRequest = PeriodicWorkRequestBuilder<MedicationWorker>(
//            medication.intervaloHoras.toLong(),
//            TimeUnit.HOURS
//        )

        // WorkManagerHelper.kt — temporal para testear
        val workRequest = PeriodicWorkRequestBuilder<MedicationWorker>(
            15L,
            TimeUnit.MINUTES  // ← temporal
        )
            .setInputData(inputData)
            .addTag("medication_${medication.id}")
            .build()

        workManager.enqueueUniquePeriodicWork(
            "medication_${medication.id}",
            androidx.work.ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )

    }

    fun cancelarRecordatorioMedicamento(medicationId: Long) {
        workManager.cancelAllWorkByTag("medication_${medicationId}")
    }
}