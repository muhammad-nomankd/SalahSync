package com.durranitech.salahsync.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import com.durranitech.salahsync.domain.repository.QrRepository
import com.durranitech.salahsync.extensions.toBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QrRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : QrRepository {

    private val qrWriter = QRCodeWriter()

    private val qrHints = mapOf(
        EncodeHintType.MARGIN to 1, EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.H
    )

    override suspend fun generateQrBitmap(
        payload: String, sizePx: Int
    ): Result<Bitmap> = withContext(Dispatchers.Default) {
        runCatching {
            qrWriter.encode(payload, BarcodeFormat.QR_CODE, sizePx, sizePx, qrHints).toBitmap()
        }
    }

    override suspend fun saveBitmapToCache(
        bitmap: Bitmap, fileName: String
    ): Result<Uri> = withContext(Dispatchers.IO) {
        runCatching {
            val directory = File(context.cacheDir, "qr_codes").apply { mkdirs() }
            val file = File(directory, fileName)

            FileOutputStream(file).use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }

            FileProvider.getUriForFile(
                context, "${context.packageName}.provider", file
            )
        }
    }
}
