package com.durranitech.salahsync.domain.usecase

import com.durranitech.salahsync.domain.repository.AuthRepository

class GetUserRoleUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(userId: String) = repository.getUserRole(userId)
}