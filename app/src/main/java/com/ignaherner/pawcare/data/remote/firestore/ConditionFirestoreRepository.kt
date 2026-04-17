package com.ignaherner.pawcare.data.remote.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.ignaherner.pawcare.domain.model.Condition
import com.ignaherner.pawcare.domain.model.Medication
import com.ignaherner.pawcare.domain.model.MedicationStatus
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConditionFirestoreRepository @Inject constructor(){

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun guardarCondicion(condition: Condition, petFirestoreId: String): Result<String>{
        return try {
            val conditionData = hashMapOf(
                "petId" to petFirestoreId,
                "nombre" to condition.nombre,
                "fechaDiagnostico" to condition.fechaDiagnostico,
                "notas" to condition.notas

            )

            val docRef = firestore.collection("pets")
                .document(petFirestoreId)
                .collection("condition")
                .add(conditionData)
                .await()

            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizarCondicion(condition: Condition, petFirestoreId: String): Result<Unit>{
        return try {
            if(condition.firestoreId.isBlank()) return Result.failure(
                Exception("Sin firestoreId")
            )

            val conditionData = hashMapOf(
                "petId" to petFirestoreId,
                "nombre" to condition.nombre,
                "fechaDiagnostico" to condition.fechaDiagnostico,
                "notas" to condition.notas
            )
            firestore.collection("pets")
                .document(petFirestoreId)
                .collection("condition")
                .document(condition.firestoreId)
                .set(conditionData, SetOptions.merge())
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarCondicion(firestoreId: String, petFirestoreId: String): Result<Unit>{
        return try {
            firestore.collection("pets")
                .document(petFirestoreId)
                .collection("condition")
                .document(firestoreId)
                .delete()
                .await()

            Result.success(Unit)
        }catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerCondicionesPorMascota(petFirestoreId: String): Result<List<Condition>> {
        return try {
            val documentos = firestore.collection("pets")
                .document(petFirestoreId)
                .collection("condition")
                .get()
                .await()

            val condition = documentos.map { doc ->
                Condition(
                    id = 0L,
                    firestoreId = doc.id,
                    petId = 0L,
                    nombre = doc.getString("nombre") ?: "",
                    fechaDiagnostico = doc.getString("fechaDiagnostico"),
                    notas = doc.getString("notas")
                )
            }
            Result.success(condition)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}