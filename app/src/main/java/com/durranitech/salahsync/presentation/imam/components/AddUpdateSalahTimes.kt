package com.durranitech.salahsync.presentation.imam.components

import android.app.TimePickerDialog
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
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
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AddUpdateSalahTimingsScreen(
    onNavigateBackToHomeScreen: () -> Unit = {}, viewModel: ImamViewModel = hiltViewModel()
) {
    LocalContext.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Simple state for each prayer time
    var fajrTime by remember { mutableLongStateOf(0L) }
    var dhuhrTime by remember { mutableLongStateOf(0L) }
    var asrTime by remember { mutableLongStateOf(0L) }
    var maghribTime by remember { mutableLongStateOf(0L) }
    var ishaTime by remember { mutableLongStateOf(0L) }
    var jummahTime by remember { mutableLongStateOf(0L) }

    // Initialize times from state or use defaults
    LaunchedEffect(state.salahTime) {
        if (state.salahTime != null) {
            fajrTime = state.salahTime?.fajr?: 0L
            dhuhrTime = state.salahTime?.dhuhr?: 0L
            asrTime = state.salahTime?.asr?: 0L
            maghribTime = state.salahTime?.maghrib?: 0L
            ishaTime = state.salahTime?.isha?: 0L
            jummahTime = state.salahTime?.jummah?: 0L
        } else {
            // Default times
            fajrTime = createTimeInMillis(5, 30)
            dhuhrTime = createTimeInMillis(12, 30)
            asrTime = createTimeInMillis(15, 45)
            maghribTime = createTimeInMillis(18, 15)
            ishaTime = createTimeInMillis(20, 0)
            jummahTime = createTimeInMillis(13, 30)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Update Salah Times") }, navigationIcon = {
                IconButton(onClick = onNavigateBackToHomeScreen) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                }
            })
        }, containerColor = Color(0xFFF8FAF7)
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
                    if (state.isLoading){
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center){
                            CircularWavyProgressIndicator()
                        }
                    } else{
                        PrayerTimeRow(
                            name = "Fajr", timeMillis = fajrTime, onTimeSelected = { fajrTime = it })

                        PrayerTimeRow(
                            name = "Dhuhr", timeMillis = dhuhrTime, onTimeSelected = { dhuhrTime = it })

                        PrayerTimeRow(
                            name = "Asr", timeMillis = asrTime, onTimeSelected = { asrTime = it })

                        PrayerTimeRow(
                            name = "Maghrib",
                            timeMillis = maghribTime,
                            onTimeSelected = { maghribTime = it })

                        PrayerTimeRow(
                            name = "Isha", timeMillis = ishaTime, onTimeSelected = { ishaTime = it })

                        PrayerTimeRow(
                            name = "Jummah",
                            timeMillis = jummahTime,
                            onTimeSelected = { jummahTime = it })
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Update button
            Button(
                onClick = {
                    val salahTimes = SalahTime(
                        fajr = fajrTime,
                        dhuhr = dhuhrTime,
                        asr = asrTime,
                        maghrib = maghribTime,
                        isha = ishaTime,
                        jummah = jummahTime
                    )
                    viewModel.updatePrayerTimes(salahTimes)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.large
            ) {
                if (state.isLoading) {
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
        }
    }
}

@Composable
private fun PrayerTimeRow(
    name: String, timeMillis: Long, onTimeSelected: (Long) -> Unit
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
                        currentTimeMillis = timeMillis,
                        onTimeSelected = onTimeSelected
                    )
                }
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = formatTimeFromMillis(timeMillis),
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

// Helper function to show time picker
private fun showTimePicker(
    context: android.content.Context, currentTimeMillis: Long, onTimeSelected: (Long) -> Unit
) {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = currentTimeMillis
    }

    TimePickerDialog(
        context,
        { _, hour, minute ->
            val newTimeMillis = createTimeInMillis(hour, minute)
            onTimeSelected(newTimeMillis)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        false // 12-hour format
    ).show()
}

// Convert hour and minute to milliseconds
private fun createTimeInMillis(hour: Int, minute: Int): Long {
    return Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

// Format milliseconds to readable time string
private fun formatTimeFromMillis(timeInMillis: Long): String {
    val calendar = Calendar.getInstance().apply {
        this.timeInMillis = timeInMillis
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