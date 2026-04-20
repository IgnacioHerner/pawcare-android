package com.ignaherner.pawcare.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.ignaherner.pawcare.domain.model.Especie
import com.ignaherner.pawcare.domain.model.FechaNacimientoTipo
import com.ignaherner.pawcare.domain.model.Pet
import com.ignaherner.pawcare.domain.model.Sex
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VetRepository @Inject constructor() {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun buscarMascotaPorId(firestoreId: String): Result<Pet> {
        return try {
            val documento = firestore.collection("pets")
                .document(firestoreId)
                .get()
                .await()

            if (!documento.exists()) {
                return Result.failure(Exception("Mascota no encontrada"))
            }

            val pet = Pet(
                id = 0L,
                firestoreId = documento.id,
                nombre = documento.getString("nombre") ?: "",
                especie = Especie.valueOf(documento.getString("especie") ?: "PERRO"),
                raza = documento.getString("raza"),
                sexo = documento.getString("sexo")?.let { Sex.valueOf(it) },
                fechaNacimiento = documento.getString("fechaNacimiento"),
                fechaNacimientoTipo = FechaNacimientoTipo.valueOf(
                    documento.getString("fechaNacimientoTipo") ?: "DESCONOCIDA"
                ),
                castrado = documento.getBoolean("castrado") ?: false,
                fechaCastracion = documento.getString("fechaCastracion"),
                fotoUri = documento.getString("fotoUri"),
                ownerId = documento.getString("ownerId") ?: ""
            )
            Result.success(pet)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun buscarMascotasPorOwnerId(ownerId: String): Result<List<Pet>> {
        return try {
            val documentos = firestore.collection("pets")
                .whereEqualTo("ownerId", ownerId)
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
                    ownerId = ownerId
                )
            }
            Result.success(mascotas)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}