package com.ignaherner.pawcare.presentation.vaccines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignaherner.pawcare.data.local.WorkManagerHelper
import com.ignaherner.pawcare.data.repository.VaccineRepository
import com.ignaherner.pawcare.domain.model.Vaccine
import com.ignaherner.pawcare.domain.model.VaccineStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VaccineViewModel @Inject constructor(
    private val repository: VaccineRepository,
    private val workManagerHelper: WorkManagerHelper
): ViewModel(){
    private val _uiState = MutableStateFlow<VaccineUiState>(VaccineUiState.Loading)
    val uiState: StateFlow<VaccineUiState> = _uiState.asStateFlow()

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

    fun insertVaccine(vaccine: Vaccine, petName: String) {
        viewModelScope.launch {
            try {
                val id = repository.insertVaccine(vaccine)
                val vaccineConId = vaccine.copy(id = id)
                if(vaccine.status is VaccineStatus.Aplicada && vaccine.proximaDosis != null) {
                    workManagerHelper.programarRecordatorioVacuna(vaccineConId, petName)
                }
            } catch (e: Exception) {
                _uiState.value = VaccineUiState.Error(e.message ?: "Error al guardar")
            }
        }
    }

    fun updateVaccine(vaccine: Vaccine, petName: String) {
        viewModelScope.launch {
            try {
                repository.updateVaccine(vaccine)
                if (vaccine.status is VaccineStatus.Aplicada && vaccine.proximaDosis != null) {
                    workManagerHelper.programarRecordatorioVacuna(vaccine, petName)
                }else {
                    workManagerHelper.cancelarRecordatorioVacuna(vaccine.id)
                }
            }catch (e: Exception) {
                _uiState.value = VaccineUiState.Error(e.message ?: "Error al actualizar")
            }
        }
    }

    fun deleteVaccine(vaccine: Vaccine) {
        viewModelScope.launch {
            try {
                workManagerHelper.cancelarRecordatorioVacuna(vaccine.id)
                repository.deleteVaccine(vaccine)
            }catch (e: Exception) {
                _uiState.value = VaccineUiState.Error(e.message ?: "Error al eliminar")
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