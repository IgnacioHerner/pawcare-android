package com.ignaherner.pawcare.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignaherner.pawcare.data.local.worker.WorkManagerHelper
import com.ignaherner.pawcare.data.remote.firestore.AppointmentFirestoreRepository
import com.ignaherner.pawcare.data.remote.firestore.ConditionFirestoreRepository
import com.ignaherner.pawcare.data.remote.firestore.DewormingFirestoreRepository
import com.ignaherner.pawcare.data.remote.firestore.MedicationFirestoreRepository
import com.ignaherner.pawcare.data.repository.MedicationRepository
import com.ignaherner.pawcare.data.remote.firestore.PetFirestoreRepository
import com.ignaherner.pawcare.data.remote.firestore.VaccineFirestoreRepository
import com.ignaherner.pawcare.data.remote.firestore.WeightFirestoreRepository
import com.ignaherner.pawcare.data.repository.AppointmentRepository
import com.ignaherner.pawcare.data.repository.ConditionRepository
import com.ignaherner.pawcare.data.repository.DewormingRepository
import com.ignaherner.pawcare.data.repository.PetRepository
import com.ignaherner.pawcare.data.repository.VaccineRepository
import com.ignaherner.pawcare.data.repository.WeightRepository
import com.ignaherner.pawcare.domain.model.MedicationStatus
import com.ignaherner.pawcare.domain.model.Pet
import com.ignaherner.pawcare.domain.model.PetSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val petRepository: PetRepository,
    private val petFirestoreRepository: PetFirestoreRepository,
    private val workManagerHelper: WorkManagerHelper,
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
    private val dewormingFirestoreRepository: DewormingFirestoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            sincronizarMascotas()
            loadHome()
        }
    }

    private suspend fun sincronizarMascotas() {
        try {
            val result = petFirestoreRepository.obtenerMascotasDueno()
            if (result.isSuccess) {
                result.getOrNull()?.forEach { petFirestore ->
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

                    // Sincronizar subcolecciones
                    val localPet = petRepository.getPetByFirestoreId(petFirestore.firestoreId)
                    localPet?.let { pet ->
                        sincronizarVacunas(pet, petFirestore.firestoreId)
                        sincronizarMedicamentos(pet, petFirestore.firestoreId)
                        sincronizarPesos(pet, petFirestore.firestoreId)
                        sincronizarTurnos(pet, petFirestore.firestoreId)
                        sincronizarCondiciones(pet, petFirestore.firestoreId)
                        sincronizarDesparasitaciones(pet, petFirestore.firestoreId)
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("SyncDebug", "Error: ${e.message}")
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
                        vaccineRepository.insertVaccine(vaccineConPetId)
                    } else {
                        vaccineRepository.updateVaccine(vaccineConPetId.copy(id = local.id))
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("SyncDebug", "Error vacunas: ${e.message}")
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
                        medicationRepository.insertMedication(medicationConPetId)
                    } else {
                        medicationRepository.updateMedication(medicationConPetId.copy(id = local.id))
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("SyncDebug", "Error medicamentos: ${e.message}")
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
            android.util.Log.e("SyncDebug", "Error pesos: ${e.message}")
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
            android.util.Log.e("SyncDebug", "Error turnos: ${e.message}")
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
            android.util.Log.e("SyncDebug", "Error condiciones: ${e.message}")
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
            android.util.Log.e("SyncDebug", "Error desparasitaciones: ${e.message}")
        }
    }

    fun loadHome() {
        viewModelScope.launch {
            try {
                petRepository.getAllPets()
                    .collect { pets ->
                        if(pets.isEmpty()) {
                            _uiState.value = HomeUiState.Empty
                            return@collect
                        }
                        val summaries = pets.map { pet ->
                            val vacunas = vaccineRepository
                                .getVaccinesByPetId(pet.id)
                                .firstOrNull() ?: emptyList()

                            val medicamentos = medicationRepository
                                .getMedicationByPetId(pet.id)
                                .firstOrNull() ?: emptyList()

                            val pesos = weightRepository
                                .getWeightByPetId(pet.id)
                                .firstOrNull() ?: emptyList()

                            PetSummary(
                                pet = pet,
                                proximaVacuna = vacunas
                                    .filter { it.proximaDosis != null }
                                    .minByOrNull { it.proximaDosis!! },
                                medicamentoActivo = medicamentos
                                    .firstOrNull { it.status == MedicationStatus.ACTIVO },
                                ultimoPeso = pesos.firstOrNull()
                            )
                        }
                        _uiState.value = HomeUiState.Success(summaries)
                    }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun deletePet(pet: Pet){
        viewModelScope.launch {
            try {
                workManagerHelper.cancelarTodosLosRecordatoriosDeMascota(pet.id)
                petRepository.deletePet(pet)
                if (pet.firestoreId.isNotBlank()){
                    petFirestoreRepository.eliminarPet(pet.firestoreId)
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Error al eliminar")
            }
        }
    }
}

sealed class HomeUiState {
    object Loading: HomeUiState()
    object Empty: HomeUiState()
    data class Success(val summaries: List<PetSummary>) : HomeUiState()
    data class Error(val mensaje: String) : HomeUiState()
}
