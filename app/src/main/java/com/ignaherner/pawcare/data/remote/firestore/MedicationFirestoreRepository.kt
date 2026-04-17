package com.ignaherner.pawcare.data.remote.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.ignaherner.pawcare.domain.model.Medication
import com.ignaherner.pawcare.domain.model.MedicationStatus

import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MedicationFirestoreRepository @Inject constructor(){

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun guardarMedicamento(medication: Medication, petFirestoreId: String): Result<String>{
        return try {
            val medicationData = hashMapOf(
                "petId" to petFirestoreId,
                "nombre" to medication.nombre,
                "fechaInicio" to medication.fechaInicio,
                "duracionDias" to medication.duracionDias,
                "intervaloHoras" to medication.intervaloHoras,
                "recetadoPor" to medication.recetadoPor,
                "dosis" to medication.dosis,
                "esUnicaDosis" to medication.esUnicaDosis,
                "notas" to medication.notas,
                "status" to medication.status.name
            )

            val docRef = firestore.collection("pets")
                .document(petFirestoreId)
                .collection("medication")
                .add(medicationData)
                .await()

            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizarMedicamento(medication: Medication, petFirestoreId: String): Result<Unit>{
        return try {
            if(medication.firestoreId.isBlank()) return Result.failure(
                Exception("Sin firestoreId")
            )

            val medicationData = hashMapOf(
                "petId" to petFirestoreId,
                "nombre" to medication.nombre,
                "fechaInicio" to medication.fechaInicio,
                "duracionDias" to medication.duracionDias,
                "intervaloHoras" to medication.intervaloHoras,
                "recetadoPor" to medication.recetadoPor,
                "dosis" to medication.dosis,
                "esUnicaDosis" to medication.esUnicaDosis,
                "notas" to medication.notas,
                "status" to medication.status.name
            )
            firestore.collection("pets")
                .document(petFirestoreId)
                .collection("medication")
                .document(medication.firestoreId)
                .set(medicationData, SetOptions.merge())
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarMedicamento(firestoreId: String, petFirestoreId: String): Result<Unit>{
        return try {
            firestore.collection("pets")
                .document(petFirestoreId)
                .collection("medication")
                .document(firestoreId)
                .delete()
                .await()

            Result.success(Unit)
        }catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerMedicamentosPorMascota(petFirestoreId: String): Result<List<Medication>> {
        return try {
            val documentos = firestore.collection("pets")
                .document(petFirestoreId)
                .collection("medication")
                .get()
                .await()

            val medication = documentos.map { doc ->
                Medication(
                    id = 0L,
                    firestoreId = doc.id,
                    petId = 0L,
                    nombre = doc.getString("nombre") ?: "",
                    fechaInicio = doc.getString("fechaInicio") ?: "",
                    duracionDias = doc.getLong("duracionDias")?.toInt() ?: 1,
                    intervaloHoras = doc.getLong("intervaloHoras")?.toInt() ?: 8,
                    dosis = doc.getString("dosis") ?: "",
                    esUnicaDosis = doc.getBoolean("esUnicaDosis") ?: false,
                    notas = doc.getString("notas"),
                    recetadoPor = doc.getString("recetadoPor"),
                    status = MedicationStatus.valueOf(
                        doc.getString("status") ?: MedicationStatus.ACTIVO.name
                    )
                )
            }
            Result.success(medication)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}