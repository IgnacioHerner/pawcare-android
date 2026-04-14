package com.ignaherner.pawcare.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignaherner.pawcare.data.repository.AuthRepository
import com.ignaherner.pawcare.data.repository.UserRepository
import com.ignaherner.pawcare.domain.model.Rol
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel(){
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    val isLoggedIn: Boolean get() = authRepository.isLoggedIn

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.login(email, password)
            _authState.value = if (result.isSuccess) {
                AuthState.Success
            } else {
                AuthState.Error(result.exceptionOrNull()?.message ?: "Error al iniciar session")
            }
        }
    }

    fun register(email: String, password: String, rol: Rol) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val result = authRepository.register(email, password)

            if(result.isSuccess) {
                val rolResult = userRepository.guardarUsuario(rol)

                _authState.value = if (rolResult.isSuccess) {
                    AuthState.Success
                } else {
                    AuthState.Error("Error al guardar el perfil")
                }
            } else {
                _authState.value = AuthState.Error(
                    result.exceptionOrNull()?.message ?: "Error al registrarse"
                )
            }
        }
    }

    fun logout(){
        authRepository.logout()
        _authState.value = AuthState.Idle
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
