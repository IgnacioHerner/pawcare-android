package com.ignaherner.pawcare.data.repository

import androidx.compose.material3.darkColorScheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.ignaherner.pawcare.domain.model.Owner
import com.ignaherner.pawcare.domain.model.Rol
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun guardarUsuario(rol: Rol, uid: String): Result<Unit> {
        return try {
            android.util.Log.d("AuthDebug", "Guardando rol: ${rol.name} para uid: $uid")

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

            android.util.Log.d("AuthDebug", "Guardado exitosamente")
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("AuthDebug", "Error: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun obtenerRol(): Result<Rol> {
        return try {
            val uid = auth.currentUser?.uid ?: return Result.failure(
                Exception("Usuario no autenticado")
            )
            android.util.Log.d("RolDebug", "Buscando rol para uid: $uid")
            val documento = firestore.collection("users")
                .document(uid)
                .get()
                .await()
            android.util.Log.d("RolDebug", "Documento existe: ${documento.exists()}")
            android.util.Log.d("RolDebug", "Datos: ${documento.data}")
            val rolString = documento.getString("rol") ?: return Result.failure(
                Exception("Rol no encontrado")
            )
            android.util.Log.d("RolDebug", "Rol encontrado: $rolString")
            Result.success(Rol.valueOf(rolString))
        } catch (e: Exception) {
            android.util.Log.e("RolDebug", "Error: ${e.message}")
            Result.failure(e)
        }
    }


    suspend fun guardarPerfilDueno(owner: Owner): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Result.failure(
                Exception("Usuario no autenticado")
            )

            val ownerData = hashMapOf(
                "nombre" to owner.nombre,
                "apellido" to owner.apellido,
                "telefono" to owner.telefono,
                "email" to owner.email,
                "ciudad" to owner.ciudad,
                "direccion" to owner.direccion,
                "fotoUri" to owner.fotoUri
            )

            firestore.collection("users")
                .document(uid)
                .set(ownerData, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerPerfilDueno(): Result<Owner> {
        return try {
            val uid = auth.currentUser?.uid ?: return Result.failure(
                Exception("Usuario no autenticado")
            )
            val documento = firestore.collection("users")
                .document(uid)
                .get()
                .await()

            if (!documento.exists()) return Result.failure(Exception("Perfil no encontrado"))

            val owner = Owner(
                id = 0L,
                nombre = documento.getString("nombre") ?: "",
                apellido = documento.getString("apellido") ?: "",
                telefono = documento.getString("telefono") ?: "",
                email = documento.getString("email"),
                ciudad = documento.getString("ciudad") ?: "",
                direccion = documento.getString("direccion"),
                fotoUri = documento.getString("fotoUri")
            )
            Result.success(owner)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}