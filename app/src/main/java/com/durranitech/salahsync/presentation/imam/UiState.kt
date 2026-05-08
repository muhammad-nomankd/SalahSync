package com.durranitech.salahsync.presentation.imam

import com.durranitech.salahsync.domain.model.Announcement
import com.durranitech.salahsync.domain.model.Masjid
import com.durranitech.salahsync.domain.model.Member
import com.durranitech.salahsync.domain.model.SalahTime

data class UiState(
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val salahTimes: List<SalahTime> = emptyList(),
    val announcements: List<Announcement> = emptyList(),
    val nextPrayerName: String = "",
    val nextPrayerTime: Long = 0.toLong(),
    val timeUntilPrayer: Long = 0.toLong(),
    val masjid: Masjid? = null,
    val salahTime: SalahTime? = SalahTime(),
    val members: List<Member> = emptyList()

)
