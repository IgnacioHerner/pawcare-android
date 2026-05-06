package com.ignaherner.pawcare.domain.model

// ═══════════════════════════════════════════════════════════
// UNIDAD DE DOSIS
// ═══════════════════════════════════════════════════════════
enum class DosisUnidad(val displayName: String) {
    COMPRIMIDO("comprimido(s)"),
    ML("ml"),
    GOTA("gota(s)"),
    MG("mg"),
    SOBRE("sobre(s)"),
    APLICACION("aplicación")
}

// ═══════════════════════════════════════════════════════════
// VÍA DE ADMINISTRACIÓN
// ═══════════════════════════════════════════════════════════
enum class ViaAdministracion(val displayName: String) {
    ORAL("Oral"),
    SUBCUTANEA("Subcutánea"),
    INTRAMUSCULAR("Intramuscular"),
    TOPICA("Tópica"),
    OFTALMICA("Oftálmica"),
    OTICA("Ótica")
}

fun dosisPredefinidasPorUnidad(unidad: DosisUnidad): List<String> {
    return when (unidad) {
        DosisUnidad.COMPRIMIDO -> listOf("1/8", "1/4", "1/2", "1", "1½", "2")
        DosisUnidad.ML -> listOf("0.5", "1", "2", "2.5", "3", "5", "10")
        DosisUnidad.GOTA -> listOf("1", "2", "3", "5", "10", "15", "20")
        DosisUnidad.MG -> listOf("5", "10", "25", "50", "100", "250", "500")
        DosisUnidad.SOBRE -> listOf("1/2", "1", "2")
        DosisUnidad.APLICACION -> listOf("1", "2")
    }
}

fun dosisToDouble(dosis: String): Double {
    return when (dosis.trim()) {
        "1/8" -> 0.125
        "1/4" -> 0.25
        "1/2" -> 0.5
        "1" -> 1.0
        "1½" -> 1.5
        "2" -> 2.0
        "2.5" -> 2.5
        "3" -> 3.0
        "5" -> 5.0
        "10" -> 10.0
        "15" -> 15.0
        "20" -> 20.0
        "25" -> 25.0
        "50" -> 50.0
        "100" -> 100.0
        "250" -> 250.0
        "500" -> 500.0
        else -> dosis.toDoubleOrNull() ?: 1.0
    }
}