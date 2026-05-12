package com.ignaherner.mispatitas.presentation.owners

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignaherner.mispatitas.data.repository.OwnerRepository
import com.ignaherner.mispatitas.domain.model.Owner
import com.ignaherner.mispatitas.presentation.BaseViewModel
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
    private val repository: OwnerRepository
) : BaseViewModel() {

    private val _ownerState = MutableStateFlow<OwnerState>(OwnerState.Loading)
    val ownerState: StateFlow<OwnerState> = _ownerState.asStateFlow()

    private val _ownerExists = MutableStateFlow<Boolean?>(null)
    val ownerExists: StateFlow<Boolean?> = _ownerExists.asStateFlow()


    init {
        checkOwnerExists()
        loadOwner()
    }

    fun checkOwnerExists() {
        viewModelScope.launch {
            _ownerExists.value = repository.ownerExists()
        }
    }

    fun loadOwner() {
        viewModelScope.launch {
            _ownerState.value = OwnerState.Loading
            val owner = repository.getOwner()
            _ownerState.value = if (owner != null) OwnerState.Success(owner)
            else OwnerState.Empty
            _ownerExists.value = owner != null
        }
    }

    fun insertOwner(owner: Owner) {
        safeLaunch(onError = "Error al crear el perfil") {
            repository.insertOwner(owner)
            _ownerExists.value = true
            _ownerState.value = OwnerState.Success(owner)
            showSnackbar("Perfil creado")
        }
    }

    fun updateOwner(owner: Owner) {
        safeLaunch(onError = "Error al actualizar") {
            repository.updateOwner(owner)
            _ownerState.value = OwnerState.Success(owner)
            showSnackbar("Perfil actualizado")
        }
    }
}

sealed class OwnerState {
    object Loading: OwnerState ()
    object Empty: OwnerState ()
    data class Success(val owner: Owner) : OwnerState ()
    data class Error(val mensaje: String) : OwnerState ()
}
