package com.ignaherner.pawcare.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignaherner.pawcare.data.local.PawCareDatabase
import com.ignaherner.pawcare.data.local.SettingsDataStore
import com.ignaherner.pawcare.data.remote.firestore.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore,
    private val authRepository: AuthRepository,
    private val database: PawCareDatabase,
) : ViewModel() {

    val nombreVeterinario: StateFlow<String> = settingsDataStore.nombreVeterinario
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )


    fun guardarNombreVeterinario(nombre: String) {
        viewModelScope.launch {
            settingsDataStore.guardarNombreVeterinario(nombre)
        }
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            database.clearAllTables()
            authRepository.logout()
        }
    }
}