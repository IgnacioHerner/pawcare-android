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

    private val _medicationDetailState = MutableStateFlow<MedicationDetailState>(MedicationDetailState.Loading)
    val medicationDetailState: StateFlow<MedicationDetailState> = _medicationDetailState

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage = _snackbarMessage.asStateFlow()

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

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

    fun loadMedicationById(id: Long) {
        viewModelScope.launch {
            try {
                val medication = repository.getMedicationById(id)
                _medicationDetailState.value = if (medication != null) {
                    MedicationDetailState.Success(medication)
                } else {
                    MedicationDetailState.Error("Medicamento no encontrado")
                }
            } catch (e: Exception) {
                _medicationDetailState.value = MedicationDetailState.Error(e.message ?: "Error")
            }
        }
    }

    fun insertMedication(medication: Medication, petName: String) {
        viewModelScope.launch {
            try {
                val id = repository.insertMedication(medication)
                val medicationConId = medication.copy(id = id)

                if (medication.status == MedicationStatus.ACTIVO) {
                    if (!medicationConId.esUnicaDosis) {
                        // Recordatorio periodico
                        workManagerHelper.programarRecordatorioMedicamento(
                            medicationConId, petName
                        )
                        // Finalizar automaticamente al terminar
                        workManagerHelper.programarFinMedicamento(medicationConId)
                        _snackbarMessage.value =
                            "Recordatorio cada ${medicationConId.intervaloHoras}h programado \uD83D\uDC8A"
                    } else {
                        // Unica dosis - finalizar inmediatamente despues
                        _snackbarMessage.value = "Medicamento de única dosis registrado \uD83D\uDC8A"
                        // Cambiar a FINALIZADO directamente
                        val medicationFinalizada = medicationConId.copy(
                            status = MedicationStatus.FINALIZADO
                        )
                        repository.updateMedication(medicationFinalizada)
                    }
                }
            }catch (e: Exception) {
                _snackbarMessage.value = "Error al guardar"
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
                workManagerHelper.cancelarRecordatorioMedicamento(medication.id)
                workManagerHelper.cancelarFinMedicamento(medication.id)
                repository.deleteMedication(medication)
                _snackbarMessage.value = "${medication.nombre} eliminada"
            }catch (e: Exception) {
                _snackbarMessage.value = "Error al eliminar"
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

sealed class MedicationDetailState {
    object Loading: MedicationDetailState()
    data class Success(val medication: Medication) : MedicationDetailState()
    data class Error(val mensaje: String) : MedicationDetailState()
}