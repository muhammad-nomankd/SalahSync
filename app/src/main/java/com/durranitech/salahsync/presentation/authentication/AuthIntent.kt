package com.durranitech.salahsync.presentation.authentication

import com.durranitech.salahsync.domain.model.UserRole

sealed class AuthIntent {
    data class SignUp(val name: String,val email: String, val password: String,val role: UserRole): AuthIntent()
    data class SignIn(val email: String, val password: String): AuthIntent()
    object LogOut: AuthIntent()
    object GetCurrentUser: AuthIntent()
}