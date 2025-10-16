package com.durranitech.salahsync.domain.usecase

import com.durranitech.salahsync.domain.model.UserRole
import com.durranitech.salahsync.domain.repository.AuthRepository

class SignUpUseCase(private val repository: AuthRepository) {
    operator fun invoke(name: String,  email: String, password: String,role: UserRole) =
        repository.signUp(name,email, password,role)

}