package com.durranitech.salahsync.presentation.navigation.root

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.durranitech.salahsync.domain.model.Announcement
import com.durranitech.salahsync.domain.model.UserRole
import com.durranitech.salahsync.presentation.authentication.AuthIntent
import com.durranitech.salahsync.presentation.authentication.screens.com.durranitech.salahsync.presentation.authentication.SignInScreen
import com.durranitech.salahsync.presentation.authentication.screens.SignUpScreen
import com.durranitech.salahsync.presentation.authentication.screens.SplashScreen
import com.durranitech.salahsync.presentation.authentication.viewModel.AuthViewModel
import com.durranitech.salahsync.presentation.imam.ImamEvent
import com.durranitech.salahsync.presentation.imam.components.AddUpdateSalahTimingsScreen
import com.durranitech.salahsync.presentation.imam.components.CreateAnnouncementScreen
import com.durranitech.salahsync.presentation.imam.screens.CreateMasjidScreen
import com.durranitech.salahsync.presentation.imam.screens.ImamMainDashboard
import com.durranitech.salahsync.presentation.imam.screens.MasjidScreen
import com.durranitech.salahsync.presentation.imam.viewmodel.ImamViewModel
import com.durranitech.salahsync.presentation.muqtadi.screens.JoinMasjidListScreen
import com.durranitech.salahsync.presentation.muqtadi.screens.JoinMasjidScreen
import com.durranitech.salahsync.presentation.muqtadi.screens.MuqtadiDashboardScreen
import com.durranitech.salahsync.presentation.muqtadi.viewmodel.MuqtadiViewModel
import com.durranitech.salahsync.presentation.navigation.Destination
import com.durranitech.salahsync.presentation.roleselection.screens.RoleSelectionScreen

@Composable
fun MainRoute(paddingValues: PaddingValues) {
    val viewModel: AuthViewModel = hiltViewModel()
    val state = viewModel.state.collectAsStateWithLifecycle()

    val backStack = rememberNavBackStack(Destination.SplashScreen)
    val imamViewModel: ImamViewModel = hiltViewModel()
    val muqtadiViewModel: MuqtadiViewModel = hiltViewModel()



    LaunchedEffect(Unit) {
        Log.d("SalahTime", "MainRoute: LaunchedEffect called")
    }
    LaunchedEffect(Unit) {
        viewModel.fetchUserAndDecideDestination()
    }

    LaunchedEffect(state.value.startDestination) {
        state.value.startDestination?.let { destination ->
            backStack.clear()
            backStack.add(destination)
        }
    }

    LaunchedEffect(state.value.isUserAuthenticated) {
        if (state.value.isUserAuthenticated == false) {
            backStack.clear()
            backStack.add(Destination.RoleSelectionScreen)

        }
    }

    NavDisplay(
        backStack = backStack,
        onBack = {
            backStack.removeLastOrNull()
        },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Destination.RoleSelectionScreen> {
                RoleSelectionScreen(
                    onRoleSelect = { selectedRole ->
                        backStack.add(Destination.SignInScreen(selectedRole))
                    },
                    paddingValues = paddingValues
                )
            }
            entry<Destination.SignInScreen> { entry ->
                SignInScreen(
                    role = entry.role ?: UserRole.MUQTADI,
                    onBack = { backStack.removeLastOrNull() },
                    onSwitchToSignUp = { selectedRole ->
                        backStack.add(Destination.SignUpScreen(selectedRole))
                    },
                    paddingValues = paddingValues,
                    onSwitchToImamDashboard = {
                        backStack.clear()
                        backStack.add(Destination.ImamDashboardScreen)
                    },
                    onSwitchToMuqtadiDashboard = {
                        backStack.clear()
                        backStack.add(Destination.MuqtadiDashboardScreen)
                    }
                )
            }

            entry<Destination.SignUpScreen> { entry ->
                SignUpScreen(
                    role = entry.role ?: UserRole.MUQTADI,
                    onBack = { backStack.removeLastOrNull() },
                    onSwitchToSignIn = { selectedRole ->
                        backStack.add(Destination.SignInScreen(selectedRole))
                    },
                    paddingValues = paddingValues,
                    onSwitchToImamDashboard = {
                        backStack.clear()
                        backStack.add(Destination.ImamDashboardScreen)
                    },
                    onSwitchToMuqtadiDashboard = {
                        backStack.clear()
                        backStack.add(Destination.MuqtadiDashboardScreen)
                    }
                )
            }
            entry<Destination.MuqtadiDashboardScreen> {
                MuqtadiDashboardScreen(
                    paddingValues = paddingValues,
                    onNavigateToAnnouncements = {},
                    onNavigateToRoleSelectionScreen = {
                        backStack.clear()
                        backStack.add(Destination.RoleSelectionScreen)
                    },
                    viewModel = muqtadiViewModel,
                    onNavigateToMasjid = {
                        backStack.add(Destination.MasjidScreen)
                    }
                )
            }

            entry<Destination.MasjidScreen> {
                MasjidScreen()
            }
            entry<Destination.JoinMasjidScreen> {
                JoinMasjidScreen(
                    onJoinMasjid = { backStack.add(Destination.JoinMasjidListScreen) },

                    )

            }

            entry<Destination.OnCreateNewAnnouncement> {
                CreateAnnouncementScreen(
                    state = imamViewModel.uiState.collectAsStateWithLifecycle().value,
                    onNavigateBack = {backStack.removeLastOrNull()},
                    onPostAnnouncement = {
                        title, message ->
                        val announcement = Announcement(title = title, description = message, date = System.currentTimeMillis())
                        imamViewModel.onImamEvent(
                            ImamEvent.AddAnnouncement(
                                announcement
                            )
                        )
                    })
            }

            entry<Destination.JoinMasjidListScreen> {
                JoinMasjidListScreen(
                    onMasjidJoined = {
                        backStack.clear()
                        backStack.add(Destination.MuqtadiDashboardScreen)
                    },
                    onBack = { backStack.removeLastOrNull() },
                    viewModel = muqtadiViewModel
                )
            }

            entry<Destination.ImamDashboardScreen> {
                ImamMainDashboard(
                    userName = state.value.user?.name ?: "User",
                    userEmail = state.value.user?.email ?: "",
                    onLogOut = {
                        viewModel.onAuthEvent(AuthIntent.LogOut)
                        backStack.clear()
                        backStack.add(Destination.RoleSelectionScreen)
                    },
                    paddingValue = paddingValues,
                    onCreateMasjid = { backStack.add(Destination.CreateMasjidScreen) },
                    onCreateNewAnnouncement = {backStack.add(Destination.OnCreateNewAnnouncement)},
                    onNavigateToUpdateSalahTimes = {backStack.add(Destination.AddOrUpdateSalahTimes)}
                )
            }

            entry<Destination.AddOrUpdateSalahTimes> {
                AddUpdateSalahTimingsScreen(onNavigateBackToHomeScreen = {backStack.removeLastOrNull()})
            }

            entry<Destination.SplashScreen> {
                SplashScreen()
            }

            entry<Destination.CreateMasjidScreen> {
                CreateMasjidScreen(
                    onBackClick = { backStack.removeLastOrNull() },
                    onCreateMasjidClick = { masjid ->
                        imamViewModel.createMasjid(masjid)
                    }
                )
            }
        }
    )
}
