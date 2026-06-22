package com.durranitech.salahsync.presentation.imam.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.net.URLDecoder
import java.util.concurrent.Executors

// ─── Colors ───────────────────────────────────────────────────────────────────
private val BluePrimary = Color(0xFF1E40AF)
private val ScannerGreen = Color(0xFF1B8C5E)
private val OverlayColor = Color(0xCC000000) // semi-transparent black overlay

// ─── Data class for parsed QR result ─────────────────────────────────────────

data class QrScanResult(
    val masjidCode: String,
    val masjidName: String
)

// ─── Screen ───────────────────────────────────────────────────────────────────

/**
 * NavEntry-level composable.
 *
 * @param onCodeScanned  Called once with the parsed [QrScanResult] when a valid
 *                       SalahSync QR is detected. The screen stops scanning after
 *                       the first successful read to avoid duplicate calls.
 * @param onBack         Navigate up callback.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanQrScreen(
    onCodeScanned: (QrScanResult) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // Track whether camera permission has been granted
    var hasCameraPermission by remember { mutableStateOf(false) }

    // After a scan succeeds, lock further scans so onCodeScanned fires exactly once
    var scanConsumed by remember { mutableStateOf(false) }

    // Permission launcher — requests CAMERA at runtime
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    // Ask for permission as soon as the screen appears
    LaunchedEffect(Unit) {
        val status = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        hasCameraPermission =
            (status == PackageManager.PERMISSION_GRANTED)
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Scan Masjid QR",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                },
                // Transparent top bar so the camera bleeds edge-to-edge
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        // Remove default background — camera fills the whole screen
        containerColor = Color.Black
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                hasCameraPermission -> {
                    // Full-screen camera preview
                    CameraPreview(
                        onQrDetected = { rawValue ->
                            if (!scanConsumed) {
                                val result = parseQrPayload(rawValue)
                                if (result != null) {
                                    scanConsumed = true
                                    onCodeScanned(result)
                                }
                            }
                        }
                    )

                    // Dark overlay with transparent viewport cutout
                    ScannerOverlay()
                }

                else -> {
                    // Permission denied state
                    PermissionDeniedContent(
                        onRequestAgain = {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    )
                }
            }
        }
    }
}

// ─── CameraX Preview ──────────────────────────────────────────────────────────

@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
@Composable
private fun CameraPreview(onQrDetected: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    // Single-thread executor for image analysis — release on dispose
    val analysisExecutor = remember { Executors.newSingleThreadExecutor() }
    DisposableEffect(Unit) {
        onDispose { analysisExecutor.shutdown() }
    }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)

            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener(
                {
                    val cameraProvider = cameraProviderFuture.get()

                    // Use-case 1: camera preview
                    val preview = Preview.Builder().build().also {
                        it.surfaceProvider = previewView.surfaceProvider
                    }

                    // Use-case 2: image analysis for QR detection
                    val imageAnalysis = ImageAnalysis.Builder()
                        // STRATEGY_KEEP_ONLY_LATEST drops frames we can't process in time
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also { analysis ->
                            analysis.setAnalyzer(analysisExecutor) { imageProxy ->
                                processImageProxy(imageProxy, onQrDetected)
                            }
                        }

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            imageAnalysis
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
                ContextCompat.getMainExecutor(ctx)
            )
            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}

// ─── ML Kit image processing ──────────────────────────────────────────────────

/**
 * Runs ML Kit's barcode scanner on each camera frame.
 * [imageProxy] MUST be closed after use — ML Kit closes it automatically
 * after the Task completes, but we add a finally block as a safety net.
 */
@ExperimentalGetImage
private fun processImageProxy(
    imageProxy: ImageProxy,
    onQrDetected: (String) -> Unit
) {
    val mediaImage = imageProxy.image ?: run { imageProxy.close(); return }
    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

    BarcodeScanning.getClient().process(image)
        .addOnSuccessListener { barcodes ->
            barcodes
                .firstOrNull { it.format == Barcode.FORMAT_QR_CODE }
                ?.rawValue
                ?.let(onQrDetected)
        }
        .addOnCompleteListener {
            // Always close — even if the task failed — to unblock the analysis pipeline
            imageProxy.close()
        }
}

// ─── QR Payload Parser ────────────────────────────────────────────────────────

/**
 * Parses a deep-link URI of the form:
 *   salahsync://join?code=NR-4821&name=Masjid+Al-Noor
 *
 * Returns null if the URI is not a valid SalahSync join link.
 */
private fun parseQrPayload(raw: String): QrScanResult? {
    return try {
        if (!raw.startsWith("salahsync://join")) return null
        val uri = Uri.parse(raw)
        val code = uri.getQueryParameter("code") ?: return null
        val name = uri.getQueryParameter("name")?.let {
            URLDecoder.decode(it, "UTF-8")
        } ?: ""
        if (code.isBlank()) null
        else QrScanResult(masjidCode = code, masjidName = name)
    } catch (e: Exception) {
        null
    }
}

// ─── Scanner Overlay UI ───────────────────────────────────────────────────────

/**
 * Draws the classic "dark surround + bright viewport" scanner UI.
 * The animated green scan line sweeps top→bottom inside the viewport.
 */
@Composable
private fun ScannerOverlay() {
    // Animated scan line
    val infiniteTransition = rememberInfiniteTransition(label = "scan_line")
    val scanLineOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scan_line_offset"
    )

    Box(modifier = Modifier.fillMaxSize()) {

        // Top overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(OverlayColor)
        )

        // Bottom overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .align(Alignment.BottomCenter)
                .background(OverlayColor)
        )

        // Left overlay (same height as viewport = 240dp)
        Box(
            modifier = Modifier
                .fillMaxWidth(0.1f)
                .height(240.dp)
                .align(Alignment.Center)
                .background(OverlayColor)
        )

        // Right overlay
        Box(
            modifier = Modifier
                .fillMaxWidth(0.1f)
                .height(240.dp)
                .align(Alignment.CenterEnd)
                .background(OverlayColor)
        )

        // Viewport frame (green border)
        Box(
            modifier = Modifier
                .size(240.dp)
                .align(Alignment.Center)
                .border(2.dp, ScannerGreen, RoundedCornerShape(16.dp))
        ) {
            // Animated scan line inside the viewport
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(ScannerGreen.copy(alpha = 0.8f))
                    .offset(y = (240.dp * scanLineOffset) - 2.dp)
            )
        }

        // Instruction text below the viewport
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 56.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Point at the masjid QR code",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "The code will be scanned automatically",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}

// ─── Permission Denied ────────────────────────────────────────────────────────

@Composable
private fun PermissionDeniedContent(onRequestAgain: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "📷",
                style = MaterialTheme.typography.displaySmall
            )
            Text(
                text = "Camera permission needed",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Allow camera access so SalahSync can scan the masjid QR code.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.65f),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = onRequestAgain,
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Grant permission", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}