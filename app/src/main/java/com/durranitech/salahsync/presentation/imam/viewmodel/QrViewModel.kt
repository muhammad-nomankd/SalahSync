package com.durranitech.salahsync.presentation.imam.viewmodel

import com.durranitech.salahsync.presentation.event.QrUiState
import com.durranitech.salahsync.presentation.intent.QrIntent
import com.durranitech.salahsync.presentation.state.QrEvent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.durranitech.salahsync.domain.usecase.GenerateQrUseCase
import com.durranitech.salahsync.domain.usecase.SaveQrUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QrViewModel @Inject constructor(
    private val generateQrUseCase: GenerateQrUseCase,
    private val saveQrUseCase: SaveQrUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(QrUiState())
    val uiState: StateFlow<QrUiState> = _uiState.asStateFlow()

    private val _events = Channel<QrEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onIntent(intent: QrIntent) {
        when (intent) {
            is QrIntent.GenerateQr -> generateQr(intent.masjidCode, intent.masjidName)
            is QrIntent.ShareQr    -> shareCurrentQr()
            is QrIntent.ClearError -> _uiState.update { it.copy(error = null) }
        }
    }

    private fun generateQr(masjidCode: String, masjidName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            generateQrUseCase(masjidCode, masjidName)
                .onSuccess { bitmap ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            qrBitmap = bitmap,
                            masjidCode = masjidCode,
                            masjidName = masjidName
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoading = false, error = error.message ?: "Failed to generate QR")
                    }
                }
        }
    }

    private fun shareCurrentQr() {
        val bitmap = _uiState.value.qrBitmap ?: return
        val code = _uiState.value.masjidCode

        viewModelScope.launch {
            saveQrUseCase(bitmap, code)
                .onSuccess { uri ->
                    _events.send(QrEvent.ShareImage(uri))
                }
                .onFailure { error ->
                    _events.send(QrEvent.ShowError(error.message ?: "Could not prepare image for sharing"))
                }
        }
    }
}