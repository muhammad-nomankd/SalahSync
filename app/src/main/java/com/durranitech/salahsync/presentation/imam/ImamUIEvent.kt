package com.durranitech.salahsync.presentation.imam

import com.durranitech.salahsync.domain.model.SalahTime

sealed class ImamUIEvent {
    data class UpdateSalahTimes(val salahTime: SalahTime) : ImamUIEvent()
    object LoadSalahTimes : ImamUIEvent()
    object ClearMessage: ImamUIEvent()
}