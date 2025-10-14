package com.durranitech.salahsync.domain.usecase

import com.durranitech.salahsync.domain.repository.AuthRepository
import com.durranitech.salahsync.util.Resource
import kotlinx.coroutines.flow.Flow

class SignOutUseCase(private val repository: AuthRepository) {
     operator fun invoke()=
        repository.signOut()

}