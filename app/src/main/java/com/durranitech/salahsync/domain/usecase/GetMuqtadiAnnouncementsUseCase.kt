package com.durranitech.salahsync.domain.usecase

import com.durranitech.salahsync.domain.repository.MuqtadiRepository
import jakarta.inject.Inject

class GetMuqtadiAnnouncementsUseCase (private val repository: MuqtadiRepository) {
    suspend operator fun invoke(imamId: String) = repository.getAnnouncements(imamId)
}