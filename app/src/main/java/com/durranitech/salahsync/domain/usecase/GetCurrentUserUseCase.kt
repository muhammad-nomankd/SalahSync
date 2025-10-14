package com.durranitech.salahsync.domain.usecase

import com.durranitech.salahsync.domain.repository.AuthRepository

class GetCurrentUserUseCase(private val repository: AuthRepository) {
    operator fun invoke() = repository.getCurrentUser()
}