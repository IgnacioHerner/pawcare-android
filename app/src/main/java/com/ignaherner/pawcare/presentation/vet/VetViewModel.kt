package com.ignaherner.pawcare.presentation.vet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignaherner.pawcare.data.remote.firestore.AppointmentFirestoreRepository
import com.ignaherner.pawcare.data.remote.firestore.ConditionFirestoreRepository
import com.ignaherner.pawcare.data.remote.firestore.DewormingFirestoreRepository
import com.ignaherner.pawcare.data.remote.firestore.MedicationFirestoreRepository
import com.ignaherner.pawcare.data.remote.firestore.UserRepository
import com.ignaherner.pawcare.data.remote.firestore.VaccineFirestoreRepository
import com.ignaherner.pawcare.data.remote.firestore.WeightFirestoreRepository
import com.ignaherner.pawcare.data.repository.VetRepository
import com.ignaherner.pawcare.domain.model.MedicationStatus
import com.ignaherner.pawcare.domain.model.Pet
import com.ignaherner.pawcare.domain.model.VetPetSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VetViewModel @Inject constructor(
    private val vetRepository: VetRepository,
    private val vaccineFirestoreRepository: VaccineFirestoreRepository,
    private val medicationFirestoreRepository: MedicationFirestoreRepository,
    private val weightFirestoreRepository: WeightFirestoreRepository,
    private val appointmentFirestoreRepository: AppointmentFirestoreRepository,
    private val conditionFirestoreRepository: ConditionFirestoreRepository,
    private val dewormingFirestoreRepository: DewormingFirestoreRepository,
    private val userRepository: UserRepository
) : ViewModel(){

    private val _searchState = MutableStateFlow<VetSearchState>(VetSearchState.Idle)
    val searchState: StateFlow<VetSearchState> = _searchState.asStateFlow()

    private val _summaryState = MutableStateFlow<VetSummaryState>(VetSummaryState.Loading)
    val summaryState: StateFlow<VetSummaryState> = _summaryState.asStateFlow()
    fun buscarMascota(firestoreId: String) {
        viewModelScope.launch {
            _searchState.value = VetSearchState.Loading
            val result = vetRepository.buscarMascotaPorId(firestoreId)
            _searchState.value = if (result.isSuccess) {
                VetSearchState.Success(result.getOrNull()!!)
            } else {
                VetSearchState.Error(result.exceptionOrNull()?.message ?: "Mascota no encontrada")
            }
        }
    }

    fun cargarResumen(firestoreId: String) {
        viewModelScope.launch {
            _summaryState.value = VetSummaryState.Loading
            try {
                // Mascota
                val petResult = vetRepository.buscarMascotaPorId(firestoreId)
                val pet = petResult.getOrNull() ?: run {
                    _summaryState.value = VetSummaryState.Error("Mascota no encontrada")
                    return@launch
                }

                // Owner
                val owner = userRepository.obtenerOwnerPorId(pet.ownerId)

                // Subcolecciones
                val vacunas = vaccineFirestoreRepository
                    .obtenerVacunasPorMascota(firestoreId).getOrNull() ?: emptyList()
                val medicamentos = medicationFirestoreRepository
                    .obtenerMedicamentosPorMascota(firestoreId).getOrNull() ?: emptyList()
                val pesos = weightFirestoreRepository
                    .obtenerPesosPorMascota(firestoreId).getOrNull() ?: emptyList()
                val turnos = appointmentFirestoreRepository
                    .obtenerTurnosPorMascota(firestoreId).getOrNull() ?: emptyList()
                val condiciones = conditionFirestoreRepository
                    .obtenerCondicionesPorMascota(firestoreId).getOrNull() ?: emptyList()
                val desparasitaciones = dewormingFirestoreRepository
                    .obtenerDesparasitacionesPorMascota(firestoreId).getOrNull() ?: emptyList()

                val summary = VetPetSummary(
                    pet = pet,
                    owner = owner,
                    ultimaVacuna = vacunas.maxByOrNull { it.fecha ?: "" },
                    medicamentoActivo = medicamentos.firstOrNull {
                        it.status == MedicationStatus.ACTIVO
                    },
                    ultimoPeso = pesos.maxByOrNull { it.fecha },
                    ultimoTurno = turnos.maxByOrNull { it.fecha ?: "" },
                    ultimaDesparasitacion = desparasitaciones.maxByOrNull { it.fecha },
                    condiciones = condiciones
                )
                _summaryState.value = VetSummaryState.Success(summary)
            } catch (e: Exception) {
                _summaryState.value = VetSummaryState.Error(e.message ?: "Error")
            }
        }
    }

    fun resetSearch() {
        _searchState.value = VetSearchState.Idle
    }

}

sealed class VetSearchState {
    object Idle : VetSearchState()
    object Loading : VetSearchState()
    data class Success(val pet: Pet) : VetSearchState()
    data class Error(val mensaje: String) : VetSearchState()
}

sealed class VetSummaryState {
    object Loading : VetSummaryState()
    data class Success(val summary: VetPetSummary) : VetSummaryState()
    data class Error(val mensaje: String) : VetSummaryState()
}