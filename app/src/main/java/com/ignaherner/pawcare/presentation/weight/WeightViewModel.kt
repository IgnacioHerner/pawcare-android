package com.ignaherner.pawcare.presentation.weight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignaherner.pawcare.data.remote.firestore.WeightFirestoreRepository
import com.ignaherner.pawcare.data.repository.PetRepository
import com.ignaherner.pawcare.data.repository.WeightRepository
import com.ignaherner.pawcare.domain.model.Weight
import com.ignaherner.pawcare.presentation.BaseViewModel
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
class WeightViewModel @Inject constructor(
    private val repository: WeightRepository,
    private val firestoreRepository: WeightFirestoreRepository,
    private val petRepository: PetRepository
) : BaseViewModel(){

    private val _uiState = MutableStateFlow<WeightUiState>(WeightUiState.Loading)
    val uiState: StateFlow<WeightUiState> = _uiState.asStateFlow()

    private val _weightDetailState = MutableStateFlow<WeightDetailState>(WeightDetailState.Loading)
    val weightDetailState: StateFlow<WeightDetailState> = _weightDetailState.asStateFlow()

    fun loadWeights(petId: Long) {
        viewModelScope.launch {
            repository.getWeightByPetId(petId).collect { weights ->
                _uiState.value = if (weights.isEmpty()) WeightUiState.Empty
                else WeightUiState.Success(weights)
            }
        }
    }

    fun loadWeightById(id: Long) {
        viewModelScope.launch {
            val weight = repository.getWeightById(id)
            _weightDetailState.value = if (weight != null)
                WeightDetailState.Success(weight)
            else
                WeightDetailState.Error("Peso no encontrado")
        }
    }

    fun insertWeight(weight: Weight) {
        safeLaunch(onError = "Error al guardar") {
            val id = repository.insertWeight(weight)
            val weightConId = weight.copy(id = id)
            val pet = petRepository.getPetById(weight.petId).firstOrNull()
            val petFirestoreId = pet?.firestoreId ?: ""
            if (petFirestoreId.isNotBlank()) {
                val result = firestoreRepository.guardarPeso(weightConId, petFirestoreId)
                if (result.isSuccess) {
                    val firestoreId = result.getOrNull() ?: ""
                    repository.updateWeight(weightConId.copy(firestoreId = firestoreId))
                }
            }
            showSnackbar("Peso registrado")
        }
    }

    fun updateWeight(weight: Weight) {
        safeLaunch(onError = "Error al actualizar") {
            repository.updateWeight(weight)
            val pet = petRepository.getPetById(weight.petId).firstOrNull()
            val petFirestoreId = pet?.firestoreId ?: ""
            if (weight.firestoreId.isNotBlank() && petFirestoreId.isNotBlank()) {
                firestoreRepository.actualizarPeso(weight, petFirestoreId)
            }
        }
    }

    fun deleteWeight(weight: Weight) {
        safeLaunch(onError = "Error al eliminar") {
            repository.deleteWeight(weight)
            val pet = petRepository.getPetById(weight.petId).firstOrNull()
            val petFirestoreId = pet?.firestoreId ?: ""
            if (weight.firestoreId.isNotBlank() && petFirestoreId.isNotBlank()) {
                firestoreRepository.eliminarPeso(weight.firestoreId, petFirestoreId)
            }
            showSnackbar("Peso eliminado")
        }
    }
}

sealed class WeightUiState {
    object Loading: WeightUiState()
    object Empty: WeightUiState()
    data class Success(val weights: List<Weight>) : WeightUiState()
    data class Error(val mensaje: String) : WeightUiState()
}

sealed class WeightDetailState{
    object Loading: WeightDetailState()
    data class Success(val weight: Weight) : WeightDetailState()
    data class Error(val mensaje: String) : WeightDetailState()
}
