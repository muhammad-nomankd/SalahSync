package com.durranitech.salahsync.presentation.authentication.screens

import android.util.Patterns
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.durranitech.salahsync.domain.model.UserRole
import com.durranitech.salahsync.presentation.authentication.AuthIntent
import com.durranitech.salahsync.presentation.authentication.viewModel.AuthViewModel

private val GreenDeep       = Color(0xFF065F46)
private val GreenPrimary    = Color(0xFF1B8C5E)
private val GreenLight      = Color(0xFF26A874)
private val GreenSurface    = Color(0xFFE8F5EF)
private val GreenGlow       = Color(0xFF34D399)
private val IndigoPrimary   = Color(0xFF3730A3)
private val IndigoLight     = Color(0xFF6366F1)
private val IndigoSurface   = Color(0xFFEEF2FF)

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
    val state   by viewModel.state.collectAsStateWithLifecycle()
    val loading  = state.isLoading

    var fullName        by remember { mutableStateOf("") }
    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword    by remember { mutableStateOf(false) }
    var showConfirm     by remember { mutableStateOf(false) }
    var localError      by remember { mutableStateOf<String?>(null) }

    val primary   = if (role == UserRole.IMAM) GreenPrimary  else IndigoPrimary
    val light     = if (role == UserRole.IMAM) GreenLight    else IndigoLight
    val surface   = if (role == UserRole.IMAM) GreenSurface  else IndigoSurface
    val glow      = if (role == UserRole.IMAM) GreenGlow     else IndigoLight
    val roleLabel = if (role == UserRole.IMAM) "Imam"        else "Muqtadi"
    val roleIcon  = if (role == UserRole.IMAM) Icons.Outlined.Person else Icons.Outlined.Groups

    val focusManager    = LocalFocusManager.current
    val emailFocus      = remember { FocusRequester() }
    val passwordFocus   = remember { FocusRequester() }
    val confirmFocus    = remember { FocusRequester() }

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val headerAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(500), label = "h_alpha"
    )
    val formAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(500, delayMillis = 150), label = "f_alpha"
    )
    val formOffset by animateFloatAsState(
        targetValue = if (visible) 0f else 24f,
        animationSpec = tween(500, delayMillis = 150, easing = EaseOutCubic), label = "f_offset"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 0.45f,
        animationSpec = infiniteRepeatable(tween(2200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glow_alpha"
    )

    LaunchedEffect(state.isUserAuthenticated, state.role) {
        if (state.isUserAuthenticated == true && state.role != null) {
            when (state.role) {
                UserRole.IMAM    -> onSwitchToImamDashboard()
                UserRole.MUQTADI -> onSwitchToMuqtadiDashboard()
                else             -> Unit
            }
        }
    }

    fun validate(): String? {
        if (fullName.isBlank())  return "Full name is required"
        if (email.isBlank())     return "Email is required"
        if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) return "Enter a valid email address"
        if (password.isBlank())  return "Password is required"
        if (password.length < 8) return "Password must be at least 8 characters"
        if (password != confirmPassword) return "Passwords do not match"
        return null
    }

    fun submit() {
        focusManager.clearFocus()
        val err = validate()
        if (err != null) { localError = err; return }
        localError = null
        viewModel.onAuthEvent(AuthIntent.SignUp(fullName.trim(), email.trim(), password, role))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier.size(260.dp).align(Alignment.TopStart)
                .blur(70.dp).background(glow.copy(alpha = glowAlpha * 0.25f), CircleShape)
        )
        Box(
            modifier = Modifier.size(200.dp).align(Alignment.BottomEnd)
                .blur(60.dp).background(primary.copy(alpha = 0.12f), CircleShape)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(20.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.alpha(headerAlpha)
            ) {
                TextButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.Start),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier.size(32.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, null, modifier = Modifier.size(16.dp))
                    }
                    Spacer(Modifier.width(8.dp))
                    Text("Role selection", style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                }

                Spacer(Modifier.height(20.dp))

                Box(contentAlignment = Alignment.Center) {
                    Box(Modifier.size(96.dp).background(primary.copy(alpha = glowAlpha * 0.15f), CircleShape))
                    Box(Modifier.size(72.dp).background(primary.copy(alpha = 0.1f), CircleShape))
                    Box(
                        modifier = Modifier.size(54.dp).background(primary, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(roleIcon, null, tint = Color.White, modifier = Modifier.size(26.dp))
                    }
                }

                Spacer(Modifier.height(14.dp))

                Row(
                    modifier = Modifier
                        .background(surface, RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(Modifier.size(5.dp).background(light, CircleShape))
                    Text(roleLabel, style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold, color = primary)
                }

                Spacer(Modifier.height(10.dp))

                Text("Create your account", style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
                Text("Join the SalahSync community", style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f))
            }

            Spacer(Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .alpha(formAlpha)
                    .padding(top = formOffset.dp)
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val errorMsg = localError ?: state.error
                AnimatedVisibility(
                    visible = errorMsg != null,
                    enter   = fadeIn(tween(200)) + slideInVertically(tween(200)) { -it }
                ) {
                    errorMsg?.let {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .background(MaterialTheme.colorScheme.errorContainer, RoundedCornerShape(10.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.Info, null, tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(15.dp))
                            Text(it, style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error)
                        }
                    }
                }

                AnimatedVisibility(
                    visible = state.message != null,
                    enter   = fadeIn(tween(200)) + slideInVertically(tween(200)) { -it }
                ) {
                    state.message?.let {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .background(Color(0xFFF0FDF4), RoundedCornerShape(10.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.CheckCircle, null, tint = Color(0xFF059669),
                                modifier = Modifier.size(15.dp))
                            Text(it, style = MaterialTheme.typography.bodySmall, color = Color(0xFF065F46))
                        }
                    }
                }

                SignUpTextField(
                    value = fullName, onValueChange = { fullName = it; localError = null },
                    label = "Full name", placeholder = "e.g. Abdullah Khan",
                    leadingIcon = Icons.Outlined.Person, accentColor = primary,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { emailFocus.requestFocus() })
                )

                SignUpTextField(
                    value = email, onValueChange = { email = it; localError = null },
                    label = "Email address", placeholder = "you@gmail.com",
                    leadingIcon = Icons.Outlined.Email, accentColor = primary,
                    modifier = Modifier.focusRequester(emailFocus),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { passwordFocus.requestFocus() })
                )

                SignUpTextField(
                    value = password, onValueChange = { password = it; localError = null },
                    label = "Password", placeholder = "Min. 8 characters",
                    leadingIcon = Icons.Outlined.Lock, accentColor = primary,
                    modifier = Modifier.focusRequester(passwordFocus),
                    isPassword = true, showPassword = showPassword,
                    onTogglePassword = { showPassword = !showPassword },
                    isError = localError?.contains("match") == true || localError?.contains("8") == true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { confirmFocus.requestFocus() })
                )

                SignUpTextField(
                    value = confirmPassword, onValueChange = { confirmPassword = it; localError = null },
                    label = "Confirm password", placeholder = "Repeat your password",
                    leadingIcon = Icons.Outlined.Lock, accentColor = primary,
                    modifier = Modifier.focusRequester(confirmFocus),
                    isPassword = true, showPassword = showConfirm,
                    onTogglePassword = { showConfirm = !showConfirm },
                    isError = localError?.contains("match") == true,
                    isValid = confirmPassword.isNotEmpty() && password == confirmPassword,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { submit() })
                )

                Button(
                    onClick = ::submit,
                    enabled = !loading,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primary,
                        disabledContainerColor = primary.copy(alpha = 0.5f)
                    )
                ) {
                    if (loading) {
                        CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Text("Creating account…", color = Color.White.copy(alpha = 0.8f))
                    } else {
                        Text("Create $roleLabel account", fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall)
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Text("already have an account?", style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f))
                    HorizontalDivider(modifier = Modifier.weight(1f), thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                }

                TextButton(
                    onClick = { onSwitchToSignIn(role) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        buildAnnotatedString {
                            append("Sign in as  ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = primary)) {
                                append("$roleLabel →")
                            }
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
            Text(
                "Empowering masajid, strengthening our Ummah",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SignUpTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    showPassword: Boolean = false,
    onTogglePassword: (() -> Unit)? = null,
    isError: Boolean = false,
    isValid: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    val successGreen = Color(0xFF1B8C5E)

    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))

        OutlinedTextField(
            value = value, onValueChange = onValueChange,
            placeholder = {
                Text(placeholder, style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
            },
            leadingIcon = {
                Icon(leadingIcon, null,
                    tint = when {
                        isError -> MaterialTheme.colorScheme.error
                        isValid -> successGreen
                        value.isNotEmpty() -> accentColor
                        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    },
                    modifier = Modifier.size(18.dp))
            },
            trailingIcon = when {
                isPassword && onTogglePassword != null -> ({
                    IconButton(onClick = onTogglePassword) {
                        Icon(
                            if (showPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                })
                isValid -> ({
                    Icon(Icons.Outlined.CheckCircle, null,
                        tint = successGreen, modifier = Modifier.size(18.dp).padding(end = 4.dp))
                })
                else -> null
            },
            visualTransformation = if (isPassword && !showPassword) PasswordVisualTransformation()
            else VisualTransformation.None,
            singleLine = true, isError = isError,
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor   = when {
                    isValid -> successGreen.copy(alpha = 0.04f)
                    else    -> accentColor.copy(alpha = 0.04f)
                },
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                focusedBorderColor      = when {
                    isError -> MaterialTheme.colorScheme.error
                    isValid -> successGreen
                    else    -> accentColor
                },
                unfocusedBorderColor    = when {
                    isError -> MaterialTheme.colorScheme.error
                    isValid -> successGreen.copy(alpha = 0.6f)
                    else    -> MaterialTheme.colorScheme.outlineVariant
                }
            ),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions
        )
    }
}