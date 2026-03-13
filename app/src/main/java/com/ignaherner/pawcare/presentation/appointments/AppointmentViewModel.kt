package com.ignaherner.pawcare.presentation.appointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignaherner.pawcare.data.repository.AppointmentRepository
import com.ignaherner.pawcare.domain.model.Appointment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppointmentViewModel @Inject constructor(
    private val repository: AppointmentRepository
) : ViewModel(){
    private val _uiState = MutableStateFlow<AppointmentUiState>(AppointmentUiState.Loading)
    val uiState: StateFlow<AppointmentUiState> = _uiState.asStateFlow()

    fun loadAppointments(petId: Long) {
        viewModelScope.launch {
            try {
                repository.getAppointmentsByPetId(petId)
                    .collect { appointments ->
                        _uiState.value = if (appointments.isEmpty()) {
                            AppointmentUiState.Empty
                        } else {
                            AppointmentUiState.Success(appointments)
                        }
                    }
            } catch (e: Exception) {
                _uiState.value = AppointmentUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun insertAppointment(appointment: Appointment) {
        viewModelScope.launch {
            try {
                repository.insertAppointment(appointment)
            } catch (e: Exception) {
                _uiState.value = AppointmentUiState.Error(e.message ?: "Error al guardar")
            }
        }
    }

    fun updateAppointment(appointment: Appointment) {
        viewModelScope.launch {
            try {
                repository.updateAppointment(appointment)
            }catch (e: Exception) {
                _uiState.value = AppointmentUiState.Error(e.message ?: "Error al actualizar")
            }
        }
    }

    fun deleteAppointment(appointment: Appointment){
        viewModelScope.launch {
            try {
                repository.deleteAppointment(appointment)
            } catch (e: Exception) {
                _uiState.value = AppointmentUiState.Error(e.message ?: "Error al eliminar")
            }
        }
    }

}

sealed class AppointmentUiState{
    object Loading: AppointmentUiState()
    object Empty: AppointmentUiState()
    data class Success(val appointments: List<Appointment>) : AppointmentUiState()
    data class Error(val mensaje: String) : AppointmentUiState()
}