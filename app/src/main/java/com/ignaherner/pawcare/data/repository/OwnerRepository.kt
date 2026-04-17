package com.ignaherner.pawcare.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.ignaherner.pawcare.domain.model.Owner
import com.ignaherner.pawcare.presentation.owners.OwnerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class OwnerRepository @Inject constructor() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _ownerState = MutableStateFlow<OwnerState>(OwnerState.Loading)
    val ownerState: StateFlow<OwnerState> = _ownerState.asStateFlow()

    suspend fun getOwner(): Owner? {
        return try {
            val uid = auth.currentUser?.uid ?: return null
            val documento = firestore.collection("users")
                .document(uid)
                .get()
                .await()

            if (!documento.exists()) return null

            Owner(
                id = 0L,
                nombre = documento.getString("nombre") ?: "",
                apellido = documento.getString("apellido") ?: "",
                telefono = documento.getString("telefono") ?: "",
                email = documento.getString("email"),
                ciudad = documento.getString("ciudad") ?: "",
                direccion = documento.getString("direccion"),
                fotoUri = documento.getString("fotoUri")
            )
        } catch (e: Exception) {
            null
        }
    }

    suspend fun insertOwner(owner: Owner): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Result.failure(
                Exception("Usuario no autenticado")
            )
            val ownerData = hashMapOf(
                "nombre" to owner.nombre,
                "apellido" to owner.apellido,
                "telefono" to owner.telefono,
                "email" to owner.email,
                "ciudad" to owner.ciudad,
                "direccion" to owner.direccion,
                "fotoUri" to owner.fotoUri
            )
            firestore.collection("users")
                .document(uid)
                .set(ownerData, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateOwner(owner: Owner): Result<Unit> = insertOwner(owner)

    suspend fun ownerExists(): Boolean {
        val owner = getOwner()
        return owner?.nombre?.isNotBlank() == true
    }
}