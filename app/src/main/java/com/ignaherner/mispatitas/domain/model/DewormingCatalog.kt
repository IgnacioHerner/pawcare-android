package com.ignaherner.mispatitas.domain.model

// ═══════════════════════════════════════════════════════════
// TIPO DE DESPARASITACIÓN
// ═══════════════════════════════════════════════════════════
enum class DewormingTipo(val displayName: String) {
    INTERNA("Interna"),
    EXTERNA("Externa"),
    AMBAS("Ambas")
}

// ═══════════════════════════════════════════════════════════
// FRECUENCIA DE DESPARASITACIÓN
// ═══════════════════════════════════════════════════════════
enum class FrecuenciaDeworming(
    val displayName: String,
    val meses: Int?
) {
    UNICA("Única", meses = null),
    MENSUAL("Mensual", meses = 1),
    TRIMESTRAL("Trimestral", meses = 3),
    SEMESTRAL("Semestral", meses = 6),
    ANUAL("Anual", meses = 12)
}
