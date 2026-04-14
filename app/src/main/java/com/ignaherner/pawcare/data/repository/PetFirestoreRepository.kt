package com.ignaherner.pawcare.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ignaherner.pawcare.domain.model.Pet
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PetFirestoreRepository @Inject constructor(){

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun guardarPet(pet: Pet): Result<String> {
        return try {
            val uid = auth.currentUser?.uid ?: return Result.failure(
                Exception("Usuario no autenticado")
            )

            val petData = hashMapOf(
                "nombre" to pet.nombre,
                "especie" to pet.especie.name,
                "raza" to pet.raza,
                "sexo" to pet.sexo?.name,
                "fechaNacimiento" to pet.fechaNacimiento,
                "fechaNacimientoTipo" to pet.fechaNacimientoTipo.name,
                "castrado" to pet.castrado,
                "fechaCastracion" to pet.fechaCastracion,
                "fotoUri" to pet.fotoUri,
                "ownerId" to uid
            )

            // Firestore genera el ID Automaticamente
            val docRef = firestore.collection("pets")
                .add(petData)
                .await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizarPet(pet: Pet): Result<Unit> {
        return try {
            if (pet.firestoreId.isBlank()) return Result.failure(
                Exception("Pet sin firestoreId")
            )

            val petData =hashMapOf(
                "nombre" to pet.nombre,
                "especie" to pet.especie.name,
                "raza" to pet.raza,
                "sexo" to pet.sexo?.name,
                "fechaNacimiento" to pet.fechaNacimiento,
                "fechaNacimientoTipo" to pet.fechaNacimientoTipo.name,
                "castrado" to pet.castrado,
                "fechaCastracion" to pet.fechaCastracion,
                "fotoUri" to pet.fotoUri
            )

            firestore.collection("pets")
                .document(pet.firestoreId)
                .update(petData as Map<String, Any>)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarPet(firestoreId: String): Result<Unit> {
        return try {
            firestore.collection("pets")
                .document(firestoreId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}