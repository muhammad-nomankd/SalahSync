package com.durranitech.salahsync.presentation.navigation

import SignInScreen
import android.graphics.pdf.content.PdfPageGotoLinkContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.scene.rememberSceneSetupNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.durranitech.salahsync.domain.model.UserRole
import com.durranitech.salahsync.presentation.auth.screens.SignUpScreen
import com.durranitech.salahsync.presentation.authentication.AuthIntent
import com.durranitech.salahsync.presentation.authentication.screens.SplashScreen
import com.durranitech.salahsync.presentation.authentication.viewModel.AuthViewModel
import com.durranitech.salahsync.presentation.imam.ImamIntent
import com.durranitech.salahsync.presentation.imam.screens.CreateMasjidScreen
import com.durranitech.salahsync.presentation.imam.screens.ImamMainDashboard
import com.durranitech.salahsync.presentation.imam.viewmodel.ImamViewModel
import com.durranitech.salahsync.presentation.muqtadi.screens.MuqtadiDashboard
import com.durranitech.salahsync.presentation.roleselection.screens.RoleSelectionScreen

@Composable
fun MainRoute(paddingValues: PaddingValues) {
    val viewModel: AuthViewModel = viewModel()
    val imamViewModel: ImamViewModel = hiltViewModel()
    val state = viewModel.state.collectAsStateWithLifecycle()

    val backStack = rememberNavBackStack(Destination.SplashScreen)
    LaunchedEffect(Unit) {
        viewModel.decideStartDestination()
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
        backStack = backStack, onBack = { count ->
            repeat(count) { backStack.removeLastOrNull() }
        }, entryDecorators = listOf(
            rememberSceneSetupNavEntryDecorator(),
            rememberSavedStateNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ), entryProvider = entryProvider {
            entry<Destination.RoleSelectionScreen> { entry ->
                RoleSelectionScreen(
                    onRoleSelect = { selectedRole ->
                    backStack.add(Destination.SignInScreen(selectedRole))
                }, paddingValues)
            }
            entry<Destination.SignInScreen> { entry ->
                SignInScreen(
                    role = entry.role ?: UserRole.MUQTADI,
                    onBack = { backStack.removeLastOrNull() },
                    onSwitchToSignUp = { selectedRole ->
                        backStack.add(
                            Destination.SignUpScreen(selectedRole)
                        )
                    },
                    paddingValues,
                    onSwitchToImamDashboard = {
                        backStack.clear()
                        backStack.add(Destination.ImamDashboardScreen)
                    },
                    onSwitchToMuqtadiDashboard = {
                        backStack.clear()
                        backStack.add(Destination.MuqtadiDashboardScreen)
                    })
            }

            entry<Destination.SignUpScreen> { entry ->
                SignUpScreen(
                    role = entry.role ?: UserRole.MUQTADI,
                    onBack = { backStack.removeLastOrNull() },
                    onSwitchToSignIn = { selectedRole ->
                        backStack.add(
                            Destination.SignInScreen(selectedRole)
                        )
                    },
                    paddingValues = paddingValues,
                    onSwitchToImamDashboard = {
                        backStack.clear()
                        backStack.add(Destination.ImamDashboardScreen)
                    },
                    onSwitchToMuqtadiDashboard = {
                        backStack.clear()
                        backStack.add(Destination.MuqtadiDashboardScreen)
                    })
            }

            entry<Destination.MuqtadiDashboardScreen> {
                MuqtadiDashboard(onSignOut = { viewModel.onAuthEvent(AuthIntent.LogOut) }, toRoleSelectionScreen = {
                    backStack.clear()
                    backStack.add(Destination.RoleSelectionScreen)
                })
            }
            entry<Destination.ImamDashboardScreen> {
                ImamMainDashboard(
                    userName = "Noman Khan",
                    userEmail = "mnomankd@gmail.com",
                    onSignOut = {
                        viewModel.onAuthEvent(AuthIntent.LogOut)
                    },
                    paddingValue = paddingValues,
                    onCreateMasjid = { backStack.add(Destination.CreateMasjidScreen) })
            }

            entry<Destination.SplashScreen> {
                SplashScreen()
            }

            entry<Destination.CreateMasjidScreen> {
                CreateMasjidScreen(
                    onBackClick = { backStack.add(Destination.ImamDashboardScreen) },
                    onCreateMasjidClick = { imamViewModel.onImamEvent(ImamIntent.createMasjid(it)) })
            }
        })
}