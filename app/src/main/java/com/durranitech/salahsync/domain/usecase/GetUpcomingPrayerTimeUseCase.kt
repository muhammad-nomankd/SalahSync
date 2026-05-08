package com.durranitech.salahsync.domain.usecase

import com.durranitech.salahsync.domain.model.SalahTime
import com.durranitech.salahsync.domain.repository.ImamRepository
import com.durranitech.salahsync.util.Resource

class GetUpcomingPrayerTimeUseCase(private val repository: ImamRepository) {
    suspend operator fun invoke(): Resource<SalahTime>{
       return repository.getMasjidPrayerTimes()
    }
}