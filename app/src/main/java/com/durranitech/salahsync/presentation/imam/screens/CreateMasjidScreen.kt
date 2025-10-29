package com.durranitech.salahsync.presentation.imam.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.durranitech.salahsync.domain.model.Masjid
import com.durranitech.salahsync.presentation.authentication.viewModel.AuthViewModel
import com.durranitech.salahsync.presentation.imam.viewmodel.ImamViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CreateMasjidScreen(
    onCreateMasjidClick: (Masjid) -> Unit = {},
    onBackClick: () -> Unit = {},
    authViewModel: AuthViewModel = hiltViewModel(),
    imamViewModel: ImamViewModel = hiltViewModel()

) {
    val masjidName = rememberSaveable { mutableStateOf("") }
    val address = rememberSaveable { mutableStateOf("") }
    val city = rememberSaveable { mutableStateOf("") }
    val authState = authViewModel.state.collectAsStateWithLifecycle()
    val imamName = authState.value.user?.name
    val imamId = authState.value.user?.id
    val imamState by imamViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(authState,Unit,imamState) {
        if (imamState.errorMessage != null){
            Toast.makeText(context, imamState.errorMessage, Toast.LENGTH_SHORT).show()
        }
        authViewModel.fetchUser()
    }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Your Masjid", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            )
        }, containerColor = MaterialTheme.colorScheme.background
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Enter your Masjid details to get started.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.primary
                ),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(20.dp))

            // Photo Upload Section
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AddAPhoto,
                    contentDescription = "Upload Masjid Logo",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )

                FloatingActionButton(
                    onClick = { /* handle image picker */ },
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(36.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White)
                }
            }

            Spacer(Modifier.height(8.dp))
            Text("Upload Masjid photo or logo", style = MaterialTheme.typography.bodySmall)

            Spacer(Modifier.height(24.dp))

            // Input Fields (Material 3 Expressive - Filled style)
            ExpressiveTextField(
                value = masjidName.value,
                onValueChange = { masjidName.value = it },
                label = "Masjid Name",
                leadingIcon = Icons.Default.Home,
            )

            ExpressiveTextField(
                value = address.value,
                onValueChange = { address.value = it },
                label = "Address",
                leadingIcon = Icons.Default.Place
            )

            ExpressiveTextField(
                value = city.value,
                onValueChange = { city.value = it },
                label = "City",
                leadingIcon = Icons.Default.LocationCity
            )

            Spacer(Modifier.height(32.dp))

            // Create Button
            Button(
                onClick = {
                    if (masjidName.value.isEmpty() || address.value.isEmpty() || city.value.isEmpty()) {
                        Toast.makeText(
                            context, "Please fill all fields", Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }
                    onCreateMasjidClick(
                        Masjid(
                            name = masjidName.value,
                            address = address.value,
                            city = city.value,
                            imamName = imamName ?: "Imam",
                            code = UUID.randomUUID().toString(),
                            lastUpdated = System.currentTimeMillis().toString(),
                            imamId = imamId ?: ""
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (imamState.isLoading) {
                    CircularWavyProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(32.dp)
                    )
                } else {
                    Text(
                        "Update Timings",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun ExpressiveTextField(
    value: String, onValueChange: (String) -> Unit, label: String, leadingIcon: ImageVector
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        leadingIcon = { Icon(leadingIcon, contentDescription = null) },
        label = { Text(label) },
        shape = RoundedCornerShape(24.dp),
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        ),
        textStyle = MaterialTheme.typography.bodyMedium,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
        )
    )
}


@Preview
@Composable
private fun CreateMasjidScreenPrev() {
    CreateMasjidScreen()
}