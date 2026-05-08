package com.ignaherner.pawcare.presentation.appointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignaherner.pawcare.data.remote.firestore.AppointmentFirestoreRepository
import com.ignaherner.pawcare.data.repository.AppointmentRepository
import com.ignaherner.pawcare.data.repository.PetRepository
import com.ignaherner.pawcare.domain.model.Appointment
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
class AppointmentViewModel @Inject constructor(
    private val repository: AppointmentRepository,
    private val firestoreRepository: AppointmentFirestoreRepository,
    private val petRepository: PetRepository
) : BaseViewModel(){
    private val _uiState = MutableStateFlow<AppointmentUiState>(AppointmentUiState.Loading)
    val uiState: StateFlow<AppointmentUiState> = _uiState.asStateFlow()

    private val _appointmentDetailState = MutableStateFlow<AppointmentDetailState>(AppointmentDetailState.Loading)
    val appointentDetailState: StateFlow<AppointmentDetailState> = _appointmentDetailState

    fun loadAppointments(petId: Long) {
        viewModelScope.launch {
            repository.getAppointmentsByPetId(petId).collect { appointments ->
                _uiState.value = if (appointments.isEmpty()) AppointmentUiState.Empty
                else AppointmentUiState.Success(appointments)
            }
        }
    }

    fun loadAppointmentById(id: Long) {
        viewModelScope.launch {
            val appointment = repository.getAppointmentById(id)
            _appointmentDetailState.value = if (appointment != null)
                AppointmentDetailState.Success(appointment)
            else
                AppointmentDetailState.Error("Turno no encontrado")
        }
    }

    fun insertAppointment(appointment: Appointment) {
        safeLaunch(onError = "Error al guardar") {
            val id = repository.insertAppointment(appointment)
            val appointmentConId = appointment.copy(id = id)
            val pet = petRepository.getPetById(appointment.petId).firstOrNull()
            val petFirestoreId = pet?.firestoreId ?: ""
            if (petFirestoreId.isNotBlank()) {
                val result = firestoreRepository.guardarTurno(appointmentConId, petFirestoreId)
                if (result.isSuccess) {
                    val firestoreId = result.getOrNull() ?: ""
                    repository.updateAppointment(appointmentConId.copy(firestoreId = firestoreId))
                }
            }
            showSnackbar("Turno guardado")
        }
    }

    fun updateAppointment(appointment: Appointment) {
        safeLaunch(onError = "Error al actualizar") {
            repository.updateAppointment(appointment)
            val pet = petRepository.getPetById(appointment.petId).firstOrNull()
            val petFirestoreId = pet?.firestoreId ?: ""
            if (appointment.firestoreId.isNotBlank() && petFirestoreId.isNotBlank()) {
                firestoreRepository.actualizarTurno(appointment, petFirestoreId)
            }
        }
    }

    fun deleteAppointment(appointment: Appointment) {
        safeLaunch(onError = "Error al eliminar") {
            repository.deleteAppointment(appointment)
            val pet = petRepository.getPetById(appointment.petId).firstOrNull()
            val petFirestoreId = pet?.firestoreId ?: ""
            if (appointment.firestoreId.isNotBlank() && petFirestoreId.isNotBlank()) {
                firestoreRepository.eliminarTurno(appointment.firestoreId, petFirestoreId)
            }
            showSnackbar("Turno eliminado")
        }
    }

}

sealed class AppointmentUiState{
    object Loading: AppointmentUiState()
    object Empty: AppointmentUiState()
    data class Success(val appointments: List<Appointment>) : AppointmentUiState()
    data class Error(val mensaje: String) : AppointmentUiState()
}

sealed class AppointmentDetailState{
    object Loading: AppointmentDetailState()
    data class Success(val appointments: Appointment) : AppointmentDetailState()
    data class Error(val mensaje: String) : AppointmentDetailState()
}