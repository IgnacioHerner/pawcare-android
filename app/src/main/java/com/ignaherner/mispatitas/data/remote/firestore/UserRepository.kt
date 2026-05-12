package com.ignaherner.mispatitas.data.remote.firestore

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.ignaherner.mispatitas.domain.model.Owner
import com.ignaherner.mispatitas.domain.model.Rol
import com.ignaherner.mispatitas.domain.model.Veterinario
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // ═══════════════════════════════════════════════════════════
    // DUEÑOS — colección users/
    // ═══════════════════════════════════════════════════════════

    suspend fun guardarUsuario(rol: Rol, uid: String, nombre: String = ""): Result<Unit> {
        return try {
            if (rol == Rol.VETERINARIO) {
                // Solo guardar referencia mínima en users/
                val userData = hashMapOf(
                    "uid" to uid,
                    "email" to (auth.currentUser?.email ?: ""),
                    "rol" to rol.name,
                    "fechaCreacion" to System.currentTimeMillis()
                )
                firestore.collection("users")
                    .document(uid)
                    .set(userData)
                    .await()
            } else {
                val userData = hashMapOf(
                    "uid" to uid,
                    "email" to (auth.currentUser?.email ?: ""),
                    "rol" to rol.name,
                    "nombre" to nombre,
                    "fechaCreacion" to System.currentTimeMillis()
                )
                firestore.collection("users")
                    .document(uid)
                    .set(userData)
                    .await()
            }
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

            // Primero buscar en users/
            val userDoc = firestore.collection("users")
                .document(uid)
                .get()
                .await()

            if (userDoc.exists()) {
                val rolString = userDoc.getString("rol") ?: return Result.failure(
                    Exception("Rol no encontrado")
                )
                return Result.success(Rol.valueOf(rolString))
            }

            // Si no está en users/, buscar en veterinarios/
            val vetDoc = firestore.collection("veterinarios")
                .document(uid)
                .get()
                .await()

            if (vetDoc.exists()) {
                return Result.success(Rol.VETERINARIO)
            }

            Result.failure(Exception("Usuario no encontrado"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerOwnerPorId(ownerId: String): Owner? {
        return try {
            val documento = firestore.collection("users")
                .document(ownerId)
                .get()
                .await()

            if (!documento.exists()) return null

            Owner(
                id = 0L,
                nombre = documento.getString("nombre") ?: "",
                apellido = documento.getString("apellido") ?: "",
                telefono = documento.getString("telefono") ?: "",
                email = documento.getString("email"),
                ciudad = documento.getString("ciudad") ?: "",
                direccion = documento.getString("direccion"),
                fotoUri = documento.getString("fotoUri")
            )
        } catch (e: Exception) {
            null
        }
    }

    // ═══════════════════════════════════════════════════════════
    // VETERINARIOS — colección veterinarios/
    // ═══════════════════════════════════════════════════════════

    suspend fun guardarVeterinario(vet: Veterinario): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Result.failure(
                Exception("Usuario no autenticado")
            )
            val vetData = hashMapOf(
                "uid" to uid,
                "email" to (auth.currentUser?.email ?: ""),
                "nombre" to vet.nombre,
                "apellido" to vet.apellido,
                "matricula" to vet.matricula,
                "especialidad" to vet.especialidad,
                "telefono" to vet.telefono,
                "clinica" to vet.clinica,
                "direccion" to vet.direccion,
                "ciudad" to vet.ciudad,
                "fotoUri" to vet.fotoUri,
                "fechaCreacion" to System.currentTimeMillis()
            )
            firestore.collection("veterinarios")
                .document(uid)
                .set(vetData, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getVeterinario(): Veterinario? {
        return try {
            val uid = auth.currentUser?.uid ?: return null
            val documento = firestore.collection("veterinarios")
                .document(uid)
                .get()
                .await()

            if (!documento.exists()) return null

            Veterinario(
                id = uid,
                nombre = documento.getString("nombre") ?: "",
                apellido = documento.getString("apellido") ?: "",
                matricula = documento.getString("matricula") ?: "",
                especialidad = documento.getString("especialidad"),
                telefono = documento.getString("telefono") ?: "",
                clinica = documento.getString("clinica"),
                direccion = documento.getString("direccion"),
                ciudad = documento.getString("ciudad"),
                fotoUri = documento.getString("fotoUri")
            )
        } catch (e: Exception) {
            null
        }
    }

    suspend fun guardarUsuarioCompleto(
        rol: Rol,
        uid: String,
        nombre: String,
        apellido: String,
        telefono: String,
        email: String,
        ciudad: String
    ): Result<Unit> {
        return try {
            val userData = hashMapOf(
                "uid" to uid,
                "email" to email,
                "rol" to rol.name,
                "nombre" to nombre,
                "apellido" to apellido,
                "telefono" to telefono,
                "ciudad" to ciudad,
                "fechaCreacion" to System.currentTimeMillis()
            )

            firestore.collection("users")
                .document(uid)
                .set(userData)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun vetExists(): Boolean = getVeterinario() != null
}
