package com.ignaherner.mispatitas.data.repository

import com.ignaherner.mispatitas.data.local.worker.WorkManagerHelper
import com.ignaherner.mispatitas.data.remote.firestore.AppointmentFirestoreRepository
import com.ignaherner.mispatitas.data.remote.firestore.ConditionFirestoreRepository
import com.ignaherner.mispatitas.data.remote.firestore.DewormingFirestoreRepository
import com.ignaherner.mispatitas.data.remote.firestore.MedicationFirestoreRepository
import com.ignaherner.mispatitas.data.remote.firestore.PetFirestoreRepository
import com.ignaherner.mispatitas.data.remote.firestore.VaccineFirestoreRepository
import com.ignaherner.mispatitas.data.remote.firestore.WeightFirestoreRepository
import com.ignaherner.mispatitas.domain.model.MedicationStatus
import com.ignaherner.mispatitas.domain.model.Pet
import com.ignaherner.mispatitas.domain.model.VaccineStatus
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepository @Inject constructor(
    private val petRepository: PetRepository,
    private val petFirestoreRepository: PetFirestoreRepository,
    private val vaccineRepository: VaccineRepository,
    private val vaccineFirestoreRepository: VaccineFirestoreRepository,
    private val medicationRepository: MedicationRepository,
    private val medicationFirestoreRepository: MedicationFirestoreRepository,
    private val weightRepository: WeightRepository,
    private val weightFirestoreRepository: WeightFirestoreRepository,
    private val appointmentRepository: AppointmentRepository,
    private val appointmentFirestoreRepository: AppointmentFirestoreRepository,
    private val conditionRepository: ConditionRepository,
    private val conditionFirestoreRepository: ConditionFirestoreRepository,
    private val dewormingRepository: DewormingRepository,
    private val dewormingFirestoreRepository: DewormingFirestoreRepository,
    private val workManagerHelper: WorkManagerHelper
) {

    private val syncMutex = Mutex()

    suspend fun sincronizarTodo() = syncMutex.withLock { // ← solo un sync a la vez
        try {
            val result = petFirestoreRepository.obtenerMascotasDueno()
            if (result.isSuccess) {
                result.getOrNull()?.forEach { petFirestore ->
                    // Validar que tiene firestoreId
                    if (petFirestore.firestoreId.isBlank()) {
                        return@forEach
                    }

                    val petLocal = petRepository.getPetByFirestoreId(petFirestore.firestoreId)

                    if (petLocal == null) {
                        petRepository.insertPet(petFirestore)
                    } else {
                        val petActualizado = petFirestore.copy(
                            id = petLocal.id,
                            fotoUri = petLocal.fotoUri ?: petFirestore.fotoUri
                        )
                        petRepository.updatePet(petActualizado)
                    }

                    val pet = petRepository.getPetByFirestoreId(petFirestore.firestoreId)
                    pet?.let {
                        sincronizarVacunas(it, petFirestore.firestoreId)
                        sincronizarMedicamentos(it, petFirestore.firestoreId)
                        sincronizarPesos(it, petFirestore.firestoreId)
                        sincronizarTurnos(it, petFirestore.firestoreId)
                        sincronizarCondiciones(it, petFirestore.firestoreId)
                        sincronizarDesparasitaciones(it, petFirestore.firestoreId)
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("SyncRepository", "Error: ${e.message}")
        }
    }

    private suspend fun sincronizarVacunas(pet: Pet, petFirestoreId: String) {
        try {
            val result = vaccineFirestoreRepository.obtenerVacunasPorMascota(petFirestoreId)
            if (result.isSuccess) {
                result.getOrNull()?.forEach { vaccineFirestore ->
                    val vaccineConPetId = vaccineFirestore.copy(petId = pet.id)
                    val local = vaccineRepository.getVaccinesByPetId(pet.id)
                        .firstOrNull()
                        ?.find { it.firestoreId == vaccineFirestore.firestoreId }

                    if (local == null) {
                        val id = vaccineRepository.insertVaccine(vaccineConPetId)
                        val vaccineConId = vaccineConPetId.copy(id = id)

                        // Programar Worker si tiene próxima dosis
                        if (vaccineConId.status is VaccineStatus.Aplicada && vaccineConId.proximaDosis != null) {
                            workManagerHelper.programarRecordatorioVacuna(vaccineConId, pet.nombre)
                        }
                    } else {
                        vaccineRepository.updateVaccine(vaccineConPetId.copy(id = local.id))
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("SyncRepository", "Error vacunas: ${e.message}")
        }
    }
    private suspend fun sincronizarMedicamentos(pet: Pet, petFirestoreId: String) {
        try {
            val result = medicationFirestoreRepository.obtenerMedicamentosPorMascota(petFirestoreId)
            if (result.isSuccess) {
                result.getOrNull()?.forEach { medicationFirestore ->
                    val medicationConPetId = medicationFirestore.copy(petId = pet.id)
                    val local = medicationRepository.getMedicationByPetId(pet.id)
                        .firstOrNull()
                        ?.find { it.firestoreId == medicationFirestore.firestoreId }
                    if (local == null) {
                        val id = medicationRepository.insertMedication(medicationConPetId)
                        val medicacionConId = medicationConPetId.copy(id = id)

                        if(medicacionConId.status == MedicationStatus.ACTIVO && !medicacionConId.esUnicaDosis){
                            workManagerHelper.programarRecordatorioMedicamento(medicacionConId, pet.nombre)
                        }

                    } else {
                        medicationRepository.updateMedication(medicationConPetId.copy(id = local.id))
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("SyncRepository", "Error medicamentos: ${e.message}")
        }
    }


    private suspend fun sincronizarTurnos(pet: Pet, petFirestoreId: String) {
        try {
            val result = appointmentFirestoreRepository.obtenerTurnosPorMascota(petFirestoreId)
            if (result.isSuccess) {
                result.getOrNull()?.forEach { appointmentFirestore ->
                    val appointmentConPetId = appointmentFirestore.copy(petId = pet.id)
                    val local = appointmentRepository.getAppointmentsByPetId(pet.id)
                        .firstOrNull()
                        ?.find { it.firestoreId == appointmentFirestore.firestoreId }
                    if (local == null) {
                        appointmentRepository.insertAppointment(appointmentConPetId)
                    } else {
                        appointmentRepository.updateAppointment(
                            appointmentConPetId.copy(id = local.id)
                        )
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("SyncRepository", "Error turnos: ${e.message}")
        }
    }

    private suspend fun sincronizarPesos(pet: Pet, petFirestoreId: String) {
        try {
            val result = weightFirestoreRepository.obtenerPesosPorMascota(petFirestoreId)
            if (result.isSuccess) {
                result.getOrNull()?.forEach { weightFirestore ->
                    val weightConPetId = weightFirestore.copy(petId = pet.id)
                    val local = weightRepository.getWeightByPetId(pet.id)
                        .firstOrNull()
                        ?.find { it.firestoreId == weightFirestore.firestoreId }
                    if (local == null) {
                        weightRepository.insertWeight(weightConPetId)
                    } else {
                        weightRepository.updateWeight(weightConPetId.copy(id = local.id))
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("SyncRepository", "Error pesos: ${e.message}")
        }
    }
    private suspend fun sincronizarCondiciones(pet: Pet, petFirestoreId: String) {
        try {
            val result = conditionFirestoreRepository.obtenerCondicionesPorMascota(petFirestoreId)
            if (result.isSuccess) {
                result.getOrNull()?.forEach { conditionFirestore ->
                    val conditionConPetId = conditionFirestore.copy(petId = pet.id)
                    val local = conditionRepository.getConditionsByPetId(pet.id)
                        .firstOrNull()
                        ?.find { it.firestoreId == conditionFirestore.firestoreId }
                    if (local == null) {
                        conditionRepository.insertCondition(conditionConPetId)
                    } else {
                        conditionRepository.updateCondition(
                            conditionConPetId.copy(id = local.id)
                        )
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("SyncRepository", "Error condiciones: ${e.message}")
        }
    }

    private suspend fun sincronizarDesparasitaciones(pet: Pet, petFirestoreId: String) {
        try {
            val result = dewormingFirestoreRepository.obtenerDesparasitacionesPorMascota(petFirestoreId)
            if (result.isSuccess) {
                result.getOrNull()?.forEach { dewormingFirestore ->
                    val dewormingConPetId = dewormingFirestore.copy(petId = pet.id)
                    val local = dewormingRepository.getDewormingsByPetId(pet.id)
                        .firstOrNull()
                        ?.find { it.firestoreId == dewormingFirestore.firestoreId }
                    if (local == null) {
                        dewormingRepository.insertDeworming(dewormingConPetId)
                    } else {
                        dewormingRepository.updateDeworming(
                            dewormingConPetId.copy(id = local.id)
                        )
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("SyncRepository", "Error desparasitaciones: ${e.message}")
        }
    }

}
