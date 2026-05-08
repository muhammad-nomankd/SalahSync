package com.durranitech.salahsync.presentation.imam.components

import android.app.TimePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.durranitech.salahsync.domain.model.SalahTime
import com.durranitech.salahsync.presentation.imam.viewmodel.ImamViewModel
import com.durranitech.salahsync.ui.theme.SalahSyncTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

// ─── Colors ───────────────────────────────────────────────────────────────────
private val GreenPrimary = Color(0xFF1B8C5E)
private val GreenLight   = Color(0xFF26A874)
private val GreenSurface = Color(0xFFE8F5EF)

// ─── Prayer metadata ──────────────────────────────────────────────────────────
private data class PrayerMeta(
    val name: String,
    val emoji: String,
    val accentColor: Color
)

private val prayerMeta = listOf(
    PrayerMeta("Fajr",    "🌄", Color(0xFF5C6BC0)),
    PrayerMeta("Dhuhr",   "☀️",  GreenPrimary),
    PrayerMeta("Asr",     "🌤️",  Color(0xFFFFA726)),
    PrayerMeta("Maghrib", "🌅", Color(0xFFE57373)),
    PrayerMeta("Isha",    "🌙", Color(0xFF7E57C2)),
    PrayerMeta("Jummah",  "🕌", GreenLight)
)

