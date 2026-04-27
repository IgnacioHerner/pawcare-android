package com.ignaherner.pawcare.data.remote.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.ignaherner.pawcare.domain.model.DosisUnidad
import com.ignaherner.pawcare.domain.model.Medication
import com.ignaherner.pawcare.domain.model.MedicationStatus
import com.ignaherner.pawcare.domain.model.ViaAdministracion

import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MedicationFirestoreRepository @Inject constructor() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun guardarMedicamento(medication: Medication, petFirestoreId: String): Result<String> {
        return try {
            val medicationData = hashMapOf(
                "petId" to petFirestoreId,
                "nombre" to medication.nombre,
                "dosisCantidad" to medication.dosisCantidad,
                "dosisUnidad" to medication.dosisUnidad.name,
                "viaAdministracion" to medication.viaAdministracion.name,
                "esUnicaDosis" to medication.esUnicaDosis,
                "fechaInicio" to medication.fechaInicio,
                "duracionDias" to medication.duracionDias,
                "intervaloHoras" to medication.intervaloHoras,
                "recetadoPor" to medication.recetadoPor,
                "notas" to medication.notas
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

    suspend fun actualizarMedicamento(medication: Medication, petFirestoreId: String): Result<Unit> {
        return try {
            if (medication.firestoreId.isBlank()) return Result.failure(
                Exception("Sin firestoreId")
            )

            val medicationData = hashMapOf(
                "petId" to petFirestoreId,
                "nombre" to medication.nombre,
                "dosisCantidad" to medication.dosisCantidad,
                "dosisUnidad" to medication.dosisUnidad.name,
                "viaAdministracion" to medication.viaAdministracion.name,
                "esUnicaDosis" to medication.esUnicaDosis,
                "fechaInicio" to medication.fechaInicio,
                "duracionDias" to medication.duracionDias,
                "intervaloHoras" to medication.intervaloHoras,
                "recetadoPor" to medication.recetadoPor,
                "notas" to medication.notas
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

    suspend fun eliminarMedicamento(firestoreId: String, petFirestoreId: String): Result<Unit> {
        return try {
            firestore.collection("pets")
                .document(petFirestoreId)
                .collection("medication")
                .document(firestoreId)
                .delete()
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
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
                    dosisCantidad = doc.getDouble("dosisCantidad") ?: 1.0,
                    dosisUnidad = try {
                        DosisUnidad.valueOf(doc.getString("dosisUnidad") ?: "COMPRIMIDO")
                    } catch (e: Exception) {
                        DosisUnidad.COMPRIMIDO
                    },
                    viaAdministracion = try {
                        ViaAdministracion.valueOf(doc.getString("viaAdministracion") ?: "ORAL")
                    } catch (e: Exception) {
                        ViaAdministracion.ORAL
                    },
                    esUnicaDosis = doc.getBoolean("esUnicaDosis") ?: false,
                    fechaInicio = doc.getString("fechaInicio") ?: "",
                    duracionDias = doc.getLong("duracionDias")?.toInt() ?: 1,
                    intervaloHoras = doc.getLong("intervaloHoras")?.toInt() ?: 8,
                    recetadoPor = doc.getString("recetadoPor"),
                    notas = doc.getString("notas")
                )
            }
            Result.success(medication)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}