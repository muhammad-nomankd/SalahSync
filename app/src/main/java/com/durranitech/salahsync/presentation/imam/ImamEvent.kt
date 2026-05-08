package com.durranitech.salahsync.presentation.imam

import com.durranitech.salahsync.domain.model.Announcement
import com.durranitech.salahsync.domain.model.Member
import com.durranitech.salahsync.domain.model.SalahTime

sealed class ImamEvent {
    data class UpdatePrayerTimes(val salahTime: SalahTime) : ImamEvent()
    data class AddAnnouncement(val announcement: Announcement) : ImamEvent()
    data class DeleteAnnouncement(val announcementId: String) : ImamEvent()
    data class AddMember(val member: Member) : ImamEvent()
    data class DeleteMember(val memberId: String) : ImamEvent()
    object ClearMessages : ImamEvent()
    object RefreshData : ImamEvent()
}