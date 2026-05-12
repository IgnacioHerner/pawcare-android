package com.ignaherner.mispatitas.utils

object CodigoGenerator {
    fun generarCodigo(nombreMascota: String): String {
        val prefijo = nombreMascota
            .uppercase()
            .filter { it.isLetter() }
            .take(3)
            .padEnd(3, 'X')
        val numero = (1000..9999).random()
        return "$prefijo-$numero"
    }
}
