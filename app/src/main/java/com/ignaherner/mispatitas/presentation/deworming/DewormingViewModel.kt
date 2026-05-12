package com.ignaherner.mispatitas.presentation.deworming

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignaherner.mispatitas.data.remote.firestore.DewormingFirestoreRepository
import com.ignaherner.mispatitas.data.repository.DewormingRepository
import com.ignaherner.mispatitas.data.repository.PetRepository
import com.ignaherner.mispatitas.domain.model.Deworming
import com.ignaherner.mispatitas.presentation.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DewormingViewModel @Inject constructor(
    private val repository: DewormingRepository,
    private val firestoreRepository: DewormingFirestoreRepository,
    private val petRepository: PetRepository
): BaseViewModel(){
    private val _uiState = MutableStateFlow<DewormingUiState>(DewormingUiState.Loading)
    val uiState: StateFlow<DewormingUiState> = _uiState.asStateFlow()

    private val _dewormingDetailState = MutableStateFlow<DewormingDetailState>(DewormingDetailState.Loading)
    val dewormingDetailState: StateFlow<DewormingDetailState> = _dewormingDetailState

    fun loadDewormings(petId: Long) {
        viewModelScope.launch {
            repository.getDewormingsByPetId(petId).collect { dewormings ->
                _uiState.value = if (dewormings.isEmpty()) DewormingUiState.Empty
                else DewormingUiState.Success(dewormings)
            }
        1}
    }

    fun loadDewormingById(id: Long) {
        viewModelScope.launch {
            val deworming = repository.getDewormingById(id)
            _dewormingDetailState.value = if (deworming != null)
                DewormingDetailState.Success(deworming)
            else
                DewormingDetailState.Error("Desparasitación no encontrada")
        }
    }
    fun insertDeworming(deworming: Deworming) {
        safeLaunch(onError = "Error al guardar") {
            val id = repository.insertDeworming(deworming)
            val dewormingConId = deworming.copy(id = id)
            val pet = petRepository.getPetById(deworming.petId).firstOrNull()
            val petFirestoreId = pet?.firestoreId ?: ""
            if (petFirestoreId.isNotBlank()) {
                val result = firestoreRepository.guardarDesparasitacion(dewormingConId, petFirestoreId)
                if (result.isSuccess) {
                    val firestoreId = result.getOrNull() ?: ""
                    repository.updateDeworming(dewormingConId.copy(firestoreId = firestoreId))
                }
            }
            showSnackbar("Desparasitación guardada")
        }
    }

    fun updateDeworming(deworming: Deworming) {
        safeLaunch(onError = "Error al actualizar") {
            repository.updateDeworming(deworming)
            val pet = petRepository.getPetById(deworming.petId).firstOrNull()
            val petFirestoreId = pet?.firestoreId ?: ""
            if (deworming.firestoreId.isNotBlank() && petFirestoreId.isNotBlank()) {
                firestoreRepository.actualizarDesparasitacion(deworming, petFirestoreId)
            }
        }
    }

    fun deleteDeworming(deworming: Deworming) {
        safeLaunch(onError = "Error al eliminar") {
            repository.deleteDeworming(deworming)
            val pet = petRepository.getPetById(deworming.petId).firstOrNull()
            val petFirestoreId = pet?.firestoreId ?: ""
            if (deworming.firestoreId.isNotBlank() && petFirestoreId.isNotBlank()) {
                firestoreRepository.eliminarDesparasitacion(deworming.firestoreId, petFirestoreId)
            }
            showSnackbar("Desparasitación eliminada")
        }
    }
}

sealed class DewormingUiState {
    object Loading: DewormingUiState()
    object Empty: DewormingUiState()
    data class Success(val deworming: List<Deworming>) : DewormingUiState()
    data class Error(val mensaje: String) : DewormingUiState()
}

sealed class DewormingDetailState {
    object Loading: DewormingDetailState()
    data class Success(val deworming: Deworming) : DewormingDetailState()
    data class Error(val mensaje: String) : DewormingDetailState()
}
