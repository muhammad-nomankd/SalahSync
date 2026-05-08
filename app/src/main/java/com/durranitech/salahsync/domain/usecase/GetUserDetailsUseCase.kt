package com.durranitech.salahsync.domain.usecase

import com.durranitech.salahsync.domain.repository.AuthRepository

class GetUserDetailsUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke() = repository.getUserDetails()
}