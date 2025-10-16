package com.durranitech.salahsync.presentation.navigation

import SignInScreen
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.durranitech.salahsync.presentation.authentication.screens.SplashScreen
import com.durranitech.salahsync.presentation.authentication.viewModel.AuthViewModel
import com.durranitech.salahsync.presentation.imam.screens.ImamDashboardScreen
import com.durranitech.salahsync.presentation.muqtadi.screens.MuqtadiDashboard
import com.durranitech.salahsync.presentation.roleselection.screens.RoleSelectionScreen

@Composable
fun MainRoute(paddingValues: PaddingValues) {
    val viewModel: AuthViewModel = viewModel()
    val state = viewModel.state.collectAsStateWithLifecycle()

    val backStack = rememberNavBackStack(AuthDestination.SplashScreen)
    LaunchedEffect(Unit) {
        viewModel.decideStartDestination()
    }

    LaunchedEffect(state.value.startDestination) {
        state.value.startDestination?.let { destination ->
            backStack.clear()
            backStack.add(destination)
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
            entry<AuthDestination.RoleSelectionScreen> { entry ->
                RoleSelectionScreen(
                    onRoleSelect = { selectedRole ->
                    backStack.add(AuthDestination.SignInScreen(selectedRole))
                }, paddingValues)
            }
            entry<AuthDestination.SignInScreen> { entry ->
                SignInScreen(
                    role = entry.role ?: UserRole.MUQTADI,
                    onBack = { backStack.removeLastOrNull() },
                    onSwitchToSignUp = { selectedRole ->
                        backStack.add(
                            AuthDestination.SignUpScreen(selectedRole)
                        )
                    },
                    paddingValues,
                    onSwitchToImamDashboard = { backStack.add(AuthDestination.ImamDashboardScreen) },
                    onSwitchToMuqtadiDashboard = { backStack.add(AuthDestination.MuqtadiDashboardScreen) })
            }

            entry<AuthDestination.SignUpScreen> { entry ->
                SignUpScreen(
                    role = entry.role ?: UserRole.MUQTADI,
                    onBack = { backStack.removeLastOrNull() },
                    onSwitchToSignIn = { selectedRole ->
                        backStack.add(
                            AuthDestination.SignInScreen(selectedRole)
                        )
                    },
                    paddingValues = paddingValues,
                    onSwitchToImamDashboard = { backStack.add(AuthDestination.ImamDashboardScreen) },
                    onSwitchToMuqtadiDashboard = { backStack.add(AuthDestination.MuqtadiDashboardScreen) })
            }

            entry<AuthDestination.MuqtadiDashboardScreen> {
                MuqtadiDashboard(toReleSelectionScreen = { backStack.clear()
                    backStack.add(AuthDestination.RoleSelectionScreen) })
            }
            entry<AuthDestination.ImamDashboardScreen> {
                ImamDashboardScreen(
                    userName = "Noman Khan",
                    userEmail = "mnomankd@gmail.com",
                    onSignOut = { backStack.add(AuthDestination.SignInScreen(UserRole.MUQTADI)) },
                    paddingValues = paddingValues
                )
            }

            entry<AuthDestination.SplashScreen> {
                SplashScreen()
            }
        })
}