package com.ignaherner.mispatitas.presentation.condition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignaherner.mispatitas.data.remote.firestore.ConditionFirestoreRepository
import com.ignaherner.mispatitas.data.repository.ConditionRepository
import com.ignaherner.mispatitas.data.repository.PetRepository
import com.ignaherner.mispatitas.domain.model.Condition
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
class ConditionViewModel @Inject constructor(
    private val repository: ConditionRepository,
    private val firestoreRepository: ConditionFirestoreRepository,
    private val petRepository: PetRepository
): BaseViewModel(){
    private val _uiState = MutableStateFlow<ConditionUiState>(ConditionUiState.Loading)
    val uiState: StateFlow<ConditionUiState> = _uiState.asStateFlow()

    private val _conditionDetailState = MutableStateFlow<ConditionDetailState>(ConditionDetailState.Loading)
    val conditionDetailState: StateFlow<ConditionDetailState> = _conditionDetailState

    fun loadConditions(petId: Long) {
        viewModelScope.launch {
            repository.getConditionsByPetId(petId).collect { conditions ->
                _uiState.value = if (conditions.isEmpty()) ConditionUiState.Empty
                else ConditionUiState.Success(conditions)
            }
        }
    }

    fun loadConditionById(id: Long) {
        viewModelScope.launch {
            val condition = repository.getConditionById(id)
            _conditionDetailState.value = if (condition != null)
                ConditionDetailState.Success(condition)
            else
                ConditionDetailState.Error("Condición no encontrada")
        }
    }


    fun insertCondition(condition: Condition) {
        safeLaunch(onError = "Error al guardar") {
            val id = repository.insertCondition(condition)
            val conditionConId = condition.copy(id = id)
            val pet = petRepository.getPetById(condition.petId).firstOrNull()
            val petFirestoreId = pet?.firestoreId ?: ""
            if (petFirestoreId.isNotBlank()) {
                val result = firestoreRepository.guardarCondicion(conditionConId, petFirestoreId)
                if (result.isSuccess) {
                    val firestoreId = result.getOrNull() ?: ""
                    repository.updateCondition(conditionConId.copy(firestoreId = firestoreId))
                }
            }
            showSnackbar("Condición guardada")
        }
    }

    fun updateCondition(condition: Condition) {
        safeLaunch(onError = "Error al actualizar") {
            repository.updateCondition(condition)
            val pet = petRepository.getPetById(condition.petId).firstOrNull()
            val petFirestoreId = pet?.firestoreId ?: ""
            if (condition.firestoreId.isNotBlank() && petFirestoreId.isNotBlank()) {
                firestoreRepository.actualizarCondicion(condition, petFirestoreId)
            }
        }
    }

    fun deleteCondition(condition: Condition) {
        safeLaunch(onError = "Error al eliminar") {
            repository.deleteCondition(condition)
            val pet = petRepository.getPetById(condition.petId).firstOrNull()
            val petFirestoreId = pet?.firestoreId ?: ""
            if (condition.firestoreId.isNotBlank() && petFirestoreId.isNotBlank()) {
                firestoreRepository.eliminarCondicion(condition.firestoreId, petFirestoreId)
            }
            showSnackbar("Condición eliminada")
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
