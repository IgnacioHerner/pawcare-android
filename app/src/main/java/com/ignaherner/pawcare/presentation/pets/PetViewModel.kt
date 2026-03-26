package com.ignaherner.pawcare.presentation.pets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignaherner.pawcare.data.local.WorkManagerHelper
import com.ignaherner.pawcare.data.repository.PetRepository
import com.ignaherner.pawcare.domain.model.Pet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PetViewModel @Inject constructor(
    private val repository: PetRepository,
    private val workManagerHelper: WorkManagerHelper
): ViewModel() {

    // Estado de la UI
    private val _uiState = MutableStateFlow<PetUiState>(PetUiState.Loading)
    val uiState: StateFlow<PetUiState> = _uiState.asStateFlow()

    // Estado de PetDetailState
    private val _detailState = MutableStateFlow<PetDetailState>(PetDetailState.Loading)
    val detailState: StateFlow<PetDetailState> = _detailState.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage = _snackbarMessage.asStateFlow()

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    init {
        loadPets()
    }

    private fun loadPets() {
        viewModelScope.launch {
            try {
                repository.getAllPets()
                    .collect { pets ->
                        _uiState.value = if ( pets.isEmpty()) {
                            PetUiState.Empty
                        } else {
                            PetUiState.Success(pets)
                        }
                    }
            } catch (e: Exception) {
                _uiState.value = PetUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun loadPetById(id: Long) {
        viewModelScope.launch {
            try {
                val pet = repository.getPetById(id)
                _detailState.value = if (pet != null) {
                    PetDetailState.Success(pet)
                } else {
                    PetDetailState.Error("Mascota no encontrada")
                }
            } catch (e: Exception) {
                _detailState.value = PetDetailState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun insertPet(pet: Pet) {
        viewModelScope.launch {
            try {
                repository.insertPet(pet)
            } catch (e: Exception) {
                _uiState.value = PetUiState.Error(e.message ?: "Error al guardar")
            }
        }
    }

    fun updatePet(pet: Pet) {
        viewModelScope.launch {
            try {
                repository.updatePet(pet)
            } catch (e: Exception) {
                _uiState.value = PetUiState.Error(e.message ?: "Error al actualizar")
            }
        }
    }

    fun deletePet(pet: Pet) {
        viewModelScope.launch {
            try {
                workManagerHelper.cancelarTodosLosRecordatoriosDeMascota(pet.id)
                repository.deletePet(pet)
                _snackbarMessage.value = "${pet.nombre} eliminado"
            } catch (e: Exception) {
                _snackbarMessage.value = "Error al eliminar"
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