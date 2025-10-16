package com.durranitech.salahsync.presentation.auth.screens

import android.R.attr.enabled
import android.R.attr.thickness
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults.contentPadding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.durranitech.salahsync.domain.model.UserRole
import com.durranitech.salahsync.presentation.authentication.AuthIntent
import com.durranitech.salahsync.presentation.authentication.viewModel.AuthViewModel
import com.durranitech.salahsync.ui.theme.Dark_Green
import com.durranitech.salahsync.ui.theme.Darker_Indigo
import com.durranitech.salahsync.ui.theme.Medium_Blue
import com.durranitech.salahsync.ui.theme.Text_Dark_Green
import com.durranitech.salahsync.ui.theme.Text_Light_Green

@Composable
fun SignUpScreen(
    role: UserRole,
    onBack: () -> Unit,
    onSwitchToSignIn: (UserRole) -> Unit,
    paddingValues: PaddingValues,
    onSwitchToImamDashboard: () -> Unit,
    onSwitchToMuqtadiDashboard: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val loading = state.value.isLoading

    // Form fields
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }

    // Local validation error
    var localError by remember { mutableStateOf<String?>(null) }

    // Remote errors and success from ViewModel
    val remoteError = state.value.error
    val success = state.value.message

    // Display whichever error exists (local validation or remote Firebase error)
    localError ?: remoteError

    // Navigation after successful signup
    LaunchedEffect(state.value.isUserAuthenticated) {
        val currentState = state.value
        if (currentState.isUserAuthenticated == true && currentState.role != null) {
            when (currentState.role) {
                UserRole.IMAM -> onSwitchToImamDashboard()
                UserRole.MUQTADI -> onSwitchToMuqtadiDashboard()
            }
        }
    }

    val (gradientColors, titleText) = when (role) {
        UserRole.IMAM -> listOf(Medium_Blue, Darker_Indigo) to "Imam Sign Up"
        UserRole.MUQTADI -> listOf(Medium_Blue, Darker_Indigo) to "Muqtadi Sign Up"
    }

    fun validate(): String? {
        if (fullName.isBlank()) return "Full name is required"
        if (email.isBlank()) return "Email is required"
        if (password.isBlank()) return "Password is required"
        if (password.length < 6) return "Password must be at least 6 characters"
        if (password != confirmPassword) return "Passwords do not match"
        return null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Header section
        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 12.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceContainer, shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Back button
            TextButton(onClick = onBack, modifier = Modifier.align(Alignment.Start)) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Text_Light_Green
                )
                Spacer(Modifier.width(4.dp))
                Text("Back to role selection", color = Text_Light_Green, fontSize = 16.sp)
            }

            // Role Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(brush = Brush.linearGradient(gradientColors), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Role Icon",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(Modifier.height(12.dp))
            Text(
                titleText,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Medium,
                color = Text_Dark_Green
            )
            Text(
                "Join the SalahSync community", fontSize = 14.sp, color = Text_Light_Green
            )

            // Form
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                if (localError != null) {
                    Text(
                        text = localError!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.errorContainer, RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (success != null) {
                    Text(
                        text = success,
                        color = Color(0xFF2E7D32),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFD0F0C0), RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                if (remoteError != null) {
                    Text(
                        text = remoteError,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.errorContainer, RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }



                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name *") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Person, contentDescription = null, tint = Text_Light_Green
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Text_Light_Green, unfocusedBorderColor = Dark_Green
                    ),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                    )
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address *") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Email, contentDescription = null, tint = Text_Light_Green
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email, imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Text_Light_Green, unfocusedBorderColor = Dark_Green
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password *") },
                    singleLine = true,
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = Text_Light_Green
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Text_Light_Green, unfocusedBorderColor = Dark_Green
                    ),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password, imeAction = ImeAction.Next
                    )
                )

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password *") },
                    singleLine = true,
                    visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                            Icon(
                                imageVector = if (showConfirmPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = Text_Light_Green
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Text_Light_Green, unfocusedBorderColor = Dark_Green
                    ),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password, imeAction = ImeAction.Done
                    )
                )

                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        val role = role
                        val validationError = validate()

                        if (validationError != null) {
                            localError = validationError
                            return@Button
                        }
                        if (password.length < 8){
                            localError = "Password must be at least 8 characters long"
                            return@Button
                        }
                        if (password != confirmPassword){
                            localError = "Passwords do not match"
                            return@Button
                        }
                        localError = null
                        viewModel.onEvent(
                            AuthIntent.SignUp(
                                fullName.trim(),
                                email.trim(),
                                password.trim(),
                                role
                            )
                        )



                    },
                    enabled = !loading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .background(
                                brush = Brush.horizontalGradient(gradientColors),
                                shape = MaterialTheme.shapes.medium
                            ), contentAlignment = Alignment.Center
                    ) {
                        if (loading) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Create ${role.name} Account", color = Color.White)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 1.dp,
                color = Text_Light_Green.copy(alpha = 0.6f)
            )

            TextButton(onClick = { onSwitchToSignIn(role) }) {
                Text(
                    buildAnnotatedString {
                        append("Already have an account?")
                        withStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Bold, color = Text_Dark_Green
                            )
                        ) {
                            append(" Sign in as ${role.name}")
                        }
                    }, textAlign = TextAlign.Center, color = Text_Light_Green
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            "Empowering Masajid, strengthening our Ummah",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            color = Text_Light_Green.copy(0.6f),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
fun SignUpScreenPreview() {
    SignUpScreen(
        role = UserRole.IMAM,
        onBack = {},
        onSwitchToSignIn = {},
        paddingValues = PaddingValues(16.dp),
        onSwitchToImamDashboard = {},
        onSwitchToMuqtadiDashboard = {})
}
