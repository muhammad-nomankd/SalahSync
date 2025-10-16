package com.durranitech.salahsync.presentation.authentication

import com.durranitech.salahsync.domain.model.User
import com.durranitech.salahsync.domain.model.UserRole
import com.durranitech.salahsync.presentation.navigation.AuthDestination

data class AuthState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val user: User? = null,
    val error: String? = null,
    val message: String? = null,
    val isUserAuthenticated: Boolean? = null,
    val isLoggedOut: Boolean = false,
    val role: UserRole? = null,
    val userId: String? = null,
    val startDestination: AuthDestination? = null
)