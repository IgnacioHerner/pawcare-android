package com.ignaherner.pawcare.data.local

import com.ignaherner.pawcare.data.repository.MedicationRepository
import com.ignaherner.pawcare.data.repository.PetRepository
import com.ignaherner.pawcare.data.repository.VaccineRepository
import com.ignaherner.pawcare.domain.model.MedicationStatus
import com.ignaherner.pawcare.domain.model.VaccineStatus
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkManagerSyncManager @Inject constructor(
    private val medicationRepository: MedicationRepository,
    private val vaccineRepository: VaccineRepository,
    private val petRepository: PetRepository,
    private val workManagerHelper: WorkManagerHelper,
    private val settingsDataStore: SettingsDataStore
) {
    suspend fun syncWorkers() {
        // Cancelar TODOS los Workers existentes
        workManagerHelper.cancelAllWorkers()

        // Obtener todas las mascotas
        val pets = petRepository.getAllPets().firstOrNull() ?: return

        // Reprogramar solo los Workers válidos
        pets.forEach { pet ->
            // Medicamentos activos
            val medicamentos = medicationRepository
                .getMedicationByPetId(pet.id)
                .firstOrNull() ?: emptyList()

            medicamentos
                .filter { it.status == MedicationStatus.ACTIVO }
                .forEach { medication ->
                    workManagerHelper.programarRecordatorioMedicamento(
                        medication, pet.nombre
                    )
                    workManagerHelper.programarFinMedicamento(medication)
                }

            // Vacuanas con proxima dosis
            val vacunas = vaccineRepository
                .getVaccinesByPetId(pet.id)
                .firstOrNull() ?: emptyList()

            vacunas
                .filter {
                    it.status is VaccineStatus.Aplicada && it.proximaDosis != null
                }
                .forEach { vaccine ->
                    workManagerHelper.programarRecordatorioVacuna(
                        vaccine, pet.nombre
                    )
                }
        }
    }

}