package com.ignaherner.pawcare.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Extension para crear el DataStore
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        val NOMBRE_VETERINARIO = stringPreferencesKey("nombre_veterinario")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val RECENT_SEARCHES = stringPreferencesKey("recent_searches")

    }

    val notificationsEnabled: Flow<Boolean> = context.dataStore.data // ← context.dataStore
        .map { preferences ->
            preferences[NOTIFICATIONS_ENABLED] ?: true
        }


    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED] = enabled
        }
    }

    // Leer nombre veterinario
    val nombreVeterinario: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[NOMBRE_VETERINARIO] ?: ""
        }


    // Guardar nombre veterinario
    suspend fun guardarNombreVeterinario(nombre: String) {
        context.dataStore.edit { preferences ->
            preferences[NOMBRE_VETERINARIO] = nombre
        }
    }

    val onboardingCompleted: Flow<Boolean> = context.dataStore.data.map {
        it[ONBOARDING_COMPLETED] ?: false
    }
    suspend fun setOnboardingCompleted() {
        context.dataStore.edit { it[ONBOARDING_COMPLETED] = true }
    }

    // Consultas recientes del vet
    val recentSearches: Flow<List<String>> = context.dataStore.data.map { prefs ->
        val json = prefs[RECENT_SEARCHES] ?: "[]"
        try {
            json.removeSurrounding("[", "]")
                .split(",")
                .map { it.trim().removeSurrounding("\"") }
                .filter { it.isNotBlank() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addRecentSearch(codigo: String) {
        context.dataStore.edit { prefs ->
            val current = (prefs[RECENT_SEARCHES] ?: "[]")
                .removeSurrounding("[", "]")
                .split(",")
                .map { it.trim().removeSurrounding("\"") }
                .filter { it.isNotBlank() }
                .toMutableList()

            // Sacar si ya existe y agregar al principio
            current.remove(codigo)
            current.add(0, codigo)

            // Máximo 10 recientes
            val limited = current.take(10)
            prefs[RECENT_SEARCHES] = "[${limited.joinToString(",") { "\"$it\"" }}]"
        }
    }

}