package com.durranitech.salahsync.domain.repository

import com.durranitech.salahsync.domain.model.Announcement
import com.durranitech.salahsync.domain.model.Masjid
import com.durranitech.salahsync.domain.model.Member
import com.durranitech.salahsync.domain.model.SalahTime
import com.durranitech.salahsync.util.Resource
import kotlinx.coroutines.flow.Flow

interface MuqtadiRepository {
    suspend fun fetchMasjidDetails(masjidId: String): Flow<Resource<Masjid>>
    suspend fun getMasjidPrayerTimes(masjidId: String): Resource<SalahTime>  // ADDED masjidId parameter
    suspend fun getAnnouncements(masjidId: String): Flow<Resource<List<Announcement>>>
    suspend fun getMembers(masjidId: String): Flow<Resource<List<Member>>>
    suspend fun logout(): Resource<Masjid>
}