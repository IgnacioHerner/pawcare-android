package com.ignaherner.pawcare.presentation.vaccines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignaherner.pawcare.data.local.worker.WorkManagerHelper
import com.ignaherner.pawcare.data.repository.PetRepository
import com.ignaherner.pawcare.data.remote.firestore.VaccineFirestoreRepository
import com.ignaherner.pawcare.data.repository.VaccineRepository
import com.ignaherner.pawcare.domain.model.Vaccine
import com.ignaherner.pawcare.domain.model.VaccineStatus
import com.ignaherner.pawcare.domain.model.toFriendlyDate
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
class VaccineViewModel @Inject constructor(
    private val repository: VaccineRepository,
    private val firestoreRepository: VaccineFirestoreRepository,
    private val petRepository: PetRepository,
    private val workManagerHelper: WorkManagerHelper
): ViewModel(){
    private val _uiState = MutableStateFlow<VaccineUiState>(VaccineUiState.Loading)
    val uiState: StateFlow<VaccineUiState> = _uiState.asStateFlow()

    private val _vaccineDetailState = MutableStateFlow< VaccineDetailState>(VaccineDetailState.Loading)
    val vaccineDetailState: StateFlow<VaccineDetailState> = _vaccineDetailState.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage = _snackbarMessage.asStateFlow()

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun loadVaccines(petId: Long) {
        viewModelScope.launch {
            try {
                repository.getVaccinesByPetId(petId)
                    .collect { vaccines ->
                        _uiState.value = if ( vaccines.isEmpty()) {
                            VaccineUiState.Empty
                        } else {
                            VaccineUiState.Success(vaccines)
                        }
                    }
            } catch (e: Exception) {
                _uiState.value = VaccineUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun loadVaccineById(id: Long) {
        viewModelScope.launch {
            try {
                val vaccine = repository.getVaccineById(id)
                _vaccineDetailState.value = if (vaccine != null) {
                    VaccineDetailState.Success(vaccine)
                } else {
                    VaccineDetailState.Error("Vacuna no encontrada")
                }
            } catch (e: Exception) {
                _vaccineDetailState.value = VaccineDetailState.Error(e.message ?: "Error")
            }
        }
    }

    fun insertVaccine(vaccine: Vaccine, petName: String) {
        viewModelScope.launch {
            try {
                val pet = petRepository.getPetById(vaccine.petId).firstOrNull()
                val petFirestoreId = pet?.firestoreId ?: ""

                val id = repository.insertVaccine(vaccine)
                val vaccineConId = vaccine.copy(id = id)

                // Sincronizar con Firestore
                withContext(NonCancellable){
                    if (petFirestoreId.isNotBlank()) {
                        val firestoreResult = firestoreRepository.guardarVacuna(
                            vaccineConId, petFirestoreId
                        )
                        if (firestoreResult.isSuccess) {
                            val firestoreId = firestoreResult.getOrNull() ?: ""
                            repository.updateVaccine(vaccineConId.copy(firestoreId = firestoreId))
                        }
                    }
                }

                if(vaccine.status is VaccineStatus.Aplicada && vaccine.proximaDosis != null) {
                    workManagerHelper.programarRecordatorioVacuna(vaccineConId, petName)
                    _snackbarMessage.value = "Próxima dosis: ${vaccineConId.proximaDosis?.toFriendlyDate()} 💉"
                    android.util.Log.d("PawCare", "Snackbar message set: ${_snackbarMessage.value}")
                }
            } catch (e: Exception) {
                _snackbarMessage.value = "Error al guardar"
            }
        }
    }

    fun updateVaccine(vaccine: Vaccine, petName: String) {
        viewModelScope.launch {
            try {
                repository.updateVaccine(vaccine)
                if (vaccine.firestoreId.isNotBlank()) {
                    // Obtener petFirestoreId desde Room
                    val pet = petRepository.getPetById(vaccine.petId).firstOrNull()
                    val petFirestoreId = pet?.firestoreId ?: ""
                    if (petFirestoreId.isNotBlank()) {
                        firestoreRepository.actualizarVacuna(vaccine, petFirestoreId)
                    }
                }
                if (vaccine.status is VaccineStatus.Aplicada && vaccine.proximaDosis != null) {
                    workManagerHelper.programarRecordatorioVacuna(vaccine, petName)
                } else {
                    workManagerHelper.cancelarRecordatorioVacuna(vaccine.id)
                }
            } catch (e: Exception) {
                _snackbarMessage.value = "Error al actualizar"
            }
        }
    }

    fun deleteVaccine(vaccine: Vaccine) {
        viewModelScope.launch {
            try {
                workManagerHelper.cancelarRecordatorioVacuna(vaccine.id)
                repository.deleteVaccine(vaccine)
                if (vaccine.firestoreId.isNotBlank()) {
                    val pet = petRepository.getPetById(vaccine.petId).firstOrNull()
                    val petFirestoreId = pet?.firestoreId ?: ""
                    if (petFirestoreId.isNotBlank()) {
                        firestoreRepository.eliminarVacuna(vaccine.firestoreId, petFirestoreId)
                    }
                }
                _snackbarMessage.value = "${vaccine.nombre} eliminada"
            } catch (e: Exception) {
                _snackbarMessage.value = "Error al eliminar"
            }
        }
    }


}

sealed class VaccineUiState{
    object Loading: VaccineUiState()
    object Empty: VaccineUiState()
    data class Success(val vaccines: List<Vaccine>) : VaccineUiState()
    data class Error(val mensaje: String) : VaccineUiState()
}

sealed class VaccineDetailState{
    object Loading : VaccineDetailState()
    data class Success(val vaccine: Vaccine) : VaccineDetailState()
    data class Error(val mensaje: String) : VaccineDetailState()
}