package com.durranitech.salahsync.presentation.navigation

import SignInScreen
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.scene.rememberSceneSetupNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.durranitech.salahsync.domain.UserRole
import com.durranitech.salahsync.presentation.auth.screens.SignUpScreen
import com.durranitech.salahsync.presentation.roleselection.screens.RoleSelectionScreen

@Composable
fun AuthRoute(startDestination: AuthDestination, paddingValues: PaddingValues) {
    val backStack = rememberNavBackStack(startDestination)
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
                    onSwitchToSignUp = { selectedRole -> backStack.add(AuthDestination.SignUpScreen(selectedRole)) }, // ✅ Pass role
                    paddingValues
                )
            }

            entry<AuthDestination.SignUpScreen> { entry ->
                SignUpScreen(
                    role = entry.role ?: UserRole.MUQTADI,
                    onBack = { backStack.removeLastOrNull() },
                    onSwitchToSignIn = { selectedRole -> backStack.add(AuthDestination.SignInScreen(selectedRole)) }, // ✅ Pass role
                    paddingValues = paddingValues,
                )
            }
        })
}