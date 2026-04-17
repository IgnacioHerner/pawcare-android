package com.ignaherner.pawcare.data.remote.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.ignaherner.pawcare.domain.model.Appointment
import com.ignaherner.pawcare.domain.model.AppointmentStatus
import com.ignaherner.pawcare.domain.model.Medication
import com.ignaherner.pawcare.domain.model.MedicationStatus
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppointmentFirestoreRepository @Inject constructor(){

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun guardarTurno(appointment: Appointment, petFirestoreId: String): Result<String>{
        return try {
            val appointmentData = hashMapOf(
                "petId" to petFirestoreId,
                "fecha" to appointment.fecha,
                "veterinario" to appointment.veterinario,
                "motivo" to appointment.motivo,
                "notas" to appointment.notas,
                "status" to appointment.status.name
            )

            val docRef = firestore.collection("pets")
                .document(petFirestoreId)
                .collection("appointment")
                .add(appointmentData)
                .await()

            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizarTurno(appointment: Appointment, petFirestoreId: String): Result<Unit>{
        return try {
            if(appointment.firestoreId.isBlank()) return Result.failure(
                Exception("Sin firestoreId")
            )

            val appointmentData = hashMapOf(
                "petId" to petFirestoreId,
                "fecha" to appointment.fecha,
                "veterinario" to appointment.veterinario,
                "motivo" to appointment.motivo,
                "notas" to appointment.notas,
                "status" to appointment.status.name
            )
            firestore.collection("pets")
                .document(petFirestoreId)
                .collection("appointment")
                .document(appointment.firestoreId)
                .set(appointmentData, SetOptions.merge())
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarTurno(firestoreId: String, petFirestoreId: String): Result<Unit>{
        return try {
            firestore.collection("pets")
                .document(petFirestoreId)
                .collection("appointment")
                .document(firestoreId)
                .delete()
                .await()

            Result.success(Unit)
        }catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerTurnoPorMascota(petFirestoreId: String): Result<List<Appointment>> {
        return try {
            val documentos = firestore.collection("pets")
                .document(petFirestoreId)
                .collection("appointment")
                .get()
                .await()

            val appointment = documentos.map { doc ->
                Appointment(
                    id = 0L,
                    firestoreId = doc.id,
                    petId = 0L,
                    fecha = doc.getString("fecha") ?: "",
                    veterinario = doc.getString("veterinario"),
                    motivo = doc.getString("motivo"),
                    notas = doc.getString("notas"),
                    status = AppointmentStatus.valueOf(
                        doc.getString("status") ?: AppointmentStatus.PENDIENTE.name
                    )
                )
            }
            Result.success(appointment)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}