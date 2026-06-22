package com.durranitech.salahsync.extensions

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.toColorInt
import com.google.zxing.common.BitMatrix

private val QrColor = "#1B8C5E".toColorInt()

fun BitMatrix.toBitmap(): Bitmap {
    val width = this.width
    val height = this.height

    val pixels = IntArray(width * height) { index ->
        val x = index % width
        val y = index / width

        if (this[x, y]) {
            QrColor
        } else {
            Color.TRANSPARENT
        }
    }

    return Bitmap.createBitmap(
        width, height, Bitmap.Config.ARGB_8888
    ).apply {
        setPixels(
            pixels, 0, width, 0, 0, width, height
        )
    }
}