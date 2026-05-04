package com.ignaherner.pawcare.domain.model

data class VetPetSummary(
    val pet: Pet,
    val owner: Owner?,
    val ultimaVacuna: Vaccine?,
    val medicamentoActivo: Medication?,
    val ultimoPeso: Weight?,
    val ultimoTurno: Appointment?,
    val ultimaDesparasitacion: Deworming?,
    val condiciones: List<Condition>,
    val totalVacunas: Int = 0,
    val totalMedicamentos: Int = 0,
    val totalDesparasitaciones: Int = 0
)