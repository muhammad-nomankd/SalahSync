package com.durranitech.salahsync.domain.repository

import com.durranitech.salahsync.domain.model.Masjid
import com.durranitech.salahsync.util.Resource
import kotlinx.coroutines.flow.Flow

interface ImamRepository {

    suspend fun getMasjid(): Resource<Masjid>
    suspend fun createMasjid(masjid: Masjid): Resource<Unit>

    fun getMasjidDetails(): Flow<Resource<Masjid>>
}