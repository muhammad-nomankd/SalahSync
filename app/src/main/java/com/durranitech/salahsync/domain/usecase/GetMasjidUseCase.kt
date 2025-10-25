package com.durranitech.salahsync.domain.usecase

import com.durranitech.salahsync.domain.repository.ImamRepository

class GetMasjidUseCase(private val repository: ImamRepository) {
    suspend operator fun invoke() = repository.getMasjid()
}