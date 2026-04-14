package com.ignaherner.pawcare.data.repository

import androidx.compose.material3.darkColorScheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ignaherner.pawcare.domain.model.Rol
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun guardarUsuario(rol: Rol): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Result.failure(
                Exception("Usuario no encontrado")
            )
            val usuario = hashMapOf(
                "uid" to uid,
                "email" to (auth.currentUser?.email ?: ""),
                "rol" to rol.name,
                "fechaCreacion" to System.currentTimeMillis()
            )
            firestore.collection("users")
                .document(uid)
                .set(usuario)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerRol(): Result<Rol> {
        return try {
            val uid = auth.currentUser?.uid ?: return Result.failure(
                Exception("Usuario no autenticado")
            )
            val documento = firestore.collection("users")
                .document(uid)
                .get()
                .await()
            val rolString = documento.getString("rol") ?: return Result.failure(
                Exception("Rol no encontrado")
            )
            Result.success(Rol.valueOf(rolString))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}