package com.durranitech.salahsync.presentation.intent

sealed interface QrIntent {
    data class GenerateQr(val masjidCode: String, val masjidName: String) : QrIntent
    object ShareQr : QrIntent
    object ClearError : QrIntent
}