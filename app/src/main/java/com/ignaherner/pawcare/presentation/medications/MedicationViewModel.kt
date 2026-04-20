package com.ignaherner.pawcare.presentation.medications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignaherner.pawcare.data.local.worker.WorkManagerHelper
import com.ignaherner.pawcare.data.remote.firestore.MedicationFirestoreRepository
import com.ignaherner.pawcare.data.repository.MedicationRepository
import com.ignaherner.pawcare.data.repository.PetRepository
import com.ignaherner.pawcare.domain.model.Medication
import com.ignaherner.pawcare.domain.model.MedicationStatus
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
class MedicationViewModel @Inject constructor(
    private val repository: MedicationRepository,
    private val firestoreRepository: MedicationFirestoreRepository,
    private val petRepository: PetRepository,
    private val workManagerHelper: WorkManagerHelper
) : BaseViewModel() {
    private val _uiState = MutableStateFlow<MedicationUiState>(MedicationUiState.Loading)
    val uiState: StateFlow<MedicationUiState> = _uiState.asStateFlow()

    private val _medicationDetailState = MutableStateFlow<MedicationDetailState>(MedicationDetailState.Loading)
    val medicationDetailState: StateFlow<MedicationDetailState> = _medicationDetailState

    fun loadMedications(petId: Long) {
        viewModelScope.launch {
            repository.getMedicationByPetId(petId).collect { medications ->
                _uiState.value = if (medications.isEmpty()) MedicationUiState.Empty
                else MedicationUiState.Success(medications)
            }
        }
    }

    fun loadMedicationById(id: Long) {
        viewModelScope.launch {
            val medication = repository.getMedicationById(id)
            _medicationDetailState.value = if (medication != null)
                MedicationDetailState.Success(medication)
            else
                MedicationDetailState.Error("Medicamento no encontrado")
        }
    }

    fun insertMedication(medication: Medication, petName: String) {
        safeLaunch(onError = "Error al guardar") {
            val id = repository.insertMedication(medication)
            val medicationConId = medication.copy(id = id)
            val pet = petRepository.getPetById(medicationConId.petId).firstOrNull()
            val petFirestoreId = pet?.firestoreId ?: ""
            if (petFirestoreId.isNotBlank()) {
                val result = firestoreRepository.guardarMedicamento(medicationConId, petFirestoreId)
                if (result.isSuccess) {
                    val firestoreId = result.getOrNull() ?: ""
                    repository.updateMedication(medicationConId.copy(firestoreId = firestoreId))
                }
            }
            if (medicationConId.status == MedicationStatus.ACTIVO) {
                if (!medicationConId.esUnicaDosis) {
                    workManagerHelper.programarRecordatorioMedicamento(medicationConId, petName)
                    workManagerHelper.programarFinMedicamento(medicationConId)
                    showSnackbar("Recordatorio cada ${medicationConId.intervaloHoras}h programado 💊")
                } else {
                    repository.updateMedication(medicationConId.copy(status = MedicationStatus.FINALIZADO))
                    showSnackbar("Medicamento de única dosis registrado 💊")
                }
            }
        }
    }

    fun updateMedication(medication: Medication, petName: String) {
        safeLaunch(onError = "Error al actualizar") {
            repository.updateMedication(medication)
            val pet = petRepository.getPetById(medication.petId).firstOrNull()
            val petFirestoreId = pet?.firestoreId ?: ""
            if (medication.firestoreId.isNotBlank() && petFirestoreId.isNotBlank()) {
                firestoreRepository.actualizarMedicamento(medication, petFirestoreId)
            }
            if (medication.status == MedicationStatus.ACTIVO) {
                workManagerHelper.programarRecordatorioMedicamento(medication, petName)
            } else {
                workManagerHelper.cancelarRecordatorioMedicamento(medication.id)
            }
        }
    }

    fun deleteMedication(medication: Medication) {
        safeLaunch(onError = "Error al eliminar") {
            workManagerHelper.cancelarRecordatorioMedicamento(medication.id)
            workManagerHelper.cancelarFinMedicamento(medication.id)
            repository.deleteMedication(medication)
            val pet = petRepository.getPetById(medication.petId).firstOrNull()
            val petFirestoreId = pet?.firestoreId ?: ""
            if (medication.firestoreId.isNotBlank() && petFirestoreId.isNotBlank()) {
                firestoreRepository.eliminarMedicamento(medication.firestoreId, petFirestoreId)
            }
            showSnackbar("${medication.nombre} eliminado")
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