package com.ignaherner.pawcare.presentation.pets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val repository: PetRepository
): ViewModel() {

    // Estado de la UI
    private val _uiState = MutableStateFlow<PetUiState>(PetUiState.Loading)
    val uiState: StateFlow<PetUiState> = _uiState.asStateFlow()

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
                repository.deletePet(pet)
            } catch (e: Exception) {
                _uiState.value = PetUiState.Error(e.message ?: "Error al eliminar")
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
