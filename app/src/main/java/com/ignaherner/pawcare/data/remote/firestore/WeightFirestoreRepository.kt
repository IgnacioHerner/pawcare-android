package com.ignaherner.pawcare.data.remote.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.ignaherner.pawcare.domain.model.Medication
import com.ignaherner.pawcare.domain.model.MedicationStatus
import com.ignaherner.pawcare.domain.model.Weight
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeightFirestoreRepository @Inject constructor() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun guardarPeso(wheight: Weight, petFirestoreId: String): Result<String>{
        return try {
            val medicationData = hashMapOf(
                "petId" to petFirestoreId,
                "peso" to wheight.peso,
                "fecha" to wheight.fecha,
                "notas" to wheight.notas
            )

            val docRef = firestore.collection("pets")
                .document(petFirestoreId)
                .collection("weight")
                .add(medicationData)
                .await()

            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizarPeso(weight: Weight, petFirestoreId: String): Result<Unit>{
        return try {
            if(weight.firestoreId.isBlank()) return Result.failure(
                Exception("Sin firestoreId")
            )

            val weightData = hashMapOf(
                "petId" to petFirestoreId,
                "peso" to weight.peso,
                "fecha" to weight.fecha,
                "notas" to weight.notas

            )
            firestore.collection("pets")
                .document(petFirestoreId)
                .collection("weight")
                .document(weight.firestoreId)
                .set(weightData, SetOptions.merge())
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarPeso(firestoreId: String, petFirestoreId: String): Result<Unit>{
        return try {
            firestore.collection("pets")
                .document(petFirestoreId)
                .collection("weight")
                .document(firestoreId)
                .delete()
                .await()

            Result.success(Unit)
        }catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerPesoPorMascota(petFirestoreId: String): Result<List<Weight>> {
        return try {
            val documentos = firestore.collection("pets")
                .document(petFirestoreId)
                .collection("weight")
                .get()
                .await()

            val weight = documentos.map { doc ->
                Weight(
                    id = 0L,
                    firestoreId = doc.id,
                    petId = 0L,
                    peso = doc.getDouble("peso") ?: 0.0,
                    fecha = doc.getString("fecha") ?: "",
                    notas = doc.getString("notas")

                )
            }
            Result.success(weight)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}