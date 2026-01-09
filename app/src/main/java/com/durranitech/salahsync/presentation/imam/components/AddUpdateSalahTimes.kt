package com.durranitech.salahsync.presentation.imam.components

import android.app.TimePickerDialog
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.durranitech.salahsync.domain.model.SalahTime
import com.durranitech.salahsync.presentation.imam.viewmodel.ImamViewModel
import com.durranitech.salahsync.ui.theme.SalahSyncTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUpdateSalahTimingsScreen(
    onNavigateBackToHomeScreen: () -> Unit = {},
    viewModel: ImamViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var fajrHour by rememberSaveable { mutableIntStateOf(0) }
    var fajrMinute by rememberSaveable { mutableIntStateOf(0) }
    var dhuhrHour by rememberSaveable { mutableIntStateOf(0) }
    var dhuhrMinute by rememberSaveable { mutableIntStateOf(0) }
    var asrHour by rememberSaveable { mutableIntStateOf(0) }
    var asrMinute by rememberSaveable { mutableIntStateOf(0) }
    var maghribHour by rememberSaveable { mutableIntStateOf(0) }
    var maghribMinute by rememberSaveable { mutableIntStateOf(0) }
    var ishaHour by rememberSaveable { mutableIntStateOf(0) }
    var ishaMinute by rememberSaveable { mutableIntStateOf(0) }
    var jummahHour by rememberSaveable { mutableIntStateOf(0) }
    var jummahMinute by rememberSaveable { mutableIntStateOf(0) }

    var hasInitialized by rememberSaveable { mutableIntStateOf(0) }

    if (hasInitialized == 0 && state.salahTime != null && !state.isLoading) {
        val t = state.salahTime ?: SalahTime()
        fajrHour = t.fajrHour
        fajrMinute = t.fajrMinute
        dhuhrHour = t.dhuhrHour
        dhuhrMinute = t.dhuhrMinute
        asrHour = t.asrHour
        asrMinute = t.asrMinute
        maghribHour = t.maghribHour
        maghribMinute = t.maghribMinute
        ishaHour = t.ishaHour
        ishaMinute = t.ishaMinute
        jummahHour = t.jummahHour
        jummahMinute = t.jummahMinute
        hasInitialized = 1
    }

    Scaffold(
        topBar = {
            SalahTimesTopAppBar(
                onNavigateBack = {onNavigateBackToHomeScreen()}
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SalahTimesCard(
                isLoading = state.isLoading,
                prayers = listOf(
                    Pair("Fajr", PrayerTimeState(fajrHour, fajrMinute) { h, m ->
                        fajrHour = h
                        fajrMinute = m
                    }),
                    Pair("Dhuhr", PrayerTimeState(dhuhrHour, dhuhrMinute) { h, m ->
                        dhuhrHour = h
                        dhuhrMinute = m
                    }),
                    Pair("Asr", PrayerTimeState(asrHour, asrMinute) { h, m ->
                        asrHour = h
                        asrMinute = m
                    }),
                    Pair("Maghrib", PrayerTimeState(maghribHour, maghribMinute) { h, m ->
                        maghribHour = h
                        maghribMinute = m
                    }),
                    Pair("Isha", PrayerTimeState(ishaHour, ishaMinute) { h, m ->
                        ishaHour = h
                        ishaMinute = m
                    }),
                    Pair("Jummah", PrayerTimeState(jummahHour, jummahMinute) { h, m ->
                        jummahHour = h
                        jummahMinute = m
                    })
                )
            )

            state.takeIf { !it.isLoading && it.nextPrayerName != null && it.nextPrayerTime != null }
                ?.let { nonNullState ->
                    Spacer(modifier = Modifier.height(16.dp))
                    NextPrayerCard(
                        prayerName = nonNullState.nextPrayerName!!,
                        prayerTime = nonNullState.nextPrayerTime!!,
                        timeUntilPrayer = nonNullState.timeUntilPrayer ?: 0L
                    )
                }

            Spacer(modifier = Modifier.height(32.dp))

            UpdateButton(
                isLoading = state.isLoading,
                onClick = {
                    val salahTimes = SalahTime(
                        fajrHour = fajrHour,
                        fajrMinute = fajrMinute,
                        dhuhrHour = dhuhrHour,
                        dhuhrMinute = dhuhrMinute,
                        asrHour = asrHour,
                        asrMinute = asrMinute,
                        maghribHour = maghribHour,
                        maghribMinute = maghribMinute,
                        ishaHour = ishaHour,
                        ishaMinute = ishaMinute,
                        jummahHour = jummahHour,
                        jummahMinute = jummahMinute
                    )
                    viewModel.updatePrayerTimes(salahTimes)
                }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
 fun SalahTimesTopAppBar(onNavigateBack: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Update Salah Times",
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Navigate back"
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
private fun SalahTimesCard(
    isLoading: Boolean,
    prayers: List<Pair<String, PrayerTimeState>>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = MaterialThemeElevation.level1)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                    prayers.forEach { (name, state) ->
                        PrayerTimeRow(
                            name = name,
                            hour = state.hour,
                            minute = state.minute,
                            onTimeSelected = state.onTimeSelected
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NextPrayerCard(
    prayerName: String,
    prayerTime: Long,
    timeUntilPrayer: Long
) {
    val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val formattedTime = formatter.format(java.util.Date(prayerTime))
    val hours = TimeUnit.MILLISECONDS.toHours(timeUntilPrayer)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(timeUntilPrayer) % 60

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = MaterialThemeElevation.level1)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Next Prayer",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$prayerName at $formattedTime",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Time until: ${hours}h ${minutes}m",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun UpdateButton(
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = !isLoading,
        shape = MaterialTheme.shapes.large
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Text(
                text = "Update Timings",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
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
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        OutlinedButton(
            onClick = {
                showTimePicker(
                    context = context,
                    currentHour = hour,
                    currentMinute = minute,
                    onPicked = onTimeSelected
                )
            },
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
            shape = MaterialTheme.shapes.large
        ) {
            Text(
                text = formatTimeFromHourMinute(hour, minute),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Outlined.AccessTime,
                contentDescription = "Select time",
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

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
        false
    ).show()
}

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


private data class PrayerTimeState(
    val hour: Int,
    val minute: Int,
    val onTimeSelected: (Int, Int) -> Unit
)

private object MaterialThemeElevation {
    val level0 = 0.dp
    val level1 = 1.dp
    val level2 = 3.dp
    val level3 = 6.dp
    val level4 = 8.dp
    val level5 = 12.dp
}

@Preview(showBackground = true)
@Composable
private fun PreviewAddUpdateSalahTimesScreen() {
    SalahSyncTheme {
        AddUpdateSalahTimingsScreen()
    }
}