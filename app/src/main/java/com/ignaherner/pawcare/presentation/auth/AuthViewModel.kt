package com.ignaherner.pawcare.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignaherner.pawcare.data.remote.firestore.AuthRepository
import com.ignaherner.pawcare.data.remote.firestore.UserRepository
import com.ignaherner.pawcare.domain.model.Rol
import com.ignaherner.pawcare.domain.model.Veterinario
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

    private val _selectedRole = MutableStateFlow<String?>(null)
    val selectedRole: StateFlow<String?> = _selectedRole.asStateFlow()


    private var rolJob: Job? = null  // ← nuevo

    init {
        if (authRepository.isLoggedIn && _rol.value == null) {
            cargarRol()
        }
    }

    fun setSelectedRole(rol: String) {
        _selectedRole.value = rol
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

    fun login(email: String, password: String, isVetMode: Boolean = false) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            _rol.value = null

            val result = authRepository.login(email, password)
            if (result.isSuccess) {
                val rolResult = userRepository.obtenerRol()
                if (rolResult.isSuccess) {
                    val rolObtenido = rolResult.getOrNull()

                    // Verificar que el rol coincida con el modo
                    val modoEsperado = if (isVetMode) Rol.VETERINARIO else Rol.DUENO
                    if (rolObtenido != modoEsperado) {
                        authRepository.logout()
                        _authState.value = AuthState.Error(
                            if (isVetMode)
                                "Esta cuenta no es de veterinario. Usá el modo Dueño para ingresar."
                            else
                                "Esta cuenta es de veterinario. Cambiá al modo Veterinario para ingresar."
                        )
                        return@launch
                    }

                    _rol.value = rolObtenido
                    _authState.value = AuthState.Success
                } else {
                    authRepository.logout()
                    _authState.value = AuthState.Error("Error al obtener el rol")
                }
            } else {
                _authState.value = AuthState.Error(
                    result.exceptionOrNull()?.message ?: "Error al iniciar sesión"
                )
            }
        }
    }

    fun register(email: String, password: String, rol: Rol, nombre: String = "") {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            withContext(NonCancellable) {
                val result = authRepository.register(email, password)

                if (result.isSuccess) {
                    val uid = result.getOrNull()?.uid ?: ""
                    val rolResult = userRepository.guardarUsuario(rol, uid, nombre)

                    if (rolResult.isSuccess) {
                        cargarRol()
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
    fun registerVet(
        email: String,
        password: String,
        nombre: String,
        apellido: String,
        matricula: String,
        especialidad: String?,
        telefono: String = "",
        clinica: String? = null,
        ciudad: String? = null,
        direccion: String? = null
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            withContext(NonCancellable) {
                val result = authRepository.register(email, password)

                if (result.isSuccess) {
                    val uid = result.getOrNull()?.uid ?: ""

                    val rolResult = userRepository.guardarUsuario(Rol.VETERINARIO, uid)

                    val vet = Veterinario(
                        nombre = nombre,
                        apellido = apellido,
                        matricula = matricula,
                        especialidad = especialidad,
                        telefono = telefono,
                        clinica = clinica,
                        ciudad = ciudad,
                        direccion = direccion
                    )
                    val vetResult = userRepository.guardarVeterinario(vet)

                    if (rolResult.isSuccess && vetResult.isSuccess) {
                        cargarRol()
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
