package com.ignaherner.pawcare.data.remote.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.ignaherner.pawcare.domain.model.Deworming
import com.ignaherner.pawcare.domain.model.Medication
import com.ignaherner.pawcare.domain.model.MedicationStatus
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DewormingFirestoreRepository @Inject constructor(){

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun guardarDesparasitacion(deworming: Deworming, petFirestoreId: String): Result<String>{
        return try {
            val dewormingData = hashMapOf(
                "petId" to petFirestoreId,
                "fecha" to deworming.fecha,
                "producto" to deworming.producto,
                "proximaFecha" to deworming.proximaFecha,
                "notas" to deworming.notas
            )

            val docRef = firestore.collection("pets")
                .document(petFirestoreId)
                .collection("deworming")
                .add(dewormingData)
                .await()

            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizarDesparasitacion(deworming: Deworming, petFirestoreId: String): Result<Unit>{
        return try {
            if(deworming.firestoreId.isBlank()) return Result.failure(
                Exception("Sin firestoreId")
            )

            val dewormingData = hashMapOf(
                "petId" to petFirestoreId,
                "fecha" to deworming.fecha,
                "producto" to deworming.producto,
                "proximaFecha" to deworming.proximaFecha,
                "notas" to deworming.notas
            )
            firestore.collection("pets")
                .document(petFirestoreId)
                .collection("deworming")
                .document(deworming.firestoreId)
                .set(dewormingData, SetOptions.merge())
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarDesparasitacion(firestoreId: String, petFirestoreId: String): Result<Unit>{
        return try {
            firestore.collection("pets")
                .document(petFirestoreId)
                .collection("deworming")
                .document(firestoreId)
                .delete()
                .await()

            Result.success(Unit)
        }catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerDesparasitacionesPorMascota(petFirestoreId: String): Result<List<Deworming>> {
        return try {
            val documentos = firestore.collection("pets")
                .document(petFirestoreId)
                .collection("deworming")
                .get()
                .await()

            val deworming = documentos.map { doc ->
                Deworming(
                    id = 0L,
                    firestoreId = doc.id,
                    petId = 0L,
                    fecha = doc.getString("fecha") ?: "",
                    producto = doc.getString("producto"),
                    proximaFecha = doc.getString("proximaFecha"),
                    notas = doc.getString("notas")
                )
            }
            Result.success(deworming)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}