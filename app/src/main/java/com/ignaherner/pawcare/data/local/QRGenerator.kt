package com.ignaherner.pawcare.data.local

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.graphics.createBitmap

@Singleton
class QRGenerator @Inject constructor() {

    fun generarQR(
        nombreMascota: String,
        nombreDueno: String,
        telefono: String,
        medicamentosActivos: List<String> = emptyList()
    ): Bitmap {
        val contenido = buildString {
            appendLine("🐾 PawCare")
            appendLine("Mascota: $nombreMascota")
            appendLine("Dueño: $nombreDueno")
            appendLine("Tel: $telefono")
            if (medicamentosActivos.isNotEmpty()) {
                appendLine("Medicamentos: ${medicamentosActivos.joinToString { ", " }}")
            }
        }

        val hints = mapOf(
            EncodeHintType.CHARACTER_SET to "UTF-8",
            EncodeHintType.MARGIN to 1
        )

        val writter = QRCodeWriter()
        val bitMatrix = writter.encode(
            contenido,
            BarcodeFormat.QR_CODE,
            512,
            512,
            hints
        )

        val bitmap = createBitmap(512, 512, Bitmap.Config.RGB_565)
        for (x in 0 until 512) {
            for (y in 0 until 512) {
                bitmap.setPixel(
                    x, y,
                    if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                )
            }
        }
        return bitmap
    }
}