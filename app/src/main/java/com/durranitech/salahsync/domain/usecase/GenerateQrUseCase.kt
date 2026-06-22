package com.durranitech.salahsync.domain.usecase
import android.graphics.Bitmap
import com.durranitech.salahsync.domain.repository.QrRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import javax.inject.Inject

class GenerateQrUseCase @Inject constructor(
    private val qrRepository: QrRepository
) {
    suspend operator fun invoke(masjidCode: String, masjidName: String): Result<Bitmap> {
        val encodedName = withContext(Dispatchers.IO) {
            URLEncoder.encode(masjidName, "UTF-8")
        }
        val payload = "salahsync://join?code=$masjidCode&name=$encodedName"
        return qrRepository.generateQrBitmap(payload, sizePx = 512)
    }
}