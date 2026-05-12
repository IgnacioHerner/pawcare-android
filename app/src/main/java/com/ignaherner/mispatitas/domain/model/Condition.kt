package com.ignaherner.mispatitas.domain.model

data class Condition(
    val id: Long = 0,
    val firestoreId: String = "",
    val petId: Long,
    val nombre: String,
    val fechaDiagnostico: String, // dd/MM/yyyy - obligatorio
    val severidad: Severidad,
    val estado: ConditionEstado,
    val veterinario: String?,
    val notas: String?
)
