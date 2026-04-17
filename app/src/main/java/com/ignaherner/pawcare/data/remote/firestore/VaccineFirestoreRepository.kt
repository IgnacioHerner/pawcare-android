package com.ignaherner.pawcare.data.remote.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.ignaherner.pawcare.data.local.mapper.toFirestoreString
import com.ignaherner.pawcare.data.local.mapper.toVaccineStatus
import com.ignaherner.pawcare.domain.model.Vaccine
import com.ignaherner.pawcare.domain.model.VaccineStatus
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VaccineFirestoreRepository @Inject constructor(){

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun guardarVacuna(vaccine: Vaccine, petFirestoreId: String): Result<String> {
        return try {
            val vaccineData = hashMapOf(
                "petId" to petFirestoreId,
                "nombre" to vaccine.nombre,
                "fecha" to vaccine.fecha,
                "esAnual" to vaccine.esAnual,
                "proximaDosis" to vaccine.proximaDosis,
                "veterinario" to vaccine.veterinario,
                "notas" to vaccine.notas,
                "status" to vaccine.status.toFirestoreString()
            )

            val docRef = firestore.collection("pets")
                .document(petFirestoreId)
                .collection("vaccines")
                .add(vaccineData)
                .await()

            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizarVacuna(vaccine: Vaccine, petFirestoreId: String): Result<Unit> {
        return try {
            if (vaccine.firestoreId.isBlank()) return Result.failure(
                Exception("Sin firestoreId")
            )
            val vaccineData = hashMapOf(
                "nombre" to vaccine.nombre,
                "fecha" to vaccine.fecha,
                "esAnual" to vaccine.esAnual,
                "proximaDosis" to vaccine.proximaDosis,
                "veterinario" to vaccine.veterinario,
                "notas" to vaccine.notas,
                "status" to vaccine.status.toFirestoreString()
            )
            firestore.collection("pets")
                .document(petFirestoreId)
                .collection("vaccines")
                .document(vaccine.firestoreId)
                .set(vaccineData, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarVacuna(firestoreId: String, petFirestoreId: String): Result<Unit> {
        return try {
            firestore.collection("pets")
                .document(petFirestoreId)
                .collection("vaccines")
                .document(firestoreId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerVacunasPorMascota(petFirestoreId: String): Result<List<Vaccine>> {
        return try {
            val documentos = firestore.collection("pets")
                .document(petFirestoreId)
                .collection("vaccines")
                .get()
                .await()

            val vacunas = documentos.map { doc ->
                Vaccine(
                    id = 0L,
                    firestoreId = doc.id,
                    petId = 0L,
                    nombre = doc.getString("nombre") ?: "",
                    fecha = doc.getString("fecha"),
                    esAnual = doc.getBoolean("esAnual") ?: false,
                    proximaDosis = doc.getString("proximaDosis"),
                    veterinario = doc.getString("veterinario"),
                    notas = doc.getString("notas"),
                    status = doc.getString("status")?.toVaccineStatus()
                        ?: VaccineStatus.Pendiente
                )
            }
            Result.success(vacunas)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}