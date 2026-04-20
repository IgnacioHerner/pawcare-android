package com.ignaherner.pawcare.presentation.vaccines

import androidx.lifecycle.viewModelScope
import com.ignaherner.pawcare.data.local.worker.WorkManagerHelper
import com.ignaherner.pawcare.data.repository.PetRepository
import com.ignaherner.pawcare.data.remote.firestore.VaccineFirestoreRepository
import com.ignaherner.pawcare.data.repository.VaccineRepository
import com.ignaherner.pawcare.domain.model.Vaccine
import com.ignaherner.pawcare.domain.model.VaccineStatus
import com.ignaherner.pawcare.domain.model.toFriendlyDate
import com.ignaherner.pawcare.presentation.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VaccineViewModel @Inject constructor(
    private val repository: VaccineRepository,
    private val firestoreRepository: VaccineFirestoreRepository,
    private val petRepository: PetRepository,
    private val workManagerHelper: WorkManagerHelper
): BaseViewModel(){
    private val _uiState = MutableStateFlow<VaccineUiState>(VaccineUiState.Loading)
    val uiState: StateFlow<VaccineUiState> = _uiState.asStateFlow()

    private val _vaccineDetailState = MutableStateFlow<VaccineDetailState>(VaccineDetailState.Loading)
    val vaccineDetailState: StateFlow<VaccineDetailState> = _vaccineDetailState.asStateFlow()

    fun loadVaccines(petId: Long) {
        viewModelScope.launch {
            repository.getVaccinesByPetId(petId).collect { vaccines ->
                _uiState.value = if (vaccines.isEmpty()) VaccineUiState.Empty
                else VaccineUiState.Success(vaccines)
            }
        }
    }

    fun loadVaccineById(id: Long) {
        viewModelScope.launch {
            val vaccine = repository.getVaccineById(id)
            _vaccineDetailState.value = if (vaccine != null)
                VaccineDetailState.Success(vaccine)
            else
                VaccineDetailState.Error("Vacuna no encontrada")
        }
    }

    fun insertVaccine(vaccine: Vaccine, petName: String) {
        safeLaunch(onError = "Error al guardar") {
            val id = repository.insertVaccine(vaccine)
            val vaccineConId = vaccine.copy(id = id)
            val pet = petRepository.getPetById(vaccine.petId).firstOrNull()
            val petFirestoreId = pet?.firestoreId ?: ""
            if (petFirestoreId.isNotBlank()) {
                val result = firestoreRepository.guardarVacuna(vaccineConId, petFirestoreId)
                if (result.isSuccess) {
                    val firestoreId = result.getOrNull() ?: ""
                    repository.updateVaccine(vaccineConId.copy(firestoreId = firestoreId))
                }
            }
            if (vaccineConId.status is VaccineStatus.Aplicada && vaccineConId.proximaDosis != null) {
                workManagerHelper.programarRecordatorioVacuna(vaccineConId, petName)
                showSnackbar("Próxima dosis: ${vaccineConId.proximaDosis.toFriendlyDate()} 💉")
            }
        }
    }

    fun updateVaccine(vaccine: Vaccine, petName: String) {
        safeLaunch(onError = "Error al actualizar") {
            repository.updateVaccine(vaccine)
            val pet = petRepository.getPetById(vaccine.petId).firstOrNull()
            val petFirestoreId = pet?.firestoreId ?: ""
            if (vaccine.firestoreId.isNotBlank() && petFirestoreId.isNotBlank()) {
                firestoreRepository.actualizarVacuna(vaccine, petFirestoreId)
            }
            if (vaccine.status is VaccineStatus.Aplicada && vaccine.proximaDosis != null) {
                workManagerHelper.programarRecordatorioVacuna(vaccine, petName)
            } else {
                workManagerHelper.cancelarRecordatorioVacuna(vaccine.id)
            }
        }
    }

    fun deleteVaccine(vaccine: Vaccine) {
        safeLaunch(onError = "Error al eliminar") {
            workManagerHelper.cancelarRecordatorioVacuna(vaccine.id)
            repository.deleteVaccine(vaccine)
            val pet = petRepository.getPetById(vaccine.petId).firstOrNull()
            val petFirestoreId = pet?.firestoreId ?: ""
            if (vaccine.firestoreId.isNotBlank() && petFirestoreId.isNotBlank()) {
                firestoreRepository.eliminarVacuna(vaccine.firestoreId, petFirestoreId)
            }
            showSnackbar("${vaccine.nombre} eliminada")
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