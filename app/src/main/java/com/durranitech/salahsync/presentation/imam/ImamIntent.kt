package com.durranitech.salahsync.presentation.imam

import com.durranitech.salahsync.domain.model.Masjid
import com.durranitech.salahsync.domain.model.SalahTime

sealed class ImamIntent {
    data class UpdateSalahTimes(val salahTime: SalahTime) : ImamIntent()
    object LoadSalahTimes : ImamIntent()
    object ClearMessage: ImamIntent()
    data class createMasjid(val masjid: Masjid): ImamIntent()
    object updateMasjid: ImamIntent()
    object getMasjid: ImamIntent()
}