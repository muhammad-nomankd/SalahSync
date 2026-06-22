package com.durranitech.salahsync.presentation.event

// ─── UI State ─────────────────────────────────────────────────────────────────

data class QrUiState(
    val isLoading: Boolean = false,
    val qrBitmap: android.graphics.Bitmap? = null,
    val masjidCode: String = "",
    val masjidName: String = "",
    val error: String? = null
)
