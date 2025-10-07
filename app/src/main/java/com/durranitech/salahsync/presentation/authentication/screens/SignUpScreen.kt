package com.durranitech.salahsync.presentation.auth.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.durranitech.salahsync.domain.UserRole
import com.durranitech.salahsync.ui.theme.*

@Composable
fun SignUpScreen(
    role: UserRole,
    onBack: () -> Unit,
    onSwitchToSignIn: (UserRole) -> Unit,
    paddingValues: PaddingValues
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }

    var error by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

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
                    MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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
                "Join the SalahSync community",
                fontSize = 14.sp,
                color = Text_Light_Green
            )

            // Form
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                if (error != null) {
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.errorContainer, RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (success != null) {
                    Text(
                        text = success!!,
                        color = Color(0xFF2E7D32),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFD0F0C0), RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name *") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Text_Light_Green) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Text_Light_Green,
                        unfocusedBorderColor = Dark_Green
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address *") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Text_Light_Green) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Text_Light_Green,
                        unfocusedBorderColor = Dark_Green
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number (Optional)") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Text_Light_Green) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Text_Light_Green,
                        unfocusedBorderColor = Dark_Green
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
                        focusedBorderColor = Text_Light_Green,
                        unfocusedBorderColor = Dark_Green
                    ),
                    shape = RoundedCornerShape(12.dp)
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
                        focusedBorderColor = Text_Light_Green,
                        unfocusedBorderColor = Dark_Green
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        val validationError = validate()
                        if (validationError != null) {
                            error = validationError
                            return@Button
                        }
                        error = null
                        loading = true
                        success = "Account created successfully!"
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
                            ),
                        contentAlignment = Alignment.Center
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

            Divider(
                thickness = 1.dp,
                color = Text_Light_Green.copy(alpha = 0.6f),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            TextButton(onClick = { onSwitchToSignIn(role) }) {
                Text(
                    buildAnnotatedString {
                        append("Already have an account?")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Text_Dark_Green)) {
                            append(" Sign in as ${role.name}")
                        }
                    },
                    textAlign = TextAlign.Center,
                    color = Text_Light_Green
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
        paddingValues = PaddingValues(16.dp)
    )
}
