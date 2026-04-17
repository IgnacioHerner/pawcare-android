package com.ignaherner.pawcare.presentation.condition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignaherner.pawcare.data.remote.firestore.ConditionFirestoreRepository
import com.ignaherner.pawcare.data.repository.ConditionRepository
import com.ignaherner.pawcare.data.repository.PetRepository
import com.ignaherner.pawcare.domain.model.Condition
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
class ConditionViewModel @Inject constructor(
    private val repository: ConditionRepository,
    private val firestoreRepository: ConditionFirestoreRepository,
    private val petRepository: PetRepository
): ViewModel(){
    private val _uiState = MutableStateFlow<ConditionUiState>(ConditionUiState.Loading)
    val uiState: StateFlow<ConditionUiState> = _uiState.asStateFlow()

    private val _conditionDetailState = MutableStateFlow<ConditionDetailState>(ConditionDetailState.Loading)
    val conditionDetailState: StateFlow<ConditionDetailState> = _conditionDetailState

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage = _snackbarMessage.asStateFlow()

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun loadConditions(petId: Long) {
        viewModelScope.launch {
            try {
                repository.getConditionsByPetId(petId)
                    .collect { conditions ->
                        _uiState.value = if(conditions.isEmpty()) {
                            ConditionUiState.Empty
                        } else {
                            ConditionUiState.Success(conditions)
                        }
                    }
            }catch (e: Exception) {
                _uiState.value = ConditionUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun loadConditionById(id: Long) {
        viewModelScope.launch {
            try {
                val condition = repository.getConditionById(id)
                _conditionDetailState.value = if (condition != null) {
                    ConditionDetailState.Success(condition)
                } else {
                    ConditionDetailState.Error("Condicion no encontrado")
                }
            } catch (e: Exception) {
                _conditionDetailState.value = ConditionDetailState.Error(e.message ?: "Error")
            }
        }
    }

    fun insertCondition(condition: Condition) {
        viewModelScope.launch {
            try {
                val id = repository.insertCondition(condition)
                val conditionConId = condition.copy(id = id)

                withContext(NonCancellable){
                    val pet = petRepository.getPetById(conditionConId.petId).firstOrNull()
                    val petFirestoreId = pet?.firestoreId ?: ""
                    if (petFirestoreId.isNotBlank()) {
                        val firestoreResult = firestoreRepository.guardarCondicion(
                            conditionConId, petFirestoreId
                        )
                        if (firestoreResult.isSuccess) {
                            val firestoreId = firestoreResult.getOrNull() ?: ""
                            repository.updateCondition(conditionConId.copy(firestoreId = firestoreId))
                        }
                    }
                }
                _snackbarMessage.value = "${condition.nombre} agregada ✅"
            } catch (e: Exception) {
                _snackbarMessage.value =  "Error al guardar"
            }
        }
    }

    fun updateCondition(condition: Condition) {
        viewModelScope.launch {
            try {
                repository.updateCondition(condition)
                if (condition.firestoreId.isNotBlank()){
                    val pet = petRepository.getPetById(condition.petId).firstOrNull()
                    val petFirestoreId = pet?.firestoreId ?: ""
                    if(petFirestoreId.isNotBlank()){
                        firestoreRepository.actualizarCondicion(condition, petFirestoreId)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = ConditionUiState.Error(e.message ?: "Error al actualizar")
            }
        }
    }

    fun deleteCondition(condition: Condition) {
        viewModelScope.launch {
            try {
                repository.deleteCondition(condition)
                val pet = petRepository.getPetById(condition.petId).firstOrNull()
                val petFirestoreId = pet?.firestoreId ?: ""
                if (condition.firestoreId.isNotBlank() && petFirestoreId.isNotBlank()) {
                    firestoreRepository.eliminarCondicion(condition.firestoreId, petFirestoreId)
                }
                _snackbarMessage.value = "${condition.nombre} eliminada"
            } catch (e: Exception) {
                _uiState.value = ConditionUiState.Error(e.message ?: "Error al eliminar")
            }
        }
    }
}

sealed class ConditionDetailState {
    object Loading: ConditionDetailState()
    data class Success(val condition: Condition) : ConditionDetailState()
    data class Error(val mensaje: String) : ConditionDetailState()
}

sealed class ConditionUiState{
    object Loading: ConditionUiState()
    object Empty: ConditionUiState()
    data class Success(val conditions: List<Condition>) : ConditionUiState()
    data class Error(val mensaje: String) : ConditionUiState()
}