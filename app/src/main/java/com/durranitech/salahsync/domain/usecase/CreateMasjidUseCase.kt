package com.durranitech.salahsync.domain.usecase

import com.durranitech.salahsync.domain.model.Masjid
import com.durranitech.salahsync.domain.repository.ImamRepository
import com.durranitech.salahsync.domain.repository.MasjidRepository

class CreateMasjidUseCase(private val repository: ImamRepository) {
    suspend operator fun invoke(masjid: Masjid)=
        repository.createMasjid(masjid)

}