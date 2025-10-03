package com.durranitech.salahsync.presentation.authentication

import com.durranitech.salahsync.domain.User

sealed class AuthState {
    data object Idle: AuthState()
    data object Loading: AuthState()
    data class SignedIn(val user: User): AuthState()
    data class SignedUp(val user: User): AuthState()
    data class Error(val message: String): AuthState()

}