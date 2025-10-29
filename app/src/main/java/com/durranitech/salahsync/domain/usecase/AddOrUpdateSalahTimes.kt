package com.durranitech.salahsync.domain.usecase

import com.durranitech.salahsync.domain.model.SalahTime
import com.durranitech.salahsync.domain.repository.ImamRepository

class AddOrUpdateSalahTimesUseCase(private val repository: ImamRepository) {
    suspend operator fun invoke(salahTimes: SalahTime) = repository.updateMasjidPrayerTimes(salahTimes  )
}