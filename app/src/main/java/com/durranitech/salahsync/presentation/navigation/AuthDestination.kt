package com.durranitech.salahsync.presentation.navigation

import androidx.navigation3.runtime.NavKey
import com.durranitech.salahsync.domain.model.UserRole
import kotlinx.serialization.Serializable

sealed interface AuthDestination : NavKey {

    @Serializable
    data object RoleSelectionScreen: AuthDestination

    @Serializable
    data class SignInScreen(val role: UserRole? = null): AuthDestination

    @Serializable
    data class SignUpScreen(val role: UserRole? = null): AuthDestination


    @Serializable
    data object ImamDashboardScreen: AuthDestination

    @Serializable
    data object MuqtadiDashboardScreen: AuthDestination

}