package com.ignaherner.pawcare.presentation.weight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignaherner.pawcare.data.remote.firestore.WeightFirestoreRepository
import com.ignaherner.pawcare.data.repository.PetRepository
import com.ignaherner.pawcare.data.repository.WeightRepository
import com.ignaherner.pawcare.domain.model.Weight
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
) : ViewModel(){

    private val _uiState = MutableStateFlow<WeightUiState>(WeightUiState.Loading)
    val uiState: StateFlow<WeightUiState> = _uiState.asStateFlow()

    private val _weightDetailState = MutableStateFlow<WeightDetailState>(WeightDetailState.Loading)
    val weightDetailState: StateFlow<WeightDetailState> = _weightDetailState.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage = _snackbarMessage.asStateFlow()

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun loadWeights(petId: Long) {
        viewModelScope.launch {
            try {
                repository.getWeightByPetId(petId)
                    .collect { weights ->
                        _uiState.value = if (weights.isEmpty()){
                            WeightUiState.Empty
                        } else {
                            WeightUiState.Success(weights)
                        }
                    }
            }catch (e: Exception) {
                _uiState.value = WeightUiState.Error(e.message ?: "Error desconociod0")
            }
        }
    }

    fun loadWeightById(id: Long) {
        viewModelScope.launch {
            try {
                val weight = repository.getWeightById(id)
                _weightDetailState.value = if (weight != null) {
                    WeightDetailState.Success(weight)
                } else {
                    WeightDetailState.Error("Peso no encontrado")
                }
            } catch (e: Exception) {
                _weightDetailState.value = WeightDetailState.Error(e.message ?: "Error")
            }

        }
    }

    fun insertWeight(weight: Weight){
        viewModelScope.launch {
            try {
                val id = repository.insertWeight(weight)
                val weightConId = weight.copy(id = id)

                withContext(NonCancellable){
                    val pet = petRepository.getPetById(weightConId.petId).firstOrNull()
                    val petFirestoreId = pet?.firestoreId ?: ""
                    if (petFirestoreId.isNotBlank()){
                        val firestoreResult = firestoreRepository.guardarPeso(
                            weightConId, petFirestoreId
                        )
                        if (firestoreResult.isSuccess){
                            val firestoreId = firestoreResult.getOrNull() ?: ""
                            repository.updateWeight(weightConId.copy(firestoreId = firestoreId))
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = WeightUiState.Error(e.message ?: "Error al guardar")
            }
        }
    }

    fun updateWeight(weight: Weight) {
        viewModelScope.launch {
            try {
                repository.updateWeight(weight)
                if (weight.firestoreId.isNotBlank()){
                    val pet = petRepository.getPetById(weight.petId).firstOrNull()
                    val petFirestoreId = pet?.firestoreId ?: ""
                    if (petFirestoreId.isNotBlank()){
                        firestoreRepository.actualizarPeso(weight, petFirestoreId)
                    }
                }
            }catch (e: Exception) {
                _uiState.value = WeightUiState.Error(e.message ?: "Error al actualizar")
            }
        }
    }

    fun deleteWeight(weight: Weight) {
        viewModelScope.launch {
            try {
                repository.deleteWeight(weight)
                val pet = petRepository.getPetById(weight.petId).firstOrNull()
                val petFirestoreId = pet?.firestoreId ?: ""
                if (weight.firestoreId.isNotBlank() && petFirestoreId.isNotBlank()){
                    firestoreRepository.eliminarPeso(weight.firestoreId, petFirestoreId)
                }
                _snackbarMessage.value = "${weight.fecha} eliminada"
            } catch (e: Exception) {
                _snackbarMessage.value = "Error al eliminar"
            }
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
