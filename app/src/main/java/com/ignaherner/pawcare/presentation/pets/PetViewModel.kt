package com.ignaherner.pawcare.presentation.pets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignaherner.pawcare.data.local.worker.WorkManagerHelper
import com.ignaherner.pawcare.data.remote.firestore.PetFirestoreRepository
import com.ignaherner.pawcare.data.repository.PetRepository
import com.ignaherner.pawcare.data.repository.WeightRepository
import com.ignaherner.pawcare.domain.model.Pet
import com.ignaherner.pawcare.domain.model.Weight
import com.ignaherner.pawcare.presentation.BaseViewModel
import com.ignaherner.pawcare.utils.fechaHoy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PetViewModel @Inject constructor(
    private val repository: PetRepository,
    private val firestoreRepository: PetFirestoreRepository,
    private val weightRepository: WeightRepository,
    private val workManagerHelper: WorkManagerHelper
): BaseViewModel() {

    // Estado de la UI
    private val _uiState = MutableStateFlow<PetUiState>(PetUiState.Loading)
    val uiState: StateFlow<PetUiState> = _uiState.asStateFlow()

    // Estado de PetDetailState
    private val _detailState = MutableStateFlow<PetDetailState>(PetDetailState.Loading)
    val detailState: StateFlow<PetDetailState> = _detailState.asStateFlow()


    init { loadPets() }

    private fun loadPets() {
        viewModelScope.launch {
            repository.getAllPets().collect { pets ->
                _uiState.value = if (pets.isEmpty()) PetUiState.Empty
                else PetUiState.Success(pets)
            }
        }
    }

    fun loadPetById(id: Long) {
        viewModelScope.launch {
            repository.getPetById(id).collect { pet ->
                _detailState.value = if (pet != null) PetDetailState.Success(pet)
                else PetDetailState.Error("Mascota no encontrada")
            }
        }
    }

    fun insertPet(pet: Pet) {
        safeLaunch(onError = "Error al guardar") {
            val firestoreResult = firestoreRepository.guardarPet(pet)
            if (firestoreResult.isSuccess) {
                val (firestoreId, codigo) = firestoreResult.getOrElse { return@safeLaunch }
                repository.insertPet(pet.copy(
                    firestoreId = firestoreId,
                    codigo = codigo
                ))
            } else {
                repository.insertPet(pet)
            }
        }
    }

    fun updatePet(pet: Pet) {
        safeLaunch(onError = "Error al actualizar") {
            repository.updatePet(pet)
            if (pet.firestoreId.isNotBlank()) {
                firestoreRepository.actualizarPet(pet)
            }
        }
    }

    fun deletePet(pet: Pet) {
        safeLaunch(onError = "Error al eliminar") {
            workManagerHelper.cancelarTodosLosRecordatoriosDeMascota(pet.id)
            repository.deletePet(pet)
            if (pet.firestoreId.isNotBlank()) {
                firestoreRepository.eliminarPet(pet.firestoreId)
            }
            showSnackbar("${pet.nombre} eliminado")
        }
    }

    fun insertPetConPeso(pet: Pet, pesoInicial: Double?) {
        safeLaunch(onError = "Error al guardar") {
            val firestoreResult = firestoreRepository.guardarPet(pet)
            val petToInsert = if (firestoreResult.isSuccess) {
                val (firestoreId, codigo) = firestoreResult.getOrNull()!!
                pet.copy(firestoreId = firestoreId, codigo = codigo)
            } else {
                pet
            }

            val petId = repository.insertPet(petToInsert)

            // Si hay peso inicial, guardarlo
            if (pesoInicial != null && pesoInicial > 0) {
                val weight = Weight(
                    petId = petId,
                    peso = pesoInicial,
                    fecha = fechaHoy(),
                    notas = null
                )
                weightRepository.insertWeight(weight)
            }
        }
    }
}

sealed class PetUiState {
    object Loading: PetUiState()
    object Empty: PetUiState()
    data class Success(val pets: List<Pet>) : PetUiState()
    data class Error(val mensaje: String) : PetUiState()
}

sealed class PetDetailState {
    object Loading : PetDetailState()
    data class Success(val pet: Pet) : PetDetailState()
    data class Error(val mensaje: String) : PetDetailState()
}