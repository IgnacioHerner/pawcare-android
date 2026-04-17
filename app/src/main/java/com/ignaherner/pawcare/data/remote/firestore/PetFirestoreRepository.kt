package com.ignaherner.pawcare.data.remote.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.ignaherner.pawcare.domain.model.Especie
import com.ignaherner.pawcare.domain.model.FechaNacimientoTipo
import com.ignaherner.pawcare.domain.model.Pet
import com.ignaherner.pawcare.domain.model.Sex
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
                .set(petData, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerMascotasDueno(): Result<List<Pet>> {
        return try {
            val uid = auth.currentUser?.uid ?: return Result.failure(
                Exception("Usuario no autenticado")
            )

            val documentos = firestore.collection("pets")
                .whereEqualTo("ownerId", uid)
                .get()
                .await()

            val mascotas = documentos.map { doc ->
                Pet(
                    id = 0L,
                    firestoreId = doc.id,
                    nombre = doc.getString("nombre") ?: "",
                    especie = Especie.valueOf(doc.getString("especie") ?: "PERRO"),
                    raza = doc.getString("raza"),
                    sexo = doc.getString("sexo")?.let { Sex.valueOf(it) },
                    fechaNacimiento = doc.getString("fechaNacimiento"),
                    fechaNacimientoTipo = FechaNacimientoTipo.valueOf(
                        doc.getString("fechaNacimientoTipo") ?: "DESCONOCIDA"
                    ),
                    castrado = doc.getBoolean("castrado") ?: false,
                    fechaCastracion = doc.getString("fechaCastracion"),
                    fotoUri = doc.getString("fotoUri"),
                    ownerId = uid
                )
            }
            Result.success(mascotas)
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