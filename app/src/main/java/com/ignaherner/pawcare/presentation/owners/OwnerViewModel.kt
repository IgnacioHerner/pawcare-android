package com.ignaherner.pawcare.presentation.owners

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignaherner.pawcare.data.repository.OwnerRepository
import com.ignaherner.pawcare.domain.model.Owner
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OwnerViewModel @Inject constructor(
    private val repository: OwnerRepository
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
                val owner = repository.getOwner()
                _ownerExists.value = owner != null
            } catch (e: Exception) {
                _ownerExists.value = false
            }
        }
    }

    fun loadOwner() {
        viewModelScope.launch {
            try {
                val owner = repository.getOwner()
                _ownerState.value = if (owner != null) {
                    OwnerState.Success(owner)
                } else {
                    OwnerState.Empty
                }
            } catch (e: Exception) {
                _ownerState.value = OwnerState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun insertOwner(owner: Owner) {
        viewModelScope.launch {
            try {
                repository.insertOwner(owner)
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
                repository.updateOwner(owner)
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