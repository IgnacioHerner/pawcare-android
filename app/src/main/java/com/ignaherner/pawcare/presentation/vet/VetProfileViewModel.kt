package com.ignaherner.pawcare.presentation.vet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignaherner.pawcare.data.remote.firestore.UserRepository
import com.ignaherner.pawcare.domain.model.Veterinario
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class VetProfileViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _vetState = MutableStateFlow<VetState>(VetState.Loading)
    val vetState: StateFlow<VetState> = _vetState.asStateFlow()

    private val _vetExists = MutableStateFlow<Boolean?>(null)
    val vetExists: StateFlow<Boolean?> = _vetExists.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage = _snackbarMessage.asStateFlow()

    init {
        checkVetExists()
        loadVeterinario()
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun checkVetExists() {
        viewModelScope.launch {
            // Esperar que Firebase Auth esté listo
            delay(500)
            android.util.Log.d("VetDebug", "Chequeando si existe veterinario...")
            val exists = repository.vetExists()
            android.util.Log.d("VetDebug", "vetExists: $exists")
            _vetExists.value = exists
        }
    }

    fun loadVeterinario() {
        viewModelScope.launch {
            _vetState.value = VetState.Loading
            val vet = repository.getVeterinario()
            _vetState.value = if (vet != null) {
                VetState.Success(vet)
            } else {
                VetState.Empty
            }
            _vetExists.value = vet != null
        }
    }

    fun guardarVeterinario(vet: Veterinario) {
        viewModelScope.launch {
            try {
                withContext(NonCancellable) {
                    repository.guardarVeterinario(vet)
                }
                _vetExists.value = true
                _vetState.value = VetState.Success(vet)
                _snackbarMessage.value = "Perfil guardado ✅"
            } catch (e: Exception) {
                _snackbarMessage.value = "Error al guardar"
            }
        }
    }
}

sealed class VetState {
    object Loading : VetState()
    object Empty : VetState()
    data class Success(val vet: Veterinario) : VetState()
    data class Error(val mensaje: String) : VetState()
}