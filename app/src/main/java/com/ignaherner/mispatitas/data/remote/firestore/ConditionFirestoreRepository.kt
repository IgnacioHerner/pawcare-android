package com.ignaherner.mispatitas.data.remote.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.ignaherner.mispatitas.domain.model.Condition
import com.ignaherner.mispatitas.domain.model.ConditionEstado
import com.ignaherner.mispatitas.domain.model.Medication
import com.ignaherner.mispatitas.domain.model.MedicationStatus
import com.ignaherner.mispatitas.domain.model.Severidad
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConditionFirestoreRepository @Inject constructor() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun guardarCondicion(condition: Condition, petFirestoreId: String): Result<String> {
        return try {
            val conditionData = hashMapOf(
                "petId" to petFirestoreId,
                "nombre" to condition.nombre,
                "fechaDiagnostico" to condition.fechaDiagnostico,
                "severidad" to condition.severidad.name,
                "estado" to condition.estado.name,
                "veterinario" to condition.veterinario,
                "notas" to condition.notas
            )

            val docRef = firestore.collection("pets")
                .document(petFirestoreId)
                .collection("conditions")
                .add(conditionData)
                .await()

            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizarCondicion(condition: Condition, petFirestoreId: String): Result<Unit> {
        return try {
            if (condition.firestoreId.isBlank()) return Result.failure(
                Exception("Sin firestoreId")
            )

            val conditionData = hashMapOf(
                "nombre" to condition.nombre,
                "fechaDiagnostico" to condition.fechaDiagnostico,
                "severidad" to condition.severidad.name,
                "estado" to condition.estado.name,
                "veterinario" to condition.veterinario,
                "notas" to condition.notas
            )
            firestore.collection("pets")
                .document(petFirestoreId)
                .collection("conditions")
                .document(condition.firestoreId)
                .set(conditionData, SetOptions.merge())
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarCondicion(firestoreId: String, petFirestoreId: String): Result<Unit> {
        return try {
            firestore.collection("pets")
                .document(petFirestoreId)
                .collection("conditions")
                .document(firestoreId)
                .delete()
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerCondicionesPorMascota(petFirestoreId: String): Result<List<Condition>> {
        return try {
            val documentos = firestore.collection("pets")
                .document(petFirestoreId)
                .collection("conditions")
                .get()
                .await()

            val conditions = documentos.map { doc ->
                Condition(
                    id = 0L,
                    firestoreId = doc.id,
                    petId = 0L,
                    nombre = doc.getString("nombre") ?: "",
                    fechaDiagnostico = doc.getString("fechaDiagnostico") ?: "",
                    severidad = try {
                        Severidad.valueOf(doc.getString("severidad") ?: "LEVE")
                    } catch (e: Exception) {
                        Severidad.LEVE
                    },
                    estado = try {
                        ConditionEstado.valueOf(doc.getString("estado") ?: "ACTIVA")
                    } catch (e: Exception) {
                        ConditionEstado.ACTIVA
                    },
                    veterinario = doc.getString("veterinario"),
                    notas = doc.getString("notas")
                )
            }
            Result.success(conditions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
