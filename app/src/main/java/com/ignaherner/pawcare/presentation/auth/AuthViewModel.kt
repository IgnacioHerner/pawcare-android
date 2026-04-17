package com.ignaherner.pawcare.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignaherner.pawcare.data.repository.AuthRepository
import com.ignaherner.pawcare.data.repository.UserRepository
import com.ignaherner.pawcare.domain.model.Rol
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel(){
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _rol = MutableStateFlow<Rol?>(null)
    val rol: StateFlow<Rol?> = _rol.asStateFlow()

    private var rolJob: Job? = null  // ← nuevo

    init {
        if (authRepository.isLoggedIn && _rol.value == null) {
            cargarRol()
        }
    }

    fun cargarRol() {
        rolJob?.cancel()
        rolJob = viewModelScope.launch {
            val uid = authRepository.currentUser?.uid
            val result = userRepository.obtenerRol()
            // Solo actualizar si el uid no cambió durante la carga
            if (authRepository.currentUser?.uid == uid && result.isSuccess) {
                _rol.value = result.getOrNull()
            }
        }
    }

    val isLoggedIn: Boolean get() = authRepository.isLoggedIn

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            _rol.value = null  // ← limpiar rol anterior

            val result = authRepository.login(email, password)
            if (result.isSuccess) {
                cargarRol()
                _authState.value = AuthState.Success
            } else {
                _authState.value = AuthState.Error(
                    result.exceptionOrNull()?.message ?: "Error al iniciar sesión"
                )
            }
        }
    }

    fun register(email: String, password: String, rol: Rol) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            withContext(NonCancellable) {
                val result = authRepository.register(email, password)

                if (result.isSuccess) {
                    val uid = result.getOrNull()?.uid ?: ""
                    val rolResult = userRepository.guardarUsuario(rol, uid)

                    if (rolResult.isSuccess) {
                        cargarRol() // ← cargar el rol antes de navegar
                        _authState.value = AuthState.Success
                    } else {
                        _authState.value = AuthState.Error("Error al guardar el perfil")
                    }
                } else {
                    _authState.value = AuthState.Error(
                        result.exceptionOrNull()?.message ?: "Error al registrarse"
                    )
                }
            }
        }
    }

    fun logout() {
        rolJob?.cancel()
        _rol.value = null
        _authState.value = AuthState.Idle
        authRepository.logout()
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle: AuthState()
    object Loading: AuthState()
    object Success: AuthState()
    data class Error(val mensaje: String) : AuthState()
}
