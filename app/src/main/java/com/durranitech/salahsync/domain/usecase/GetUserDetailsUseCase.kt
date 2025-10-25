package com.durranitech.salahsync.domain.usecase

import com.durranitech.salahsync.domain.repository.AuthRepository

class GetUserDetailsUseCase(private val repository: AuthRepository) {
    operator suspend fun invoke() = repository.getUserDetails()
}