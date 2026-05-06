package com.ignaherner.pawcare.domain.model

// ═══════════════════════════════════════════════════════════
// SEVERIDAD DE LA CONDICIÓN
// ═══════════════════════════════════════════════════════════
enum class Severidad(val displayName: String) {
    LEVE("Leve"),
    MODERADA("Moderada"),
    GRAVE("Grave")
}

// ═══════════════════════════════════════════════════════════
// ESTADO DE LA CONDICIÓN
// ═══════════════════════════════════════════════════════════
enum class ConditionEstado(val displayName: String) {
    ACTIVA("Activa"),
    EN_TRATAMIENTO("En tratamiento"),
    RESUELTA("Resuelta")
}

enum class CondicionComun(
    val displayName: String,
    val severidadDefault: Severidad
) {
    DISPLASIA_CADERA("Displasia de cadera", Severidad.MODERADA),
    DISPLASIA_CODO("Displasia de codo", Severidad.MODERADA),
    ALERGIA_ALIMENTARIA("Alergia alimentaria", Severidad.LEVE),
    ALERGIA_CUTANEA("Alergia cutánea", Severidad.LEVE),
    OTITIS("Otitis", Severidad.LEVE),
    DERMATITIS("Dermatitis", Severidad.LEVE),
    EPILEPSIA("Epilepsia", Severidad.GRAVE),
    DIABETES("Diabetes", Severidad.GRAVE),
    ARTRITIS("Artritis", Severidad.MODERADA),
    INSUFICIENCIA_RENAL("Insuficiencia renal", Severidad.GRAVE),
    INSUFICIENCIA_CARDIACA("Insuficiencia cardíaca", Severidad.GRAVE),
    HIPOTIROIDISMO("Hipotiroidismo", Severidad.MODERADA),
    LEISHMANIASIS("Leishmaniasis", Severidad.GRAVE),
    OBESIDAD("Obesidad", Severidad.MODERADA),
    CATARATAS("Cataratas", Severidad.MODERADA),
    SARNA("Sarna", Severidad.MODERADA),
    OTRA("Otra", Severidad.LEVE)
}
