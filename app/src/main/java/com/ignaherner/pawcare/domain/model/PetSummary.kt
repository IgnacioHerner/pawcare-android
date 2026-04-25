package com.ignaherner.pawcare.domain.model

data class PetSummary(
    val pet: Pet,
    val proximaVacuna: Vaccine?,
    val medicamentoActivo: Medication?,
    val ultimoPeso: Weight?,
    val totalVacunas: Int = 0,
    val totalMedicamentos: Int = 0
)