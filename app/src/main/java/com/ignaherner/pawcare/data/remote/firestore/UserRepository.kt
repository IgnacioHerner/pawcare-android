package com.ignaherner.pawcare.data.remote.firestore

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.ignaherner.pawcare.domain.model.Owner
import com.ignaherner.pawcare.domain.model.Rol
import com.ignaherner.pawcare.domain.model.Veterinario
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun guardarUsuario(rol: Rol, uid: String, nombre: String = ""): Result<Unit> {
        return try {
            val usuario = hashMapOf(
                "uid" to uid,
                "email" to (auth.currentUser?.email ?: ""),
                "rol" to rol.name,
                "nombre" to nombre,
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

    suspend fun getVeterinario(): Veterinario? {
        return try {
            val uid = auth.currentUser?.uid ?: return null
            val documento = firestore.collection("users")
                .document(uid)
                .get()
                .await()

            if (!documento.exists()) return null
            if (documento.getString("matricula") == null) return null

            Veterinario(
                id = uid,
                nombre = documento.getString("nombre") ?: "",
                apellido = documento.getString("apellido") ?: "",
                matricula = documento.getString("matricula") ?: "",
                telefono = documento.getString("telefono") ?: "",
                direccionVet = documento.getString("direccionVet"),
                ciudadVet = documento.getString("ciudadVet"),
                especialidad = documento.getString("especialidad")
            )
        } catch (e: Exception) {
            null
        }
    }

    suspend fun vetExists(): Boolean = getVeterinario() != null

    suspend fun guardarVeterinario(vet: Veterinario): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Result.failure(
                Exception("Usuario no autenticado")
            )
            val vetData = hashMapOf(
                "nombre" to vet.nombre,
                "apellido" to vet.apellido,
                "matricula" to vet.matricula,
                "telefono" to vet.telefono,
                "direccionVet" to vet.direccionVet,
                "ciudadVet" to vet.ciudadVet,
                "especialidad" to vet.especialidad
            )
            firestore.collection("users")
                .document(uid)
                .set(vetData, SetOptions.merge())
                .await()
            Result.success(Unit)
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

}