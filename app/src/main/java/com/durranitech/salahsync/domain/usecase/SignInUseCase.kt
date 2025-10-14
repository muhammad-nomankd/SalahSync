package com.durranitech.salahsync.domain.usecase

import com.durranitech.salahsync.domain.repository.AuthRepository

class SignInUseCase(private val authRepository: AuthRepository) {
    operator fun invoke(email: String, password: String) =
        authRepository.signIn(email, password)
}
