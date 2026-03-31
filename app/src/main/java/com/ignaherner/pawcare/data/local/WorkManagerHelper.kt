package com.ignaherner.pawcare.data.local

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.ignaherner.pawcare.domain.model.Medication
import com.ignaherner.pawcare.domain.model.Vaccine
import com.ignaherner.pawcare.domain.model.diasHastaFecha
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

        // WorkManagerHelper.kt — temporal para testear
//        val workRequest = PeriodicWorkRequestBuilder<MedicationWorker>(
//            15L,
//            TimeUnit.MINUTES  // ← temporal
//        )

        val workRequest = PeriodicWorkRequestBuilder<MedicationWorker>(
            medication.intervaloHoras.toLong(),
            TimeUnit.HOURS
        )

            .setInputData(inputData)
            .setInitialDelay(
                medication.intervaloHoras.toLong(),
                TimeUnit.HOURS
            )
            .addTag("medication_${medication.id}")
            .addTag("pet_${medication.petId}")
            .build()

        workManager.enqueueUniquePeriodicWork(
            "medication_${medication.id}",
            androidx.work.ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )

    }

    fun cancelarRecordatorioMedicamento(medicationId: Long) {
        android.util.Log.d("WorkManager", "Cancelando medication_$medicationId")
        workManager.cancelAllWorkByTag("medication_${medicationId}")
    }

    fun programarRecordatorioVacuna(
        vaccine: Vaccine,
        petName: String
    ) {
        // Si no tiene proxima dosis, no programaos nada
        val proximaDosis = vaccine.proximaDosis ?: return

        // Calculamos los dias que faltan
        val diasRestantes = diasHastaFecha(proximaDosis)

        // Si la fecha ya paso, no programamos
        if (diasRestantes <= 0) return

        val inputData = Data.Builder()
            .putString(VaccineWorker.KEY_PET_NAME, petName)
            .putString(VaccineWorker.KEY_VACCINE_NAME, vaccine.nombre)
            .putString(VaccineWorker.KEY_FECHA, proximaDosis)
            .putString(VaccineWorker.KEY_VACCINE_ID, vaccine.id.toInt().toString())
            .build()

        val workRequest = OneTimeWorkRequestBuilder<VaccineWorker>()
            .setInputData(inputData)
            // Temporal para testear
//             .setInitialDelay(1L, TimeUnit.MINUTES)
            // Producción
            .setInitialDelay(diasRestantes, TimeUnit.DAYS)
            .addTag("vaccine_${vaccine.id}")
            .addTag("pet_${vaccine.petId}")
            .build()

        workManager.enqueueUniqueWork(
            "vaccine_${vaccine.id}",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun cancelarRecordatorioVacuna(vaccineId: Long) {
        android.util.Log.d("WorkManager", "Cancelando vaccine_$vaccineId")
        workManager.cancelAllWorkByTag("vaccine_${vaccineId}")
    }

    fun cancelarTodosLosRecordatoriosDeMascota(petId: Long) {
        workManager.cancelAllWorkByTag("pet_$petId")
    }

    fun programarFinMedicamento(medication: Medication) {
        // No programar si es unica dosis - se finaliza inmediatamente
        if (medication.esUnicaDosis) return

        val inputData = Data.Builder()
            .putLong(MedicationFinishWorker.KEY_MEDICAITON_ID, medication.id)
            .putLong(MedicationFinishWorker.KEY_PET_ID, medication.petId)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<MedicationFinishWorker>()
            .setInputData(inputData)
            // Producción
//            .setInitialDelay(
//                medication.duracionDias.toLong(),
//                TimeUnit.DAYS
//            )
            // Temporal para testear
            .setInitialDelay(1L, TimeUnit.MINUTES)

            .addTag("medication_finish_${medication.id}")
            .addTag("pet_${medication.petId}")
            .build()

        workManager.enqueueUniqueWork(
            "medication_finish_${medication.id}",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun cancelarFinMedicamento(medicationId: Long) {
        workManager.cancelAllWorkByTag("medication_finish_$medicationId")
    }
}