package com.durranitech.salahsync.domain.repository
import android.graphics.Bitmap
import android.net.Uri

interface QrRepository {
    /**
     * Generates a square QR code bitmap from [payload].
     * @param payload  The string to encode (e.g. deep-link URI)
     * @param sizePx   Width/height of the resulting bitmap in pixels
     */
    suspend fun generateQrBitmap(payload: String, sizePx: Int): Result<Bitmap>

    /**
     * Saves a [Bitmap] to the app's cache directory and returns a
     * FileProvider [Uri] suitable for sharing via Intent.
     */
    suspend fun saveBitmapToCache(bitmap: Bitmap, fileName: String): Result<Uri>
}