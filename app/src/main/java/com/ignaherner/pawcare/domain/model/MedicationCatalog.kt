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