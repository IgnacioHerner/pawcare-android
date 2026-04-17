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
}