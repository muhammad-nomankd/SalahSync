package com.durranitech.salahsync.domain.usecase
import android.graphics.Bitmap
import android.net.Uri
import com.durranitech.salahsync.domain.repository.QrRepository
import javax.inject.Inject

/**
 * Persists the QR [Bitmap] to the app cache and returns a shareable [Uri].
 * Kept separate from [GenerateQrUseCase] so each use-case has a single responsibility.
 */
class SaveQrUseCase @Inject constructor(
    private val qrRepository: QrRepository
) {
    suspend operator fun invoke(bitmap: Bitmap, masjidCode: String): Result<Uri> {
        val fileName = "masjid_qr_$masjidCode.png"
        return qrRepository.saveBitmapToCache(bitmap, fileName)
    }
}