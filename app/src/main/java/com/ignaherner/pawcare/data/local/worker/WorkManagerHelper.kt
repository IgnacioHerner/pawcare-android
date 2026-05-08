package com.ignaherner.pawcare.data.local.worker

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.ignaherner.pawcare.domain.model.Medication
import com.ignaherner.pawcare.domain.model.Vaccine
import com.ignaherner.pawcare.utils.calcularFechaFin
import com.ignaherner.pawcare.utils.diasHastaFecha
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
            .putString(MedicationWorker.KEY_DOSIS, medication.dosisDisplay)
            .putString(MedicationWorker.KEY_MEDICATION_ID, medication.id.toString())
            .build()

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
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )

    }

    fun cancelarRecordatorioMedicamento(medicationId: Long) {
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
            .putString(VaccineWorker.KEY_VACCINE_NAME, vaccine.tipo.displayName)
            .putString(VaccineWorker.KEY_FECHA, proximaDosis)
            .putString(VaccineWorker.KEY_VACCINE_ID, vaccine.id.toString())
            .build()

        val workRequest = OneTimeWorkRequestBuilder<VaccineWorker>()
            .setInputData(inputData)
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
        workManager.cancelAllWorkByTag("vaccine_${vaccineId}")
    }

    fun cancelarTodosLosRecordatoriosDeMascota(petId: Long) {
        workManager.cancelAllWorkByTag("pet_$petId")
    }

    fun cancelAllWorkers() {
        workManager.cancelAllWork()
    }
}