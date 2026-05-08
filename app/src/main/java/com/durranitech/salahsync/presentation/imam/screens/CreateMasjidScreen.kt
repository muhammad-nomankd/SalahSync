package com.durranitech.salahsync.presentation.imam.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Mosque
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.durranitech.salahsync.domain.model.Masjid
import com.durranitech.salahsync.presentation.authentication.viewModel.AuthViewModel
import com.durranitech.salahsync.presentation.imam.viewmodel.ImamViewModel
import com.durranitech.salahsync.ui.theme.SalahSyncTheme
import java.util.UUID

// ─── Colors ───────────────────────────────────────────────────────────────────
private val GreenPrimary  = Color(0xFF1B8C5E)
private val GreenLight    = Color(0xFF26A874)
private val GreenSurface  = Color(0xFFE8F5EF)

// ─── Screen ───────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMasjidScreen(
    onCreateMasjidClick: (Masjid) -> Unit = {},
    onBackClick: () -> Unit = {},
    authViewModel: AuthViewModel = hiltViewModel(),
    imamViewModel: ImamViewModel = hiltViewModel()
) {
    val authState  by authViewModel.state.collectAsStateWithLifecycle()
    val imamState  by imamViewModel.uiState.collectAsStateWithLifecycle()
    val context    = LocalContext.current

    // Form state — rememberSaveable survives rotation
    var masjidName by rememberSaveable { mutableStateOf("") }
    var address    by rememberSaveable { mutableStateOf("") }
    var city       by rememberSaveable { mutableStateOf("") }

    // Validation errors
    var nameError    by remember { mutableStateOf(false) }
    var addressError by remember { mutableStateOf(false) }
    var cityError    by remember { mutableStateOf(false) }

    // Animate content in on first composition
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    LaunchedEffect(imamState.errorMessage) {
        imamState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        authViewModel.fetchUserAndDecideDestination()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Create Masjid",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Fill in your masjid details",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = "Back",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 16 }
        ) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ── Hero avatar ──────────────────────────────────────────────
                MasjidAvatarPicker()

                Spacer(Modifier.height(28.dp))

                // ── Section label ────────────────────────────────────────────
                SectionLabel(text = "Masjid Information")
                Spacer(Modifier.height(12.dp))

                // ── Fields ───────────────────────────────────────────────────
                MasjidTextField(
                    value         = masjidName,
                    onValueChange = {
                        masjidName = it
                        if (nameError) nameError = false
                    },
                    label         = "Masjid Name",
                    placeholder   = "e.g. Masjid Al-Noor",
                    leadingIcon   = Icons.Outlined.Mosque,
                    isError       = nameError,
                    errorMessage  = "Masjid name is required",
                    imeAction     = ImeAction.Next
                )

                MasjidTextField(
                    value         = address,
                    onValueChange = {
                        address = it
                        if (addressError) addressError = false
                    },
                    label         = "Street Address",
                    placeholder   = "e.g. 123 Main Street",
                    leadingIcon   = Icons.Outlined.LocationOn,
                    isError       = addressError,
                    errorMessage  = "Address is required",
                    imeAction     = ImeAction.Next
                )

                MasjidTextField(
                    value         = city,
                    onValueChange = {
                        city = it
                        if (cityError) cityError = false
                    },
                    label         = "City",
                    placeholder   = "e.g. Peshawar",
                    leadingIcon   = Icons.Outlined.Apartment,
                    isError       = cityError,
                    errorMessage  = "City is required",
                    imeAction     = ImeAction.Done
                )

                Spacer(Modifier.height(8.dp))

                // ── Imam info chip ───────────────────────────────────────────
                authState.user?.name?.let { name ->
                    ImamInfoChip(imamName = name)
                    Spacer(Modifier.height(8.dp))
                }

                Spacer(Modifier.height(24.dp))

                // ── Create button ────────────────────────────────────────────
                Button(
                    onClick = {
                        // Inline validation — show all errors at once
                        nameError    = masjidName.isBlank()
                        addressError = address.isBlank()
                        cityError    = city.isBlank()
                        if (nameError || addressError || cityError) return@Button

                        onCreateMasjidClick(
                            Masjid(
                                name        = masjidName.trim(),
                                address     = address.trim(),
                                city        = city.trim(),
                                imamName    = authState.user?.name ?: "Imam",
                                code        = UUID.randomUUID().toString().take(8).uppercase(),
                                lastUpdated = System.currentTimeMillis().toString(),
                                imamId      = authState.user?.id ?: ""
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    enabled = !imamState.isLoading
                ) {
                    if (imamState.isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = "Creating…",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    } else {
                        Text(
                            text = "Create Masjid",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

// ─── Avatar Picker ────────────────────────────────────────────────────────────

@Composable
private fun MasjidAvatarPicker() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            // Avatar circle with gradient ring
            Box(
                modifier = Modifier
                    .size(104.dp)
                    .clip(CircleShape)
                    .border(
                        width = 2.dp,
                        brush = Brush.linearGradient(listOf(GreenPrimary, GreenLight)),
                        shape = CircleShape
                    )
                    .background(GreenSurface),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🕌", fontSize = 40.sp)
            }

            // Camera badge
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(GreenPrimary, CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.background, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.AddAPhoto,
                    contentDescription = "Upload photo",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Text(
            text = "Upload masjid photo or logo",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}

// ─── Imam Info Chip ───────────────────────────────────────────────────────────

@Composable
private fun ImamInfoChip(imamName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(GreenSurface, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(GreenPrimary, CircleShape)
        )
        Text(
            text = "Registering as Imam: ",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = imamName,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = GreenPrimary
        )
    }
}

// ─── Section Label ────────────────────────────────────────────────────────────

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        modifier = Modifier.fillMaxWidth()
    )
}

// ─── Text Field ───────────────────────────────────────────────────────────────

@Composable
fun MasjidTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    leadingIcon: ImageVector,
    isError: Boolean = false,
    errorMessage: String = "",
    imeAction: ImeAction = ImeAction.Next
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            placeholder = {
                Text(
                    placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
                )
            },
            leadingIcon = {
                Icon(
                    leadingIcon,
                    contentDescription = null,
                    tint = if (isError) MaterialTheme.colorScheme.error
                    else if (value.isNotEmpty()) GreenPrimary
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    modifier = Modifier.size(20.dp)
                )
            },
            isError = isError,
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor   = GreenSurface.copy(alpha = 0.5f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                errorContainerColor     = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
                focusedBorderColor      = GreenPrimary,
                unfocusedBorderColor    = MaterialTheme.colorScheme.outlineVariant,
                errorBorderColor        = MaterialTheme.colorScheme.error
            ),
            textStyle = MaterialTheme.typography.bodyMedium,
            keyboardOptions = KeyboardOptions(
                keyboardType    = KeyboardType.Text,
                imeAction       = imeAction,
                capitalization  = KeyboardCapitalization.Words
            )
        )

        // Inline error message — slides in when isError = true
        AnimatedVisibility(
            visible = isError,
            enter   = fadeIn(tween(200)) + slideInVertically(tween(200)) { -it / 2 }
        ) {
            Text(
                text     = errorMessage,
                style    = MaterialTheme.typography.labelSmall,
                color    = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 16.dp, top = 2.dp)
            )
        }
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun CreateMasjidScreenPreview() {
    SalahSyncTheme {
        // Preview without ViewModels — just the layout
        Scaffold(
            topBar = {},
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MasjidAvatarPicker()
                Spacer(Modifier.height(24.dp))
                SectionLabel("Masjid Information")
                Spacer(Modifier.height(12.dp))
                MasjidTextField(
                    value = "Masjid Al-Noor", onValueChange = {},
                    label = "Masjid Name", leadingIcon = Icons.Outlined.Mosque
                )
                MasjidTextField(
                    value = "", onValueChange = {},
                    label = "Street Address",
                    leadingIcon = Icons.Outlined.LocationOn,
                    isError = true, errorMessage = "Address is required"
                )
                MasjidTextField(
                    value = "Peshawar", onValueChange = {},
                    label = "City", leadingIcon = Icons.Outlined.Apartment
                )
                Spacer(Modifier.height(8.dp))
                ImamInfoChip(imamName = "Sheikh Abdullah")
            }
        }
    }
}