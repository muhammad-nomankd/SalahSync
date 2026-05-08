package com.durranitech.salahsync.domain.usecase

import com.durranitech.salahsync.domain.model.SalahTime
import com.durranitech.salahsync.domain.repository.MuqtadiRepository
import com.durranitech.salahsync.util.Resource
import javax.inject.Inject

class GetMuqtadiPrayerTimesUseCase @Inject constructor(
    private val repository: MuqtadiRepository
) {
    suspend operator fun invoke(masjidId: String): Resource<SalahTime> {
        return repository.getMasjidPrayerTimes(masjidId)
    }
}