package com.durranitech.salahsync.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface AuthDestination : NavKey {

    @Serializable
    data object RoleSelectionScreen: AuthDestination

    @Serializable
    data object SignInScreen

    @Serializable
    data object SignUpScreen

}