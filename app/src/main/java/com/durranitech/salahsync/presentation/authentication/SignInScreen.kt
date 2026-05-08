package com.durranitech.salahsync.presentation.authentication.screens.com.durranitech.salahsync.presentation.authentication

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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Groups
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
import androidx.compose.ui.res.painterResource
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.durranitech.salahsync.R
import com.durranitech.salahsync.domain.model.UserRole
import com.durranitech.salahsync.presentation.authentication.AuthIntent
import com.durranitech.salahsync.presentation.authentication.viewModel.AuthViewModel

// ─── Design tokens ─────────────────────────────────────────────────────────────
private val GreenDeep    = Color(0xFF065F46)
private val GreenPrimary = Color(0xFF1B8C5E)
private val GreenLight   = Color(0xFF26A874)
private val GreenSurface = Color(0xFFE8F5EF)
private val GreenGlow    = Color(0xFF34D399)
private val IndigoPrimary = Color(0xFF3730A3)
private val IndigoLight   = Color(0xFF6366F1)
private val IndigoSurface = Color(0xFFEEF2FF)

@Composable
fun SignInScreen(
    role: UserRole,
    onBack: () -> Unit,
    onSwitchToSignUp: (UserRole) -> Unit,
    paddingValues: PaddingValues,
    onSwitchToImamDashboard: () -> Unit,
    onSwitchToMuqtadiDashboard: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state    by viewModel.state.collectAsStateWithLifecycle()
    val isLoading = state.isLoading

    var email         by remember { mutableStateOf("") }
    var password      by remember { mutableStateOf("") }
    var showPassword  by remember { mutableStateOf(false) }
    var localError    by remember { mutableStateOf<String?>(null) }

    val focusManager    = LocalFocusManager.current
    val passwordFocus   = remember { FocusRequester() }

    // Role-specific branding
    val primary    = if (role == UserRole.IMAM) GreenPrimary  else IndigoPrimary
    val light      = if (role == UserRole.IMAM) GreenLight    else IndigoLight
    val surface    = if (role == UserRole.IMAM) GreenSurface  else IndigoSurface
    val glow       = if (role == UserRole.IMAM) GreenGlow     else IndigoLight
    val roleLabel  = if (role == UserRole.IMAM) "Imam"        else "Muqtadi"
    val roleIcon   = if (role == UserRole.IMAM) Icons.Outlined.Person else Icons.Outlined.Groups
    val demoEmail  = if (role == UserRole.IMAM) "imam@mosque.demo" else "muqtadi@mosque.demo"

    // Staggered entrance
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val headerAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(500),
        label = "header"
    )
    val formAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(500, delayMillis = 150),
        label = "form"
    )
    val formOffset by animateFloatAsState(
        targetValue = if (visible) 0f else 24f,
        animationSpec = tween(500, delayMillis = 150, easing = EaseOutCubic),
        label = "form_offset"
    )

    // Glow pulse on icon
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 0.5f,
        animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
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

    fun submit() {
        focusManager.clearFocus()
        if (email.isBlank() || password.isBlank()) {
            localError = "Please fill in all fields"; return
        }
        localError = null
        viewModel.onAuthEvent(AuthIntent.SignIn(email.trim(), password.trim()))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Background glow blobs
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.TopEnd)
                .blur(80.dp)
                .background(glow.copy(alpha = glowAlpha * 0.3f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.BottomStart)
                .blur(60.dp)
                .background(primary.copy(alpha = 0.15f), CircleShape)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            // ── Header ────────────────────────────────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.
                alpha(headerAlpha)
                    .fillMaxWidth()
            ) {
                // Back button
                TextButton(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Start),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
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

                // Icon with glow rings
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(primary.copy(alpha = glowAlpha * 0.18f), CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .background(primary.copy(alpha = 0.12f), CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(primary, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(roleIcon, null, tint = Color.White, modifier = Modifier.size(26.dp))
                    }
                }

                Spacer(Modifier.height(16.dp))

                // App wordmark (small)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Image(painterResource(R.drawable.main_icon), null, modifier = Modifier.size(20.dp))
                    Text("SalahSync", style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold, color = GreenDeep)
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Welcome back, $roleLabel",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Sign in to continue",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            Spacer(Modifier.height(28.dp))

            // ── Form card ─────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .alpha(formAlpha)
                    .padding(top = formOffset.dp)
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Error banner
                val errorMsg = localError ?: state.error
                AnimatedVisibility(
                    visible = errorMsg != null,
                    enter   = fadeIn(tween(200)) + slideInVertically(tween(200)) { -it }
                ) {
                    errorMsg?.let {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.errorContainer,
                                    RoundedCornerShape(10.dp)
                                )
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier.size(6.dp)
                                    .background(MaterialTheme.colorScheme.error, CircleShape)
                            )
                            Text(it, style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error)
                        }
                    }
                }

                // Email
                SignInTextField(
                    value = email, onValueChange = { email = it; localError = null },
                    label = "Email address", placeholder = "you@gmail.com",
                    leadingIcon = Icons.Outlined.Email, accentColor = primary,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { passwordFocus.requestFocus() })
                )

                // Password
                SignInTextField(
                    value = password, onValueChange = { password = it; localError = null },
                    label = "Password", placeholder = "Your password",
                    leadingIcon = Icons.Outlined.Lock, accentColor = primary,
                    modifier = Modifier.focusRequester(passwordFocus),
                    isPassword = true, showPassword = showPassword,
                    onTogglePassword = { showPassword = !showPassword },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { submit() })
                )

                // Sign In button
                Button(
                    onClick = ::submit,
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primary,
                        disabledContainerColor = primary.copy(alpha = 0.5f)
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Text("Signing in…", color = Color.White.copy(alpha = 0.8f))
                    } else {
                        Text("Sign in as $roleLabel", fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall)
                    }
                }

                // Divider
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Text("or", style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f))
                    HorizontalDivider(modifier = Modifier.weight(1f), thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                }

                // Demo tile
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(surface, RoundedCornerShape(14.dp))
                        .border(1.dp, primary.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text("Try demo account", style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold, color = primary)
                            Text(demoEmail, style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                            Text("Password: demo123", style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                        }
                        Button(
                            onClick = { email = demoEmail; password = "demo123" },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = primary),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text("Use demo", style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Sign Up link
            TextButton(onClick = { onSwitchToSignUp(role) }) {
                Text(
                    buildAnnotatedString {
                        append("Don't have an account?  ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = primary)) {
                            append("Sign up →")
                        }
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                )
            }

            Spacer(Modifier.height(8.dp))
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

// ─── Reusable text field ──────────────────────────────────────────────────────

@Composable
private fun SignInTextField(
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
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
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
                    tint = if (value.isNotEmpty()) accentColor
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    modifier = Modifier.size(18.dp))
            },
            trailingIcon = if (isPassword && onTogglePassword != null) ({
                IconButton(onClick = onTogglePassword) {
                    Icon(
                        if (showPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }) else null,
            visualTransformation = if (isPassword && !showPassword) PasswordVisualTransformation()
            else VisualTransformation.None,
            singleLine = true,
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor   = accentColor.copy(alpha = 0.04f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                focusedBorderColor      = accentColor,
                unfocusedBorderColor    = MaterialTheme.colorScheme.outlineVariant
            ),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions
        )
    }
}
