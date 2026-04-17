package com.ignaherner.pawcare.presentation.owners

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignaherner.pawcare.data.repository.OwnerRepository
import com.ignaherner.pawcare.data.repository.UserRepository
import com.ignaherner.pawcare.domain.model.Owner
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class OwnerViewModel @Inject constructor(
    private val repository: OwnerRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _ownerState = MutableStateFlow<OwnerState>(OwnerState.Loading)
    val ownerState: StateFlow<OwnerState> = _ownerState.asStateFlow()

    private val _ownerExists = MutableStateFlow<Boolean?>(null)
    val ownerExists: StateFlow<Boolean?> = _ownerExists.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage = _snackbarMessage.asStateFlow()

    init {
        checkOwnerExists()
        loadOwner()
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun checkOwnerExists() {
        viewModelScope.launch {
            try {
                repository.getOwner()
                    .collect { owner ->
                        _ownerExists.value = owner != null
                    }
            } catch (e: Exception) {
                _ownerExists.value = false
            }
        }
    }

    suspend fun sincronizarOwner() {
        try {
            val result = userRepository.obtenerPerfilDueno()
            if (result.isSuccess) {
                result.getOrNull()?.let { owner ->
                    repository.insertOwner(owner)
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("OwnerDebug", "Error: ${e.message}")
        }
    }

    fun loadOwner() {
        viewModelScope.launch {
            try {
                sincronizarOwner()

                repository.getOwner()
                    .collect { owner ->
                        _ownerState.value = if (owner != null) {
                            OwnerState.Success(owner)
                        } else {
                            OwnerState.Empty
                        }
                    }
            } catch (e: Exception) {
                _ownerState.value = OwnerState.Error(e.message ?: "Error")
            }
        }
    }

    fun insertOwner(owner: Owner) {
        viewModelScope.launch {
            try {
                withContext(NonCancellable){
                    repository.insertOwner(owner)
                    userRepository.guardarPerfilDueno(owner)
                }
                _ownerExists.value = true
                _ownerState.value = OwnerState.Success(owner)
                _snackbarMessage.value = "Perfil creado ✅"
            } catch (e: Exception) {
                _snackbarMessage.value = "Error al crear el perfil"
            }
        }
    }

    fun updateOwner(owner: Owner) {
        viewModelScope.launch {
            try {
                withContext(NonCancellable){
                    repository.updateOwner(owner)
                    userRepository.guardarPerfilDueno(owner)
                }
                _ownerState.value = OwnerState.Success(owner)
                _snackbarMessage.value = "Perfil actualizado ✅"
            } catch (e: Exception) {
                _snackbarMessage.value = "Error al actualizar"
            }
        }
    }
}

sealed class OwnerState {
    object Loading: OwnerState ()
    object Empty: OwnerState ()
    data class Success(val owner: Owner) : OwnerState ()
    data class Error(val mensaje: String) : OwnerState ()
}