package com.ignaherner.pawcare.presentation.appointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignaherner.pawcare.data.remote.firestore.AppointmentFirestoreRepository
import com.ignaherner.pawcare.data.repository.AppointmentRepository
import com.ignaherner.pawcare.data.repository.PetRepository
import com.ignaherner.pawcare.domain.model.Appointment
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
) : ViewModel(){
    private val _uiState = MutableStateFlow<AppointmentUiState>(AppointmentUiState.Loading)
    val uiState: StateFlow<AppointmentUiState> = _uiState.asStateFlow()

    private val _appointmentDetailState = MutableStateFlow<AppointmentDetailState>(AppointmentDetailState.Loading)
    val appointentDetailState: StateFlow<AppointmentDetailState> = _appointmentDetailState

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage = _snackbarMessage.asStateFlow()

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

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

    fun loadAppointmentById(id: Long) {
        viewModelScope.launch {
            try {
                val appointment = repository.getAppointmentById(id)
                _appointmentDetailState.value = if (appointment != null) {
                    AppointmentDetailState.Success(appointment)
                } else {
                    AppointmentDetailState.Error("Turno no encontrado")
                }
            }catch (e: Exception) {
                _appointmentDetailState.value = AppointmentDetailState.Error(e.message ?: "Error")
            }
        }
    }

    fun insertAppointment(appointment: Appointment) {
        viewModelScope.launch {
            try {
                val id = repository.insertAppointment(appointment)
                val appointmentConId = appointment.copy(id = id)

                withContext(NonCancellable){
                    val pet = petRepository.getPetById(appointmentConId.petId).firstOrNull()
                    val petFirestoreId = pet?.firestoreId ?: ""
                    if (petFirestoreId.isNotBlank()){
                        val firestoreResult = firestoreRepository.guardarTurno(
                            appointmentConId, petFirestoreId
                        )
                        if (firestoreResult.isSuccess){
                            val firestoreId = firestoreResult.getOrNull() ?: ""
                            repository.updateAppointment(appointmentConId.copy(firestoreId = firestoreId))
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = AppointmentUiState.Error(e.message ?: "Error al guardar")
            }
        }
    }

    fun updateAppointment(appointment: Appointment) {
        viewModelScope.launch {
            try {
                repository.updateAppointment(appointment)
                if (appointment.firestoreId.isNotBlank()){
                    val pet = petRepository.getPetById(appointment.petId).firstOrNull()
                    val petFirestoreId = pet?.firestoreId ?: ""
                    if (petFirestoreId.isNotBlank()){
                        firestoreRepository.actualizarTurno(appointment, petFirestoreId)
                    }
                }
            }catch (e: Exception) {
                _uiState.value = AppointmentUiState.Error(e.message ?: "Error al actualizar")
            }
        }
    }

    fun deleteAppointment(appointment: Appointment){
        viewModelScope.launch {
            try {
                repository.deleteAppointment(appointment)
                val pet = petRepository.getPetById(appointment.petId).firstOrNull()
                val petFirestoreId = pet?.firestoreId ?: ""
                if (appointment.firestoreId.isNotBlank() && petFirestoreId.isNotBlank()){
                    firestoreRepository.eliminarTurno(appointment.firestoreId, petFirestoreId)
                }
                _snackbarMessage.value = "${appointment.fecha} eliminada"
            } catch (e: Exception) {
                _snackbarMessage.value = "Error al eliminar"
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

sealed class AppointmentDetailState{
    object Loading: AppointmentDetailState()
    data class Success(val appointments: Appointment) : AppointmentDetailState()
    data class Error(val mensaje: String) : AppointmentDetailState()
}