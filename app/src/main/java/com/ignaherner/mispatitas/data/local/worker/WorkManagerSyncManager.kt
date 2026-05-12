package com.ignaherner.mispatitas.data.local.worker

import com.ignaherner.mispatitas.data.local.SettingsDataStore
import com.ignaherner.mispatitas.data.repository.MedicationRepository
import com.ignaherner.mispatitas.data.repository.PetRepository
import com.ignaherner.mispatitas.data.repository.VaccineRepository
import com.ignaherner.mispatitas.domain.model.MedicationStatus
import com.ignaherner.mispatitas.utils.calcularFechaFin
import com.ignaherner.mispatitas.utils.diasHastaFecha
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
            // Medicamentos activos — status se calcula automáticamente
            val medicamentos = medicationRepository
                .getMedicationByPetId(pet.id)
                .firstOrNull() ?: emptyList()

            medicamentos
                .filter { it.status == MedicationStatus.ACTIVO && !it.esUnicaDosis }
                .forEach { medication ->
                    workManagerHelper.programarRecordatorioMedicamento(
                        medication, pet.nombre
                    )
                }

            // Vacunas con próxima dosis
            val vacunas = vaccineRepository
                .getVaccinesByPetId(pet.id)
                .firstOrNull() ?: emptyList()

            vacunas
                .filter { it.proximaDosis != null }
                .forEach { vaccine ->
                    workManagerHelper.programarRecordatorioVacuna(
                        vaccine, pet.nombre
                    )
                }
        }
    }
}
