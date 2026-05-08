package com.durranitech.salahsync.domain.repository

import com.durranitech.salahsync.util.Resource

interface NotificationRepository {

    suspend fun subscribeToMasjid(masjidId: String): Resource<Unit>
    suspend fun unsubscribeFromMasjid(masjidId: String): Resource<Unit>
}