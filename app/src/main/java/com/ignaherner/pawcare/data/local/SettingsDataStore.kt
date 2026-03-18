package com.ignaherner.pawcare.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
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
        val NOMBRE_USUARIO = stringPreferencesKey("nombre_usuario")
    }

    // Leer nombre veterinario
    val nombreVeterinario: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[NOMBRE_VETERINARIO] ?: ""
        }

    // Leer nombre usuario
    val nombreUsuario: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[NOMBRE_USUARIO] ?: "Usuario"
        }

    // Guardar nombre veterinario
    suspend fun guardarNombreVeterinario(nombre: String) {
        context.dataStore.edit { preferences ->
            preferences[NOMBRE_VETERINARIO] = nombre
        }
    }

    // Guardar nombre usuario
    suspend fun guardarNombreUsuario(nombre: String) {
        context.dataStore.edit { preferences ->
            preferences[NOMBRE_USUARIO] = nombre
        }
    }
}