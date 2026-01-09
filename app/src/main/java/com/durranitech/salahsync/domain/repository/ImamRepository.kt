package com.durranitech.salahsync.domain.repository

import com.durranitech.salahsync.domain.model.Announcement
import com.durranitech.salahsync.domain.model.Masjid
import com.durranitech.salahsync.domain.model.Member
import com.durranitech.salahsync.domain.model.Prayer
import com.durranitech.salahsync.domain.model.SalahTime
import com.durranitech.salahsync.util.Resource
import kotlinx.coroutines.flow.Flow

interface ImamRepository {
    suspend fun getMasjid(): Resource<Masjid>
    suspend fun createMasjid(masjid: Masjid): Resource<Unit>

    fun getMasjidDetails(): Flow<Resource<Masjid>>

    suspend fun updateMasjidPrayerTimes(prayer: SalahTime): Resource<Unit>
    suspend fun getMasjidPrayerTimes(): Resource<SalahTime>
    suspend fun addAnnouncements(announcement: Announcement): Resource<Unit>
    suspend fun deleteAnnouncement(announcementId: String): Resource<Unit>
    suspend fun getAnnouncements(): Flow<Resource<List<Announcement>>>
    suspend fun addMember(member: Member): Resource<Unit>
    suspend fun deleteMember(memberId: String): Resource<Unit>
    suspend fun getMembers(): Flow<Resource<List<Member>>>
}