package com.durranitech.salahsync.presentation.state

sealed interface QrEvent {
    data class ShareImage(val uri: android.net.Uri) : QrEvent

    data class ShowError(val message: String) : QrEvent
}