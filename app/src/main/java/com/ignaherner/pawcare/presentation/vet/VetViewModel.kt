package com.ignaherner.pawcare.presentation.vet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignaherner.pawcare.data.repository.VetRepository
import com.ignaherner.pawcare.domain.model.Pet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VetViewModel @Inject constructor(
    private val vetRepository: VetRepository
) : ViewModel(){

    private val _searchState = MutableStateFlow<VetSearchState>(VetSearchState.Idle)
    val searchState: StateFlow<VetSearchState> = _searchState.asStateFlow()

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