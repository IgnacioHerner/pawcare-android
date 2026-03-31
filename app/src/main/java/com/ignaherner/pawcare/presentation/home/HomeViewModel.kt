package com.ignaherner.pawcare.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignaherner.pawcare.data.repository.MedicationRepository
import com.ignaherner.pawcare.data.repository.PetRepository
import com.ignaherner.pawcare.data.repository.VaccineRepository
import com.ignaherner.pawcare.data.repository.WeightRepository
import com.ignaherner.pawcare.domain.model.MedicationStatus
import com.ignaherner.pawcare.domain.model.Pet
import com.ignaherner.pawcare.domain.model.PetSummary
import com.ignaherner.pawcare.presentation.medications.MedicationViewModel
import com.ignaherner.pawcare.presentation.weight.WeightViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val petRepository: PetRepository,
    private val vaccineRepository: VaccineRepository,
    private val medicationRepository: MedicationRepository,
    private val weightRepository: WeightRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHome()
    }

    private fun loadHome() {
        viewModelScope.launch {
            try {
                petRepository.getAllPets()
                    .collect { pets ->
                        if(pets.isEmpty()) {
                            _uiState.value = HomeUiState.Empty
                            return@collect
                        }
                        val summaries = pets.map { pet ->
                            val vacunas = vaccineRepository
                                .getVaccinesByPetId(pet.id)
                                .firstOrNull() ?: emptyList()

                            val medicamentos = medicationRepository
                                .getMedicationByPetId(pet.id)
                                .firstOrNull() ?: emptyList()

                            val pesos = weightRepository
                                .getWeightByPetId(pet.id)
                                .firstOrNull() ?: emptyList()

                            PetSummary(
                                pet = pet,
                                proximaVacuna = vacunas
                                    .filter { it.proximaDosis != null }
                                    .minByOrNull { it.proximaDosis!! },
                                medicamentoActivo = medicamentos
                                    .firstOrNull { it.status == MedicationStatus.ACTIVO },
                                ultimoPeso = pesos.firstOrNull()
                            )
                        }
                        _uiState.value = HomeUiState.Success(summaries)
                    }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun deletePet(pet: Pet){
        viewModelScope.launch {
            try {
                petRepository.deletePet(pet)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Error al eliminar")
            }
        }
    }
}

sealed class HomeUiState {
    object Loading: HomeUiState()
    object Empty: HomeUiState()
    data class Success(val summaries: List<PetSummary>) : HomeUiState()
    data class Error(val mensaje: String) : HomeUiState()
}
