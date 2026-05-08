package com.durranitech.salahsync.domain.usecase
import com.durranitech.salahsync.domain.model.SalahTime
import com.durranitech.salahsync.domain.repository.ImamRepository
import com.durranitech.salahsync.util.Resource
import javax.inject.Inject

class UpdatePrayerTimesUseCase @Inject constructor(
    private val repository: ImamRepository
) {
    suspend operator fun invoke(salahTime: SalahTime): Resource<Unit> {
        return repository.updateMasjidPrayerTimes(salahTime)
    }
}