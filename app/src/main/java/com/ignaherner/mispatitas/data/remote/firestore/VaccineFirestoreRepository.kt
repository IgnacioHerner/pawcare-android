package com.ignaherner.mispatitas.data.remote.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.ignaherner.mispatitas.domain.model.FrecuenciaVacuna
import com.ignaherner.mispatitas.domain.model.TipoVacuna
import com.ignaherner.mispatitas.domain.model.Vaccine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VaccineFirestoreRepository @Inject constructor() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun guardarVacuna(vaccine: Vaccine, petFirestoreId: String): Result<String> {
        return try {
            val vaccineData = hashMapOf(
                "petId" to petFirestoreId,
                "tipo" to vaccine.tipo.name,
                "nombreComercial" to vaccine.nombreComercial,
                "fechaAplicacion" to vaccine.fechaAplicacion,
                "frecuencia" to vaccine.frecuencia.name,
                "proximaDosis" to vaccine.proximaDosis,
                "veterinario" to vaccine.veterinario,
                "notas" to vaccine.notas
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
                "tipo" to vaccine.tipo.name,
                "nombreComercial" to vaccine.nombreComercial,
                "fechaAplicacion" to vaccine.fechaAplicacion,
                "frecuencia" to vaccine.frecuencia.name,
                "proximaDosis" to vaccine.proximaDosis,
                "veterinario" to vaccine.veterinario,
                "notas" to vaccine.notas
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
                    tipo = try {
                        TipoVacuna.valueOf(doc.getString("tipo") ?: "OTRA")
                    } catch (e: Exception) {
                        TipoVacuna.OTRA
                    },
                    nombreComercial = doc.getString("nombreComercial"),
                    fechaAplicacion = doc.getString("fechaAplicacion") ?: "",
                    frecuencia = try {
                        FrecuenciaVacuna.valueOf(doc.getString("frecuencia") ?: "UNICA")
                    } catch (e: Exception) {
                        FrecuenciaVacuna.UNICA
                    },
                    proximaDosis = doc.getString("proximaDosis"),
                    veterinario = doc.getString("veterinario"),
                    notas = doc.getString("notas")
                )
            }
            Result.success(vacunas)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
