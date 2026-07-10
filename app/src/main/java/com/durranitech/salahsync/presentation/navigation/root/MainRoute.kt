package com.durranitech.salahsync.presentation.navigation.root
import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
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
import com.durranitech.salahsync.presentation.authentication.screens.SignUpScreen
import com.durranitech.salahsync.presentation.authentication.screens.SplashScreen
import com.durranitech.salahsync.presentation.authentication.SignInScreen
import com.durranitech.salahsync.presentation.authentication.viewModel.AuthViewModel
import com.durranitech.salahsync.presentation.imam.ImamEvent
import com.durranitech.salahsync.presentation.imam.components.AddUpdateSalahTimingsScreen
import com.durranitech.salahsync.presentation.imam.components.CreateAnnouncementScreen
import com.durranitech.salahsync.presentation.imam.screens.CreateMasjidScreen
import com.durranitech.salahsync.presentation.imam.screens.ImamMainDashboard
import com.durranitech.salahsync.presentation.imam.screens.MasjidScreen
import com.durranitech.salahsync.presentation.imam.screens.QrScanResult
import com.durranitech.salahsync.presentation.imam.screens.QrScreen
import com.durranitech.salahsync.presentation.imam.screens.ScanQrScreen
import com.durranitech.salahsync.presentation.imam.viewmodel.ImamViewModel
import com.durranitech.salahsync.presentation.muqtadi.screens.JoinMasjidListScreen
import com.durranitech.salahsync.presentation.muqtadi.screens.JoinMasjidScreen
import com.durranitech.salahsync.presentation.muqtadi.screens.MasjidDetailScreen
import com.durranitech.salahsync.presentation.muqtadi.screens.MuqtadiDashboardScreen
import com.durranitech.salahsync.presentation.muqtadi.viewmodel.MuqtadiViewModel
import com.durranitech.salahsync.presentation.navigation.Destination
import com.durranitech.salahsync.presentation.roleselection.screens.RoleSelectionScreen

@Composable
fun MainRoute(paddingValues: PaddingValues) {
    val viewModel: AuthViewModel = hiltViewModel()
    val state = viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val backStack = rememberNavBackStack(Destination.SplashScreen)
    val muqtadiViewModel: MuqtadiViewModel = hiltViewModel()

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
        backStack = backStack, onBack = { backStack.removeLastOrNull() }, entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ), entryProvider = entryProvider {
            entry<Destination.SplashScreen> {
                SplashScreen()
            }

            entry<Destination.RoleSelectionScreen> {
                RoleSelectionScreen(
                    onRoleSelect = { selectedRole ->
                        backStack.add(Destination.SignInScreen(selectedRole))
                    }, paddingValues = paddingValues
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
                    })
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
                    })
            }

            entry<Destination.ImamDashboardScreen> {
                val imamViewModel: ImamViewModel = hiltViewModel()
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
                    onCreateNewAnnouncement = { backStack.add(Destination.OnCreateNewAnnouncement) },
                    onNavigateToUpdateSalahTimes = { backStack.add(Destination.AddOrUpdateSalahTimes) },
                    onShowQrCode = {
                        val masjid = imamViewModel.uiState.value.masjid
                        if (masjid != null && masjid.code.isNotBlank()) {
                            backStack.add(
                                Destination.QrScreen(
                                    masjidCode = masjid.code, masjidName = masjid.name
                                )
                            )
                        }
                    })
            }

            entry<Destination.MasjidScreen> {
                val imamViewModel: ImamViewModel = hiltViewModel()
                MasjidScreen(
                    onEditDetails = { /* TODO: navigate to edit screen */ },
                    onCreateMasjid = { backStack.add(Destination.CreateMasjidScreen) },
                    onLogOut = {
                        viewModel.onAuthEvent(AuthIntent.LogOut)
                        backStack.clear()
                        backStack.add(Destination.RoleSelectionScreen)
                    },
                    onShowQrCode = {
                        val masjid = imamViewModel.uiState.value.masjid
                        if (masjid != null && masjid.code.isNotBlank()) {
                            backStack.add(
                                Destination.QrScreen(
                                    masjidCode = masjid.code, masjidName = masjid.name
                                )
                            )
                        }
                    },
                    viewModel = imamViewModel
                )
            }



            entry<Destination.CreateMasjidScreen> {
                val imamViewModel: ImamViewModel = hiltViewModel()
                CreateMasjidScreen(
                    onBackClick = { backStack.removeLastOrNull() },
                    onCreateMasjidClick = { masjid ->
                        imamViewModel.createMasjid(masjid)
                    })
            }

            entry<Destination.OnCreateNewAnnouncement> {

                val imamViewModel: ImamViewModel = hiltViewModel()
                CreateAnnouncementScreen(
                    state = imamViewModel.uiState.collectAsStateWithLifecycle().value,
                    onNavigateBack = { backStack.removeLastOrNull() },
                    onPostAnnouncement = { title, message ->
                        val announcement = Announcement(
                            title = title, description = message, date = System.currentTimeMillis()
                        )
                        imamViewModel.onImamEvent(ImamEvent.AddAnnouncement(announcement))
                    })
            }

            entry<Destination.AddOrUpdateSalahTimes> {
                AddUpdateSalahTimingsScreen(
                    onNavigateBackToHomeScreen = { backStack.removeLastOrNull() })
            }


            entry<Destination.MuqtadiDashboardScreen> {
                MuqtadiDashboardScreen(
                    paddingValues = paddingValues,
                    onNavigateToRoleSelectionScreen = {
                        backStack.clear()
                        backStack.add(Destination.RoleSelectionScreen)
                    },
                    onNavigateToMasjid = { backStack.add(Destination.MasjidDetailScreenForMuqtadi) },
                    onNavigateToScanQr = { backStack.add(Destination.ScanQrScreen) },      // ← NEW
                    onNavigateToBrowseMasjid = { backStack.add(Destination.JoinMasjidListScreen) }, // ← NEW
                    viewModel = muqtadiViewModel
                )
            }

            entry<Destination.MasjidDetailScreenForMuqtadi> {
                MasjidDetailScreen(onBack = { backStack.removeLastOrNull() })
            }


            entry<Destination.JoinMasjidScreen> {
                JoinMasjidScreen(
                    onJoinMasjid = { backStack.add(Destination.JoinMasjidListScreen) },
                    onScanQr = { backStack.add(Destination.ScanQrScreen) })
            }

            entry<Destination.JoinMasjidListScreen> {
                JoinMasjidListScreen(
                    onMasjidJoined = {
                    backStack.clear()
                    backStack.add(Destination.MuqtadiDashboardScreen)
                }, onBack = { backStack.removeLastOrNull() }, viewModel = muqtadiViewModel
                )
            }

            entry<Destination.QrScreen> { entry ->
                QrScreen(
                    masjidCode = entry.masjidCode,
                    masjidName = entry.masjidName,
                    onBack = { backStack.removeLastOrNull() })
            }

            entry<Destination.ScanQrScreen> {
                ScanQrScreen(onCodeScanned = { result: QrScanResult ->
                    backStack.removeLastOrNull()
                    muqtadiViewModel.joinMasjidByCode(code = result.masjidCode, onSuccess = {
                        backStack.clear()
                        backStack.add(Destination.MuqtadiDashboardScreen)
                    }, onError = { errorMessage ->
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    })
                }, onBack = { backStack.removeLastOrNull() })
            }
        })
}