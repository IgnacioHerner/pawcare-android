package com.ignaherner.pawcare.presentation.deworming

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignaherner.pawcare.data.repository.DewormingRepository
import com.ignaherner.pawcare.domain.model.Deworming
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DewormingViewModel @Inject constructor(
    private val repository: DewormingRepository
): ViewModel(){
    private val _uiState = MutableStateFlow<DewormingUiState>(DewormingUiState.Loading)
    val uiState: StateFlow<DewormingUiState> = _uiState.asStateFlow()

    private val _dewormingDetailState = MutableStateFlow<DewormingDetailState>(DewormingDetailState.Loading)
    val dewormingDetailState: StateFlow<DewormingDetailState> = _dewormingDetailState

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage = _snackbarMessage.asStateFlow()

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun loadDewormings(petId: Long) {
        viewModelScope.launch {
            try {
                repository.getDewormingsByPetId(petId)
                    .collect { deworming ->
                        _uiState.value = if (deworming.isEmpty()) {
                            DewormingUiState.Empty
                        } else {
                            DewormingUiState.Success(deworming)
                        }
                    }
            } catch (e: Exception) {
                _uiState.value = DewormingUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun loadDewormingById(id: Long) {
        viewModelScope.launch {
            try {
                val deworming = repository.getDewormingById(id)
                _dewormingDetailState.value = if (deworming != null) {
                    DewormingDetailState.Success(deworming)
                } else {
                    DewormingDetailState.Error("Desparacitacion no encontrada")
                }
            } catch (e: Exception) {
                _dewormingDetailState.value = DewormingDetailState.Error(e.message ?: "Error")
            }
        }
    }

    fun insertDeworming(deworming: Deworming) {
        viewModelScope.launch {
            try {
                repository.insertDeworming(deworming)
                _snackbarMessage.value = "${deworming.fecha} agregada ✅"
            } catch (e: Exception) {
                _snackbarMessage.value = "Error al guardar"
            }
        }
    }

    fun updateDeworming(deworming: Deworming) {
        viewModelScope.launch {
            try {
                repository.updateDeworming(deworming)
            } catch (e: Exception) {
                _uiState.value = DewormingUiState.Error(e.message ?: "Error al actualizar")
            }
        }
    }

    fun deleteDeworming(deworming: Deworming) {
        viewModelScope.launch {
            try {
                repository.deleteDeworming(deworming)
                _snackbarMessage.value = "${deworming.fecha} eliminada"
            } catch (e: Exception) {
                _uiState.value = DewormingUiState.Error(e.message ?: "Error al eliminar")
            }
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
