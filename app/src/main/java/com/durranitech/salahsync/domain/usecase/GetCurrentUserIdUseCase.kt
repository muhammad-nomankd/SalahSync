package com.durranitech.salahsync.domain.usecase

import com.durranitech.salahsync.domain.repository.AuthRepository

class  GetCurrentUserIdUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke() = repository.getCurrentUserId()
}