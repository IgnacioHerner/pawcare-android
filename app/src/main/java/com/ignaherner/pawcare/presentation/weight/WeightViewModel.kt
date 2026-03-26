package com.ignaherner.pawcare.presentation.weight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignaherner.pawcare.data.repository.WeightRepository
import com.ignaherner.pawcare.domain.model.Weight
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeightViewModel @Inject constructor(
    private val repository: WeightRepository
) : ViewModel(){

    private val _uiState = MutableStateFlow<WeightUiState>(WeightUiState.Loading)
    val uiState: StateFlow<WeightUiState> = _uiState.asStateFlow()

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

    fun insertWeight(weight: Weight){
        viewModelScope.launch {
            try {
                repository.insertWeight(weight)
            } catch (e: Exception) {
                _uiState.value = WeightUiState.Error(e.message ?: "Error al guardar")
            }
        }
    }

    fun updateWeight(weight: Weight) {
        viewModelScope.launch {
            try {
                repository.updateWeight(weight)
            }catch (e: Exception) {
                _uiState.value = WeightUiState.Error(e.message ?: "Error al actualizar")
            }
        }
    }

    fun deleteWeight(weight: Weight) {
        viewModelScope.launch {
            try {
                repository.deleteWeight(weight)
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
