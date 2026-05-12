package com.ignaherner.mispatitas.data.remote.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.ignaherner.mispatitas.domain.model.Appointment
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppointmentFirestoreRepository @Inject constructor() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun guardarTurno(appointment: Appointment, petFirestoreId: String): Result<String> {
        return try {
            val appointmentData = hashMapOf(
                "petId" to petFirestoreId,
                "fecha" to appointment.fecha,
                "motivo" to appointment.motivo,
                "veterinario" to appointment.veterinario,
                "clinica" to appointment.clinica,
                "diagnostico" to appointment.diagnostico,
                "notas" to appointment.notas
            )

            val docRef = firestore.collection("pets")
                .document(petFirestoreId)
                .collection("appointments")
                .add(appointmentData)
                .await()

            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizarTurno(appointment: Appointment, petFirestoreId: String): Result<Unit> {
        return try {
            if (appointment.firestoreId.isBlank()) return Result.failure(
                Exception("Sin firestoreId")
            )

            val appointmentData = hashMapOf(
                "fecha" to appointment.fecha,
                "motivo" to appointment.motivo,
                "veterinario" to appointment.veterinario,
                "clinica" to appointment.clinica,
                "diagnostico" to appointment.diagnostico,
                "notas" to appointment.notas
            )
            firestore.collection("pets")
                .document(petFirestoreId)
                .collection("appointments")
                .document(appointment.firestoreId)
                .set(appointmentData, SetOptions.merge())
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarTurno(firestoreId: String, petFirestoreId: String): Result<Unit> {
        return try {
            firestore.collection("pets")
                .document(petFirestoreId)
                .collection("appointments")
                .document(firestoreId)
                .delete()
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerTurnosPorMascota(petFirestoreId: String): Result<List<Appointment>> {
        return try {
            val documentos = firestore.collection("pets")
                .document(petFirestoreId)
                .collection("appointments")
                .get()
                .await()

            val appointments = documentos.map { doc ->
                Appointment(
                    id = 0L,
                    firestoreId = doc.id,
                    petId = 0L,
                    fecha = doc.getString("fecha") ?: "",
                    motivo = doc.getString("motivo") ?: "",
                    veterinario = doc.getString("veterinario"),
                    clinica = doc.getString("clinica"),
                    diagnostico = doc.getString("diagnostico"),
                    notas = doc.getString("notas")
                )
            }
            Result.success(appointments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
