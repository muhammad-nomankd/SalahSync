package com.durranitech.salahsync.domain.usecase

import com.durranitech.salahsync.domain.model.Announcement
import com.durranitech.salahsync.domain.repository.ImamRepository

class AddAnnouncementUseCase(private val repository: ImamRepository) {
    operator suspend fun invoke(announcement: Announcement) = repository.addAnnouncements(announcement)
}