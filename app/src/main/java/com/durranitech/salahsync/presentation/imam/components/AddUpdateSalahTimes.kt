package com.durranitech.salahsync.presentation.imam.components

import android.app.TimePickerDialog
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.durranitech.salahsync.domain.model.SalahTime
import com.durranitech.salahsync.presentation.imam.viewmodel.ImamViewModel
import com.durranitech.salahsync.ui.theme.SalahSyncTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AddUpdateSalahTimingsScreen(
    onNavigateBackToHomeScreen: () -> Unit = {},
    viewModel: ImamViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle() // lifecycle-aware state collection [web:44]

    // Local UI state as hour/minute (no millis)
    var fajrHour by remember { mutableIntStateOf(5) }
    var fajrMinute by remember { mutableIntStateOf(30) }
    var dhuhrHour by remember { mutableIntStateOf(12) }
    var dhuhrMinute by remember { mutableIntStateOf(30) }
    var asrHour by remember { mutableIntStateOf(15) }
    var asrMinute by remember { mutableIntStateOf(45) }
    var maghribHour by remember { mutableIntStateOf(18) }
    var maghribMinute by remember { mutableIntStateOf(15) }
    var ishaHour by remember { mutableIntStateOf(20) }
    var ishaMinute by remember { mutableIntStateOf(0) }
    var jummahHour by remember { mutableIntStateOf(13) }
    var jummahMinute by remember { mutableIntStateOf(30) }

    // Initialize from state if available
    LaunchedEffect(state.salahTime) {
        state.salahTime?.let { t ->
            // Expect SalahTime to be defined with hour/minute fields
            fajrHour = t.fajrHour; fajrMinute = t.fajrMinute
            dhuhrHour = t.dhuhrHour; dhuhrMinute = t.dhuhrMinute
            asrHour = t.asrHour; asrMinute = t.asrMinute
            maghribHour = t.maghribHour; maghribMinute = t.maghribMinute
            ishaHour = t.ishaHour; ishaMinute = t.ishaMinute
            jummahHour = t.jummahHour; jummahMinute = t.jummahMinute
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Update Salah Times") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBackToHomeScreen) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        containerColor = Color(0xFFF8FAF7)
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Prayer times card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(Modifier.padding(vertical = 12.dp)) {
                    if (state.isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator() }
                    } else {
                        PrayerTimeRow(
                            name = "Fajr",
                            hour = fajrHour,
                            minute = fajrMinute,
                            onTimeSelected = { h, m -> fajrHour = h; fajrMinute = m }
                        )

                        PrayerTimeRow(
                            name = "Dhuhr",
                            hour = dhuhrHour,
                            minute = dhuhrMinute,
                            onTimeSelected = { h, m -> dhuhrHour = h; dhuhrMinute = m }
                        )

                        PrayerTimeRow(
                            name = "Asr",
                            hour = asrHour,
                            minute = asrMinute,
                            onTimeSelected = { h, m -> asrHour = h; asrMinute = m }
                        )

                        PrayerTimeRow(
                            name = "Maghrib",
                            hour = maghribHour,
                            minute = maghribMinute,
                            onTimeSelected = { h, m -> maghribHour = h; maghribMinute = m }
                        )

                        PrayerTimeRow(
                            name = "Isha",
                            hour = ishaHour,
                            minute = ishaMinute,
                            onTimeSelected = { h, m -> ishaHour = h; ishaMinute = m }
                        )

                        PrayerTimeRow(
                            name = "Jummah",
                            hour = jummahHour,
                            minute = jummahMinute,
                            onTimeSelected = { h, m -> jummahHour = h; jummahMinute = m }
                        )
                    }
                }
            }

            // Optional: show next prayer info if your ViewModel exposes it
            if (!state.isLoading && state.nextPrayerName != null && state.nextPrayerTime != null) {
                Spacer(Modifier.height(16.dp))
                val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
                val nextTimeFormatted = formatter.format(java.util.Date(state.nextPrayerTime!!))
                val remain = state.timeUntilPrayer ?: 0L
                val hours = TimeUnit.MILLISECONDS.toHours(remain)
                val minutes = TimeUnit.MILLISECONDS.toMinutes(remain) % 60

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3FFF2))
                ) {
                    Column(Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Next Prayer", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        Text(text = "${state.nextPrayerName} at $nextTimeFormatted", fontSize = 16.sp)
                        Text(text = "Time until: ${hours}h ${minutes}m", fontSize = 15.sp)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Update button
            Button(
                onClick = {
                    // Build SalahTime with hour/minute fields only
                    val salahTimes = SalahTime(
                        fajrHour = fajrHour, fajrMinute = fajrMinute,
                        dhuhrHour = dhuhrHour, dhuhrMinute = dhuhrMinute,
                        asrHour = asrHour, asrMinute = asrMinute,
                        maghribHour = maghribHour, maghribMinute = maghribMinute,
                        ishaHour = ishaHour, ishaMinute = ishaMinute,
                        jummahHour = jummahHour, jummahMinute = jummahMinute
                    )
                    viewModel.updatePrayerTimes(salahTimes)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.large
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                } else {
                    Text(
                        "Update Timings",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun PrayerTimeRow(
    name: String,
    hour: Int,
    minute: Int,
    onTimeSelected: (Int, Int) -> Unit
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Prayer name
        Text(
            text = name, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface
        )

        // Time picker button
        Row(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = Color.LightGray.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable {
                    showTimePicker(
                        context = context,
                        currentHour = hour,
                        currentMinute = minute,
                        onPicked = onTimeSelected
                    )
                }
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formatTimeFromHourMinute(hour, minute),
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.width(6.dp))
            Icon(
                Icons.Outlined.AccessTime,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// Show a TimePickerDialog and return hour/minute only
private fun showTimePicker(
    context: android.content.Context,
    currentHour: Int,
    currentMinute: Int,
    onPicked: (Int, Int) -> Unit
) {
    TimePickerDialog(
        context,
        { _, hour, minute -> onPicked(hour, minute) },
        currentHour,
        currentMinute,
        false // 12-hour format
    ).show()
}

// Format hour/minute to readable time string
private fun formatTimeFromHourMinute(hour: Int, minute: Int): String {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return formatter.format(calendar.time)
}

@Preview
@Composable
fun PreviewAddUpdateSalahTimesScreen() {
    SalahSyncTheme {
        AddUpdateSalahTimingsScreen()
    }
}
