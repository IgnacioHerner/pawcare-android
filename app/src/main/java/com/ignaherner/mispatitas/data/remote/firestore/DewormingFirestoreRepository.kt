package com.ignaherner.mispatitas.data.remote.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.ignaherner.mispatitas.domain.model.Deworming
import com.ignaherner.mispatitas.domain.model.DewormingTipo
import com.ignaherner.mispatitas.domain.model.FrecuenciaDeworming
import com.ignaherner.mispatitas.domain.model.Medication
import com.ignaherner.mispatitas.domain.model.MedicationStatus
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DewormingFirestoreRepository @Inject constructor() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun guardarDesparasitacion(deworming: Deworming, petFirestoreId: String): Result<String> {
        return try {
            val dewormingData = hashMapOf(
                "petId" to petFirestoreId,
                "producto" to deworming.producto,
                "tipo" to deworming.tipo.name,
                "fechaAplicacion" to deworming.fechaAplicacion,
                "frecuencia" to deworming.frecuencia.name,
                "proximaDosis" to deworming.proximaDosis,
                "veterinario" to deworming.veterinario,
                "notas" to deworming.notas
            )

            val docRef = firestore.collection("pets")
                .document(petFirestoreId)
                .collection("dewormings")
                .add(dewormingData)
                .await()

            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizarDesparasitacion(deworming: Deworming, petFirestoreId: String): Result<Unit> {
        return try {
            if (deworming.firestoreId.isBlank()) return Result.failure(
                Exception("Sin firestoreId")
            )

            val dewormingData = hashMapOf(
                "producto" to deworming.producto,
                "tipo" to deworming.tipo.name,
                "fechaAplicacion" to deworming.fechaAplicacion,
                "frecuencia" to deworming.frecuencia.name,
                "proximaDosis" to deworming.proximaDosis,
                "veterinario" to deworming.veterinario,
                "notas" to deworming.notas
            )
            firestore.collection("pets")
                .document(petFirestoreId)
                .collection("dewormings")
                .document(deworming.firestoreId)
                .set(dewormingData, SetOptions.merge())
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarDesparasitacion(firestoreId: String, petFirestoreId: String): Result<Unit> {
        return try {
            firestore.collection("pets")
                .document(petFirestoreId)
                .collection("dewormings")
                .document(firestoreId)
                .delete()
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerDesparasitacionesPorMascota(petFirestoreId: String): Result<List<Deworming>> {
        return try {
            val documentos = firestore.collection("pets")
                .document(petFirestoreId)
                .collection("dewormings")
                .get()
                .await()

            val dewormings = documentos.map { doc ->
                Deworming(
                    id = 0L,
                    firestoreId = doc.id,
                    petId = 0L,
                    producto = doc.getString("producto") ?: "",
                    tipo = try {
                        DewormingTipo.valueOf(doc.getString("tipo") ?: "INTERNA")
                    } catch (e: Exception) {
                        DewormingTipo.INTERNA
                    },
                    fechaAplicacion = doc.getString("fechaAplicacion") ?: "",
                    frecuencia = try {
                        FrecuenciaDeworming.valueOf(doc.getString("frecuencia") ?: "UNICA")
                    } catch (e: Exception) {
                        FrecuenciaDeworming.UNICA
                    },
                    proximaDosis = doc.getString("proximaDosis"),
                    veterinario = doc.getString("veterinario"),
                    notas = doc.getString("notas")
                )
            }
            Result.success(dewormings)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
