package com.ignaherner.pawcare.presentation.medications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignaherner.pawcare.data.local.WorkManagerHelper
import com.ignaherner.pawcare.data.repository.MedicationRepository
import com.ignaherner.pawcare.domain.model.Medication
import com.ignaherner.pawcare.domain.model.MedicationStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MedicationViewModel @Inject constructor(
    private val repository: MedicationRepository,
    private val workManagerHelper: WorkManagerHelper
) : ViewModel() {
    private val _uiState = MutableStateFlow<MedicationUiState>(MedicationUiState.Loading)
    val uiState: StateFlow<MedicationUiState> = _uiState.asStateFlow()

    fun loadMedications(petId: Long) {
        viewModelScope.launch {
            try {
                repository.getMedicationByPetId(petId)
                    .collect { medications ->
                        _uiState.value = if (medications.isEmpty()) {
                            MedicationUiState.Empty
                        } else {
                            MedicationUiState.Success(medications)
                        }
                    }
            } catch (e: Exception) {
                _uiState.value = MedicationUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun insertMedication(medication: Medication, petName: String) {
        viewModelScope.launch {
            try {
                repository.insertMedication(medication)
                if (medication.status == MedicationStatus.ACTIVO) {
                    workManagerHelper.programarRecordatorioMedicamento(medication, petName)
                }
            }catch (e: Exception) {
                _uiState.value = MedicationUiState.Error(e.message ?: "Error al guardar")
            }
        }
    }

    fun updateMedication(medication: Medication, petName: String) {
        viewModelScope.launch {
            try {
                repository.updateMedication(medication)
                if (medication.status == MedicationStatus.ACTIVO) {
                    workManagerHelper.programarRecordatorioMedicamento(medication, petName)
                } else {
                    workManagerHelper.cancelarRecordatorioMedicamento(medication.id)
                }
            }catch (e: Exception) {
                _uiState.value = MedicationUiState.Error(e.message ?: "Error al actualizar")
            }
        }
    }

    fun deleteMedication(medication: Medication) {
        viewModelScope.launch {
            try {
                repository.deleteMedication(medication)
                workManagerHelper.cancelarRecordatorioMedicamento(medication.id)
            }catch (e: Exception) {
                _uiState.value = MedicationUiState.Error(e.message ?: "Error al eliminar")
            }
        }
    }

}

sealed class MedicationUiState {
    object Loading: MedicationUiState()
    object Empty: MedicationUiState()
    data class Success(val medications: List<Medication>) : MedicationUiState()
    data class Error(val mensaje: String) : MedicationUiState()
}
