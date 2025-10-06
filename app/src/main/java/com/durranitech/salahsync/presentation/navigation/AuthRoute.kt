package com.durranitech.salahsync.presentation.navigation

import SignInScreen
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.scene.rememberSceneSetupNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.durranitech.salahsync.domain.UserRole
import com.durranitech.salahsync.presentation.roleselection.screens.RoleSelectionScreen

@Composable
fun AuthRoute(startDestination: AuthDestination) {
    val backStack = rememberNavBackStack(startDestination)
    var userRole by remember { mutableStateOf(UserRole.IMAM) }
    NavDisplay(
        backStack = backStack, onBack = { count ->
            repeat(count) { backStack.removeLastOrNull() }
        }, entryDecorators = listOf(
            rememberSceneSetupNavEntryDecorator(),
            rememberSavedStateNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ), entryProvider = entryProvider {
            entry<AuthDestination.RoleSelectionScreen> {
                RoleSelectionScreen(
                    onRoleSelect = { userRole })
            }
            entry<AuthDestination.SignInScreen> {
                SignInScreen(
                    role = UserRole.IMAM,
                    onBack = { backStack.removeLastOrNull() },
                    onSwitchToSignUp = { backStack.add(AuthDestination.SignUpScreen) },
                    PaddingValues(16.dp)
                )
            }
        })
}