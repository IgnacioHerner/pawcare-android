package com.ignaherner.mispatitas.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ignaherner.mispatitas.data.local.PawCareDatabase
import com.ignaherner.mispatitas.data.local.SettingsDataStore
import com.ignaherner.mispatitas.data.remote.firestore.AuthRepository
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

    val notificationsEnabled: StateFlow<Boolean> = settingsDataStore.notificationsEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setNotificationsEnabled(enabled)
        }
    }

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

    val onboardingCompleted: StateFlow<Boolean> = settingsDataStore.onboardingCompleted
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun completeOnboarding() {
        viewModelScope.launch {
            settingsDataStore.setOnboardingCompleted()
        }
    }

    val recentSearches: StateFlow<List<String>> = settingsDataStore.recentSearches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