// ─── Screen ───────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUpdateSalahTimingsScreen(
    onNavigateBackToHomeScreen: () -> Unit = {},
    viewModel: ImamViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var fajrHour    by rememberSaveable { mutableIntStateOf(0) }
    var fajrMinute  by rememberSaveable { mutableIntStateOf(0) }
    var dhuhrHour   by rememberSaveable { mutableIntStateOf(0) }
    var dhuhrMinute by rememberSaveable { mutableIntStateOf(0) }
    var asrHour     by rememberSaveable { mutableIntStateOf(0) }
    var asrMinute   by rememberSaveable { mutableIntStateOf(0) }
    var maghribHour   by rememberSaveable { mutableIntStateOf(0) }
    var maghribMinute by rememberSaveable { mutableIntStateOf(0) }
    var ishaHour    by rememberSaveable { mutableIntStateOf(0) }
    var ishaMinute  by rememberSaveable { mutableIntStateOf(0) }
    var jummahHour    by rememberSaveable { mutableIntStateOf(0) }
    var jummahMinute  by rememberSaveable { mutableIntStateOf(0) }

    // ✅ One-time initialisation from saved Firestore data
    var hasInitialized by rememberSaveable { mutableStateOf(false) }
    if (!hasInitialized && state.salahTime != null && !state.isLoading) {
        val t = state.salahTime!!
        fajrHour = t.fajrHour ?: 0;     fajrMinute  = t.fajrMinute ?: 0
        dhuhrHour = t.dhuhrHour ?: 0;   dhuhrMinute = t.dhuhrMinute ?: 0
        asrHour = t.asrHour ?: 0;       asrMinute   = t.asrMinute ?: 0
        maghribHour = t.maghribHour ?: 0; maghribMinute = t.maghribMinute ?: 0
        ishaHour = t.ishaHour ?: 0;     ishaMinute  = t.ishaMinute ?: 0
        jummahHour = t.jummahHour ?: 0; jummahMinute = t.jummahMinute ?: 0
        hasInitialized = true
    }

    // Entrance animation
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val prayers = listOf(
        Triple(fajrHour,    fajrMinute,    { h: Int, m: Int -> fajrHour = h;    fajrMinute = m }),
        Triple(dhuhrHour,   dhuhrMinute,   { h: Int, m: Int -> dhuhrHour = h;   dhuhrMinute = m }),
        Triple(asrHour,     asrMinute,     { h: Int, m: Int -> asrHour = h;     asrMinute = m }),
        Triple(maghribHour, maghribMinute, { h: Int, m: Int -> maghribHour = h; maghribMinute = m }),
        Triple(ishaHour,    ishaMinute,    { h: Int, m: Int -> ishaHour = h;    ishaMinute = m }),
        Triple(jummahHour,  jummahMinute,  { h: Int, m: Int -> jummahHour = h;  jummahMinute = m })
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Prayer Times",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Set daily salah schedule",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBackToHomeScreen) {
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
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ── Next prayer info banner ──────────────────────────────────
                state.takeIf { !it.isLoading && it.nextPrayerName != null && it.nextPrayerTime != null }
                    ?.let {
                        NextPrayerBanner(
                            prayerName      = it.nextPrayerName!!,
                            prayerTime      = it.nextPrayerTime!!,
                            timeUntilPrayer = it.timeUntilPrayer ?: 0L
                        )
                    }

                // ── Prayer times card ────────────────────────────────────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    if (state.isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = GreenPrimary, strokeWidth = 2.dp)
                        }
                    } else {
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            prayers.forEachIndexed { index, (hour, minute, onPicked) ->
                                val meta = prayerMeta[index]
                                PrayerTimeRow(
                                    meta     = meta,
                                    hour     = hour,
                                    minute   = minute,
                                    onPicked = onPicked
                                )
                                if (index < prayers.size - 1) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        thickness = 0.5.dp,
                                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                    )
                                }
                            }
                        }
                    }
                }

                // ── Save button ──────────────────────────────────────────────
                Button(
                    onClick = {
                        viewModel.updatePrayerTimes(
                            SalahTime(
                                fajrHour = fajrHour, fajrMinute = fajrMinute,
                                dhuhrHour = dhuhrHour, dhuhrMinute = dhuhrMinute,
                                asrHour = asrHour, asrMinute = asrMinute,
                                maghribHour = maghribHour, maghribMinute = maghribMinute,
                                ishaHour = ishaHour, ishaMinute = ishaMinute,
                                jummahHour = jummahHour, jummahMinute = jummahMinute
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    enabled = !state.isLoading
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text("Saving…", color = Color.White.copy(alpha = 0.8f))
                    } else {
                        Text(
                            text = "Save Prayer Times",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

// ─── Next Prayer Banner ───────────────────────────────────────────────────────

@Composable
private fun NextPrayerBanner(
    prayerName: String,
    prayerTime: Long,
    timeUntilPrayer: Long
) {
    val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val formattedTime = formatter.format(java.util.Date(prayerTime))
    val hours   = TimeUnit.MILLISECONDS.toHours(timeUntilPrayer)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(timeUntilPrayer) % 60
    val countdown = if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(listOf(GreenPrimary, GreenLight)),
                RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Next Prayer",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.75f)
                )
                Text(
                    text = prayerName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "⏱", fontSize = 20.sp)
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "in $countdown",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}

// ─── Prayer Time Row ──────────────────────────────────────────────────────────

@Composable
private fun PrayerTimeRow(
    meta: PrayerMeta,
    hour: Int,
    minute: Int,
    onPicked: (Int, Int) -> Unit
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon + name
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(meta.accentColor.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = meta.emoji, fontSize = 20.sp)
            }
            Text(
                text = meta.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }

        // Time picker button
        OutlinedButton(
            onClick = {
                TimePickerDialog(
                    context,
                    { _, h, m -> onPicked(h, m) },
                    hour, minute, false
                ).show()
            },
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (hour != 0 || minute != 0) GreenPrimary
                else MaterialTheme.colorScheme.outlineVariant
            )
        ) {
            Text(
                text = formatTimeFromHourMinute(hour, minute),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (hour != 0 || minute != 0) GreenPrimary
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Spacer(Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Outlined.AccessTime,
                contentDescription = "Pick time",
                modifier = Modifier.size(16.dp),
                tint = if (hour != 0 || minute != 0) GreenPrimary
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
    }
}

// ─── Helpers ──────────────────────────────────────────────────────────────────

private fun formatTimeFromHourMinute(hour: Int, minute: Int): String {
    val cal = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
    }
    return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(cal.time)
}

private data class PrayerTimeState(val hour: Int, val minute: Int, val onTimeSelected: (Int, Int) -> Unit)

@Preview(showBackground = true)
@Composable
private fun PreviewSalahTimings() {
    SalahSyncTheme { AddUpdateSalahTimingsScreen() }
}