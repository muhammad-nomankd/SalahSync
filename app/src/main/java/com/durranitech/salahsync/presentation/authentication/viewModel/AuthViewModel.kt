package com.durranitech.salahsync.presentation.authentication.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.durranitech.salahsync.domain.model.UserRole
import com.durranitech.salahsync.domain.usecase.GetCurrentUserIdUseCase
import com.durranitech.salahsync.domain.usecase.GetCurrentUserUseCase
import com.durranitech.salahsync.domain.usecase.GetUserDetailsUseCase
import com.durranitech.salahsync.domain.usecase.GetUserRoleUseCase
import com.durranitech.salahsync.domain.usecase.SignInUseCase
import com.durranitech.salahsync.domain.usecase.SignOutUseCase
import com.durranitech.salahsync.domain.usecase.SignUpUseCase
import com.durranitech.salahsync.domain.usecase.UnsubscribeFromMasjidUseCase
import com.durranitech.salahsync.presentation.authentication.AuthIntent
import com.durranitech.salahsync.presentation.authentication.AuthState
import com.durranitech.salahsync.presentation.navigation.Destination
import com.durranitech.salahsync.util.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getUserRoleUseCase: GetUserRoleUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val getUserDetailsUseCase: GetUserDetailsUseCase,
    private val firebaseAuth: FirebaseAuth,
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()

    fun onAuthEvent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.SignUp -> signUp(intent.name, intent.email, intent.password, intent.role)
            is AuthIntent.SignIn -> signIn(intent.email, intent.password)
            AuthIntent.GetCurrentUser -> getCurrentUser()
            AuthIntent.LogOut -> signOut()
        }
    }

    fun signUp(name: String,  email: String, password: String, role: UserRole) {
        viewModelScope.launch {
            try {
                signUpUseCase(name, email, password, role).collect { result ->
                    when (result) {
                        is Resource.Error -> _state.value = _state.value.copy(
                            error = result.message, isLoading = false, isUserAuthenticated = false
                        )

                        is Resource.Loading -> _state.value =
                            _state.value.copy(isLoading = true, error = null)

                        is Resource.Success -> _state.value = _state.value.copy(
                            isLoading = false,
                            error = null,
                            userId = result.data.id,
                            masjid = result.data.masjid,
                            masjidId = result.data.masjid?.id,
                            user = result.data,
                            role = result.data.role,
                            isUserAuthenticated = true,
                            message = "Sign Up Successful"
                        )
                    }
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.localizedMessage)
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            signInUseCase(email, password).collect { result ->
                when (result) {
                    is Resource.Loading -> _state.value =
                        _state.value.copy(isLoading = true, error = null)

                    is Resource.Success -> {
                        val user = result.data
                        _state.value = _state.value.copy(
                            user = user, isLoading = false, error = null, success = true, masjid = result.data.masjid,
                            masjidId = result.data.masjid?.id, userId = result.data.id
                        )

                        user.let {
                            if (it.role != null) {
                                _state.value = _state.value.copy(
                                    role = it.role, isUserAuthenticated = true
                                )
                            } else {
                                getUserRole(it.id)
                            }
                        }
                    }

                    is Resource.Error -> _state.value =
                        _state.value.copy(isLoading = false, error = result.message)
                }
            }
        }
    }

    fun getCurrentUser() {
        viewModelScope.launch {
            getCurrentUserUseCase().collect { result ->
                when (result) {
                    is Resource.Loading -> _state.value =
                        _state.value.copy(isLoading = true, error = null)

                    is Resource.Success -> _state.value = _state.value.copy(
                        isLoading = false, error = null, user = result.data
                    )

                    is Resource.Error -> _state.value = _state.value.copy(
                        isLoading = false, error = result.message
                    )
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            signOutUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> _state.value = _state.value.copy(
                        user = null,
                        role = null,
                        isUserAuthenticated = false,
                        isLoading = false,
                        error = null
                    )

                    is Resource.Loading -> _state.value =
                        _state.value.copy(isLoading = true, error = null)

                    is Resource.Error -> _state.value =
                        _state.value.copy(isLoading = false, error = result.message)
                }
            }
        }
    }

    fun refreshUser() {
        viewModelScope.launch {
            getUserDetailsUseCase().collect { result ->
                when (result) {
                    is Resource.Loading -> { }
                    is Resource.Success -> _state.value = _state.value.copy(
                        user = result.data,
                        error = null
                    )
                    is Resource.Error -> { }
                }
            }
        }
    }

    fun getUserRole(userId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            when (val result = getUserRoleUseCase(userId)) {
                is Resource.Loading -> _state.value = _state.value.copy(isLoading = true)
                is Resource.Success -> _state.value = _state.value.copy(
                    isLoading = false,
                    role = result.data,
                    error = null,
                    isUserAuthenticated = true
                )

                is Resource.Error -> _state.value = _state.value.copy(
                    isLoading = false, error = result.message
                )
            }
        }
    }

    fun getCurrentUserId() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            when (val result = getCurrentUserIdUseCase()) {
                is Resource.Loading -> _state.value = _state.value.copy(isLoading = true)
                is Resource.Error -> _state.value =
                    _state.value.copy(error = result.message, isLoading = false)

                is Resource.Success -> _state.value = _state.value.copy(
                    userId = result.data, isLoading = false
                )
            }
        }
    }
    fun fetchUserAndDecideDestination() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            getUserDetailsUseCase().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isLoading = true, error = null)
                    }
                    is Resource.Success -> {
                        val user = result.data
                        val destination = when (user.role) {
                            UserRole.IMAM -> Destination.ImamDashboardScreen
                            UserRole.MUQTADI -> Destination.MuqtadiDashboardScreen
                            else -> Destination.RoleSelectionScreen
                        }
                        _state.value = _state.value.copy(
                            isLoading = false,
                            user = user,
                            role = user.role,
                            startDestination = destination,
                            error = null
                        )
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            startDestination = Destination.RoleSelectionScreen,
                            error = result.message
                        )
                    }
                }
            }
        }
    }
}