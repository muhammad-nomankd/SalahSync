package com.durranitech.salahsync.presentation.imam

import com.durranitech.salahsync.domain.model.Announcement
import com.durranitech.salahsync.domain.model.Masjid
import com.durranitech.salahsync.domain.model.Prayer
import com.durranitech.salahsync.domain.model.SalahTime

data class ImamUiState(
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val salahTimes: List<SalahTime> = emptyList(),
    val announcements: List<Announcement> = emptyList(),
    val nextPrayer: Prayer? = null,
    val timeUntilPrayer: Long? = 0L,
    val masjid: Masjid? = null

)
