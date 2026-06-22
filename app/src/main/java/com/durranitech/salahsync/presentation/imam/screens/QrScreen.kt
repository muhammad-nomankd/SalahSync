package com.durranitech.salahsync.presentation.imam.screens

import android.content.Intent
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.durranitech.salahsync.presentation.event.QrUiState
import com.durranitech.salahsync.presentation.imam.viewmodel.QrViewModel
import com.durranitech.salahsync.presentation.intent.QrIntent
import com.durranitech.salahsync.presentation.state.QrEvent
import com.durranitech.salahsync.ui.theme.SalahSyncTheme

private val GreenPrimary = Color(0xFF1B8C5E)
private val GreenSurface = Color(0xFFE8F5EF)


@Composable
fun QrScreen(
    masjidCode: String,
    masjidName: String,
    onBack: () -> Unit,
    viewModel: QrViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(masjidCode) {
        viewModel.onIntent(QrIntent.GenerateQr(masjidCode, masjidName))
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is QrEvent.ShareImage -> {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "image/png"
                        putExtra(Intent.EXTRA_STREAM, event.uri)
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "Join ${state.masjidName} on SalahSync!\nUse code: ${state.masjidCode}"
                        )
                        clipData = android.content.ClipData.newRawUri("QR Code", event.uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }

                    val chooser = Intent.createChooser(shareIntent, "Share Masjid QR").apply {
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }

                    context.startActivity(chooser)
                }

                is QrEvent.ShowError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    QrScreenContent(
        state = state,
        onBack = onBack,
        onShare = { viewModel.onIntent(QrIntent.ShareQr) },
        onRetry = { viewModel.onIntent(QrIntent.GenerateQr(masjidCode, masjidName)) })
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrScreenContent(
    state: QrUiState, onBack: () -> Unit, onShare: () -> Unit, onRetry: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                Text(
                    text = "Masjid QR Code",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }, navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Go back"
                    )
                }
            }, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
            )
        }, containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            InstructionText()
            QrCardAnimated(
                isLoading = state.isLoading,
                bitmap = state.qrBitmap,
                error = state.error,
                onRetry = onRetry
            )
            if (state.masjidCode.isNotBlank()) {
                MasjidCodeChip(code = state.masjidCode, name = state.masjidName)
            }
            if (state.qrBitmap != null) {
                ShareButton(onClick = onShare)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ─── Sub-Composables ──────────────────────────────────────────────────────────

@Composable
private fun InstructionText() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Share this QR with your congregation",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Muqtadis can scan it to join your masjid instantly",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun QrCardAnimated(
    isLoading: Boolean, bitmap: Bitmap?, error: String?, onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(280.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(GreenSurface),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = when {
                isLoading -> "loading"
                error != null -> "error"
                bitmap != null -> "qr"
                else -> "loading"
            }, transitionSpec = {
                (fadeIn(tween(400)) + scaleIn(
                    tween(400),
                    initialScale = 0.92f
                )) togetherWith fadeOut(tween(200))
            }, label = "qr_state"
        ) { screenState ->
            when (screenState) {
                "loading" -> Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator(
                        color = GreenPrimary, strokeWidth = 2.dp, modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "Generating…",
                        style = MaterialTheme.typography.labelMedium,
                        color = GreenPrimary
                    )
                }

                "qr" -> Image(
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = "Masjid QR code",
                    modifier = Modifier
                        .size(240.dp)
                        .padding(16.dp)
                )

                "error" -> Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.QrCode2,
                        contentDescription = null,
                        tint = GreenPrimary.copy(alpha = 0.4f),
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = error ?: "Failed to generate QR",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                    OutlinedButton(onClick = onRetry) { Text("Retry") }
                }
            }
        }
    }
}

@Composable
private fun MasjidCodeChip(code: String, name: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(GreenSurface)
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Text(
                text = code,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = GreenPrimary,
                letterSpacing = 4.sp
            )
        }
        Text(
            text = "Masjid join code",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
    }
}

@Composable
private fun ShareButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
    ) {
        Icon(
            imageVector = Icons.Outlined.Share,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Share QR Code",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ─── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun QrScreenPreview() {
    SalahSyncTheme {
        QrScreenContent(
            state = QrUiState(
            masjidCode = "ABCDEF",
            masjidName = "Grand Mosque",
            isLoading = false
        ), onBack = {}, onShare = {}, onRetry = {})
    }
}

@Preview(showBackground = true, name = "Loading State")
@Composable
private fun QrScreenLoadingPreview() {
    SalahSyncTheme {
        QrScreenContent(
            state = QrUiState(isLoading = true),
            onBack = {},
            onShare = {},
            onRetry = {})
    }
}

@Preview(showBackground = true, name = "Error State")
@Composable
private fun QrScreenErrorPreview() {
    SalahSyncTheme {
        QrScreenContent(
            state = QrUiState(error = "Unable to connect to server", isLoading = false),
            onBack = {},
            onShare = {},
            onRetry = {})
    }
}