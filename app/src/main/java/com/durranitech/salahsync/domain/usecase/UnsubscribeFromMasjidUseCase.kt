package com.durranitech.salahsync.domain.usecase

import com.durranitech.salahsync.domain.repository.NotificationRepository
import javax.inject.Inject

class UnsubscribeFromMasjidUseCase @Inject constructor(private val repository: NotificationRepository) {
    suspend operator fun invoke(masjidId: String) = repository.unsubscribeFromMasjid(masjidId)
}