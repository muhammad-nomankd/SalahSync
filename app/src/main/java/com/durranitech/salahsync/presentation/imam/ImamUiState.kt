package com.durranitech.salahsync.presentation.imam

import com.durranitech.salahsync.domain.model.SalahTime

data class ImamUiState(
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val salahTimes: List<SalahTime> = emptyList()

)
