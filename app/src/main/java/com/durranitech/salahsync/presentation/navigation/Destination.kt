package com.durranitech.salahsync.presentation.navigation

import androidx.navigation3.runtime.NavKey
import com.durranitech.salahsync.domain.model.UserRole
import kotlinx.serialization.Serializable

sealed interface Destination : NavKey {

    @Serializable
    data object RoleSelectionScreen : Destination

    @Serializable
    data class SignInScreen(val role: UserRole? = null) : Destination

    @Serializable
    data class SignUpScreen(val role: UserRole? = null) : Destination

    @Serializable
    data object JoinMasjidScreen : Destination


    @Serializable
    data object ImamDashboardScreen : Destination

    @Serializable
    data object AddOrUpdateSalahTimes : Destination

    @Serializable
    data object MuqtadiDashboardScreen : Destination

    @Serializable
    data object MasjidScreen : Destination

    @Serializable
    data object SplashScreen : Destination

    @Serializable
    data object OnCreateNewAnnouncement : Destination

    @Serializable
    data object CreateMasjidScreen : Destination

    @Serializable
    data object JoinMasjidListScreen : Destination

    @Serializable
    data object MasjidDetailScreenForMuqtadi : Destination

    @Serializable
    data class QrScreen(val masjidCode: String, val masjidName: String) : Destination
    @Serializable
    object ScanQrScreen : Destination
}