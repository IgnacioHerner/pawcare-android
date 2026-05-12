package com.ignaherner.mispatitas.domain.model

// ═══════════════════════════════════════════════════════════
// CATÁLOGO DE VACUNAS PREDEFINIDAS
// Permite estandarizar las vacunas más comunes
// y facilita la carga del veterinario
// ═══════════════════════════════════════════════════════════

enum class TipoVacuna(
    val displayName: String,
    val especie: List<Especie>,
    val descripcion: String
) {
    // Perros
    ANTIRRABICA_CANINA(
        displayName = "Antirrábica",
        especie = listOf(Especie.PERRO),
        descripcion = "Previene la rabia"
    ),
    POLIVALENTE_QUINTUPLE(
        displayName = "Polivalente / Quíntuple",
        especie = listOf(Especie.PERRO),
        descripcion = "DHPPi-L (moquillo, hepatitis, parvovirus, parainfluenza, leptospirosis)"
    ),
    SEXTUPLE(
        displayName = "Séxtuple",
        especie = listOf(Especie.PERRO),
        descripcion = "Quíntuple + coronavirus"
    ),
    BORDETELLA(
        displayName = "Bordetella",
        especie = listOf(Especie.PERRO),
        descripcion = "Tos de las perreras"
    ),
    LEISHMANIASIS(
        displayName = "Leishmaniasis",
        especie = listOf(Especie.PERRO),
        descripcion = "Previene la leishmaniasis canina"
    ),
    CORONAVIRUS_CANINO(
        displayName = "Coronavirus canino",
        especie = listOf(Especie.PERRO),
        descripcion = "Previene la enteritis por coronavirus"
    ),

    // Gatos
    ANTIRRABICA_FELINA(
        displayName = "Antirrábica",
        especie = listOf(Especie.GATO),
        descripcion = "Previene la rabia"
    ),
    TRIPLE_FELINA(
        displayName = "Triple felina",
        especie = listOf(Especie.GATO),
        descripcion = "FVRCP (rinotraqueítis, calicivirus, panleucopenia)"
    ),
    LEUCEMIA_FELINA(
        displayName = "Leucemia felina",
        especie = listOf(Especie.GATO),
        descripcion = "FeLV"
    ),
    PIF(
        displayName = "PIF (Peritonitis)",
        especie = listOf(Especie.GATO),
        descripcion = "Peritonitis infecciosa felina"
    ),

    // Otra
    OTRA(
        displayName = "Otra",
        especie = listOf(Especie.PERRO, Especie.GATO),
        descripcion = "Especificá el nombre comercial"
    );

    companion object {
        fun porEspecie(especie: Especie): List<TipoVacuna> {
            return values().filter { it.especie.contains(especie) }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// FRECUENCIA DE VACUNACIÓN
// ═══════════════════════════════════════════════════════════
enum class FrecuenciaVacuna(
    val displayName: String,
    val meses: Int?  // null = única dosis
) {
    UNICA(displayName = "Única dosis", meses = null),
    SEIS_MESES(displayName = "Cada 6 meses", meses = 6),
    ANUAL(displayName = "Anual", meses = 12),
    TRES_ANIOS(displayName = "Cada 3 años", meses = 36);
}
