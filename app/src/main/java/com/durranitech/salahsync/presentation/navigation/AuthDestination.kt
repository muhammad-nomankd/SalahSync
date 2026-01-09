package com.durranitech.salahsync.presentation.navigation

import androidx.navigation3.runtime.NavKey
import com.durranitech.salahsync.domain.model.UserRole
import kotlinx.serialization.Serializable

sealed interface AuthDestination : NavKey {

    @Serializable
    data object RoleSelectionScreen: Destination

    @Serializable
    data class SignInScreen(val role: UserRole? = null): Destination

    @Serializable
    data class SignUpScreen(val role: UserRole? = null): Destination


    @Serializable
    data object ImamDashboardScreen: Destination

    @Serializable
    data object MuqtadiDashboardScreen: Destination

    @Serializable
    data object SplashScreen: Destination

    @Serializable
    data object CreateMasjidScreen: Destination

}