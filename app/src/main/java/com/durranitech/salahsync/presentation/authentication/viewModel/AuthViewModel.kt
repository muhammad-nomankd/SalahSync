package com.durranitech.salahsync.presentation.authentication.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.durranitech.salahsync.domain.model.UserRole
import com.durranitech.salahsync.domain.usecase.GetCurrentUserIdUseCase
import com.durranitech.salahsync.domain.usecase.GetCurrentUserUseCase
import com.durranitech.salahsync.domain.usecase.GetUserRoleUseCase
import com.durranitech.salahsync.domain.usecase.SignInUseCase
import com.durranitech.salahsync.domain.usecase.SignOutUseCase
import com.durranitech.salahsync.domain.usecase.SignUpUseCase
import com.durranitech.salahsync.presentation.authentication.AuthIntent
import com.durranitech.salahsync.presentation.authentication.AuthState
import com.durranitech.salahsync.presentation.navigation.AuthDestination
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
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()


    fun onEvent(event: AuthIntent) {
        when (event) {
            is AuthIntent.SignUp -> signUp(event.name,event.phone,event.email, event.password,event.role)
            is AuthIntent.SignIn -> signIn(email = event.email,event.password)
            AuthIntent.GetCurrentUser -> getCurrentUser()
            AuthIntent.LogOut -> signOut()
        }
    }

    fun signUp(name: String, phone: String, email: String, password: String, role: UserRole) {
        viewModelScope.launch {
            try {
                signUpUseCase(name, phone,email, password,role).collect { result ->
                    when (result) {
                        is Resource.Error -> _state.value = _state.value.copy(
                            error = result.message,
                            isLoading = false,
                            isUserAuthenticated = false
                        )

                        is Resource.Loading -> _state.value =
                            _state.value.copy(isLoading = true, error = null)

                        is Resource.Success -> _state.value = _state.value.copy(
                            isLoading = false,
                            error = null,
                            user = result.data,
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

                    is Resource.Success -> _state.value =
                        _state.value.copy(user = result.data, isLoading = false, error = null)

                    is Resource.Error -> _state.value =
                        _state.value.copy(isLoading = false, error = result.message)
                }
            }
        }

    }

    fun getCurrentUser(){
        viewModelScope.launch{
            getCurrentUserUseCase().collect { result ->
                when(result){
                    is Resource.Loading -> _state.value = _state.value.copy(isLoading = true, error = null)
                    is Resource.Success ->_state.value = _state.value.copy(isLoading = false, error = null, user = result.data)
                    is Resource.Error -> _state.value = _state.value.copy(isLoading = false, error = result.message)
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

    fun getUserRole(userId: String){
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val result = getUserRoleUseCase(userId)
            when(result){
                is Resource.Loading -> _state.value = _state.value.copy(isLoading = true)
                is Resource.Success -> _state.value = _state.value.copy(isLoading = false, role = result.data, error = null )
                is Resource.Error -> _state.value = _state.value.copy(isLoading = false, error = result.message)
            }
        }
    }

    fun getCurrentUserId(){
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            val result = getCurrentUserIdUseCase()
            when(result){
                is Resource.Loading -> _state.value = _state.value.copy(isLoading = true)
                is Resource.Error -> _state.value= _state.value.copy(error = result.message)
                is Resource.Success -> _state.value = _state.value.copy(userId = result.data)
            }


        }
    }

    fun decideStartDestination(firebaseAuth: FirebaseAuth) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val currentUser = firebaseAuth.currentUser
            if (currentUser == null) {
                // User not logged in
                _state.value = _state.value.copy(startDestination = AuthDestination.RoleSelectionScreen)
            } else {
                // Logged in, now get their role
                val result = getUserRoleUseCase(currentUser.uid)
                when (result) {
                    is Resource.Success -> {
                        val destination = when (result.data) {
                            UserRole.IMAM -> AuthDestination.ImamDashboardScreen
                            UserRole.MUQTADI -> AuthDestination.MuqtadiDashboardScreen
                            else -> AuthDestination.RoleSelectionScreen
                        }
                        _state.value = _state.value.copy(startDestination = destination, isLoading = false)
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(
                            startDestination = AuthDestination.RoleSelectionScreen,
                            error = result.message,
                            isLoading = false
                        )
                    }
                    else -> {}
                }
            }
        }
    }

}