package com.example.gestionsolicitudes.utils

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

fun codigoQr(texto: String): ImageBitmap {
    val tamaño = 512
    val matriz = QRCodeWriter().encode(texto, BarcodeFormat.QR_CODE, tamaño, tamaño)
    val bitmap = Bitmap.createBitmap(tamaño, tamaño, Bitmap.Config.ARGB_8888)
    for (x in 0 until tamaño) {
        for (y in 0 until tamaño) {
            val color = if (matriz.get(x, y)) Color.BLACK else Color.WHITE
            bitmap.setPixel(x, y, color)
        }
    }
    return bitmap.asImageBitmap()
}
