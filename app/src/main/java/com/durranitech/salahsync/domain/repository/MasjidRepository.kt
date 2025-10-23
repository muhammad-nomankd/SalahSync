package com.durranitech.salahsync.domain.repository

import com.durranitech.salahsync.domain.model.Announcement
import com.durranitech.salahsync.domain.model.SalahTime
import com.durranitech.salahsync.util.Resource

interface MasjidRepository{
    suspend fun getSalahTimes(masjidId: String): Resource<SalahTime>
    suspend fun updateSalahTimes(): Boolean
    suspend fun postAnnouncement(announcement: Announcement): Boolean
    suspend fun getAnnouncements(): List<Announcement>
    suspend fun deleteAnnouncement(announcement: Announcement): Boolean
    suspend fun updateAnnouncement(announcement: Announcement): Boolean
    suspend fun getNextPrayerTime(): SalahTime?
}