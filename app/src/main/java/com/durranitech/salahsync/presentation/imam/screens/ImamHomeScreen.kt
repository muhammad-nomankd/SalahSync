package com.durranitech.salahsync.presentation.imam.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.durranitech.salahsync.presentation.imam.ImamEvent
import com.durranitech.salahsync.presentation.imam.UiState
import com.durranitech.salahsync.presentation.imam.viewmodel.ImamViewModel
import com.durranitech.salahsync.ui.getTimeInMillis
import com.durranitech.salahsync.util.formatTime
import java.util.Calendar

// ─── Color Palette ────────────────────────────────────────────────────────────
private val PrimaryGreen      = Color(0xFF1B8C5E)
private val PrimaryGreenLight = Color(0xFF26A874)
private val SurfaceCard       = Color(0xFFF8FAF9)
private val ActivePrayer      = Color(0xFFE8F5EF)
private val ActivePrayerText  = Color(0xFF1B8C5E)

// ─── Screen ───────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImamHomeScreen(
    paddingValues: PaddingValues,
    onNavigateToUpdateSalahTimes: () -> Unit = {},
    onCreateNewAnnouncement: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToAnnouncement: () -> Unit = {},
    viewModel: ImamViewModel = hiltViewModel()  // ✅ Only called here, not in children
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        Log.d("ImamHomeScreen", "fajrHour = ${uiState.salahTime?.fajrHour}")
        viewModel.onImamEvent(ImamEvent.ClearMessages)
        viewModel.fetchMasjidDetails()
    }

    // Animate content on first load
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 12 }
        ) {
            HomeContent(
                uiState = uiState,
                onEditTimes = onNavigateToUpdateSalahTimes,
                onCreateNewAnnouncement = onCreateNewAnnouncement,
                onNavigateToSalahTimes = onNavigateToUpdateSalahTimes,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

// ─── Content ──────────────────────────────────────────────────────────────────

@Composable
private fun HomeContent(
    uiState: UiState,
    onEditTimes: () -> Unit,
    onCreateNewAnnouncement: () -> Unit,
    onNavigateToSalahTimes: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        NextPrayerCard(
            prayerName = uiState.nextPrayerName,
            prayerTime = uiState.nextPrayerTime,
            isLoading = uiState.isLoading,
            hasSalahTimes = uiState.salahTime?.fajrHour != null,
            onNavigateToSalahTimes = onNavigateToSalahTimes
        )

        QuickActionsSection(
            onEditTimes = onEditTimes,
            onCreateNewAnnouncement = onCreateNewAnnouncement
        )

        PrayerTimesSection(uiState = uiState)

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ─── Next Prayer Card ─────────────────────────────────────────────────────────

@Composable
private fun NextPrayerCard(
    prayerName: String?,
    prayerTime: Long?,
    isLoading: Boolean,
    hasSalahTimes: Boolean,  // ✅ Pass data, not ViewModel
    onNavigateToSalahTimes: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(PrimaryGreen, PrimaryGreenLight, Color(0xFF3FC28A))
                )
            )
    ) {
        // Decorative circle
        Box(
            modifier = Modifier
                .size(160.dp)
                .align(Alignment.TopEnd)
                .padding(top = 0.dp, end = 0.dp)
                .background(Color.White.copy(alpha = 0.06f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.BottomEnd)
                .padding(bottom = 8.dp, end = 8.dp)
                .background(Color.White.copy(alpha = 0.06f), CircleShape)
        )

        when {
            isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
                }
            }
            !hasSalahTimes -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .clickable { onNavigateToSalahTimes() },
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "🕌", fontSize = 32.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No prayer times set yet",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Tap to add prayer times",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.75f)
                    )
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Next Prayer",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White.copy(alpha = 0.8f),
                        letterSpacing = 1.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = prayerName ?: "—",
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = formatTime(prayerTime ?: 0L),
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color.White.copy(alpha = 0.18f), RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🌙", fontSize = 28.sp)
                        }
                    }
                }
            }
        }
    }
}

// ─── Quick Actions ────────────────────────────────────────────────────────────

@Composable
private fun QuickActionsSection(
    onEditTimes: () -> Unit,
    onCreateNewAnnouncement: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(title = "Quick Actions")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionCard(
                title = "Edit Times",
                subtitle = "Update salah schedule",
                icon = Icons.Outlined.Edit,
                backgroundGradient = listOf(Color(0xFFE8F5EF), Color(0xFFD0EDE2)),
                iconTint = PrimaryGreen,
                onClick = onEditTimes,
                modifier = Modifier.weight(1f)
            )
            QuickActionCard(
                title = "Announce",
                subtitle = "New announcement",
                icon = Icons.Default.Add,
                backgroundGradient = listOf(Color(0xFFE3F2FD), Color(0xFFBBDEFB)),
                iconTint = Color(0xFF1565C0),
                onClick = onCreateNewAnnouncement,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    backgroundGradient: List<Color>,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        Brush.linearGradient(backgroundGradient),
                        RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Column {
                Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                )
            }
        }
    }
}

// ─── Prayer Times Section ─────────────────────────────────────────────────────

@Composable
private fun PrayerTimesSection(uiState: UiState) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(title = "Today's Prayer Times")

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                val salahTime = uiState.salahTime

                when {
                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = PrimaryGreen, strokeWidth = 2.dp)
                        }
                    }
                    salahTime != null -> {
                        val prayers = listOf(
                            Triple("Fajr",    "🌄", getTimeInMillis(salahTime.fajrHour    ?: 0, salahTime.fajrMinute    ?: 0)),
                            Triple("Dhuhr",   "☀️", getTimeInMillis(salahTime.dhuhrHour   ?: 0, salahTime.dhuhrMinute   ?: 0)),
                            Triple("Asr",     "🌤️", getTimeInMillis(salahTime.asrHour     ?: 0, salahTime.asrMinute     ?: 0)),
                            Triple("Maghrib", "🌅", getTimeInMillis(salahTime.maghribHour ?: 0, salahTime.maghribMinute ?: 0)),
                            Triple("Isha",    "🌙", getTimeInMillis(salahTime.ishaHour    ?: 0, salahTime.ishaMinute    ?: 0))
                        )

                        // Determine currently active prayer
                        val nowMillis = System.currentTimeMillis()
                        val activePrayerIndex = prayers.indexOfLast { (_, _, millis) ->
                            nowMillis >= millis
                        }.coerceAtLeast(0)

                        prayers.forEachIndexed { index, (name, icon, millis) ->
                            PrayerTimeRow(
                                prayerName = name,
                                time = formatTime(millis),
                                icon = icon,
                                isActive = index == activePrayerIndex
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
                    else -> {
                        Text(
                            text = "No prayer times available",
                            modifier = Modifier.padding(24.dp).fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PrayerTimeRow(
    prayerName: String,
    time: String,
    icon: String,
    isActive: Boolean = false
) {
    val bgColor = if (isActive) ActivePrayer else Color.Transparent
    val textColor = if (isActive) ActivePrayerText else MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (isActive) PrimaryGreen.copy(alpha = 0.15f)
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 20.sp)
            }
            Text(
                text = prayerName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                color = textColor
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = time,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = if (isActive) ActivePrayerText else MaterialTheme.colorScheme.onSurface
            )
            if (isActive) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .background(PrimaryGreen, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Now",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// ─── Helpers ──────────────────────────────────────────────────────────────────

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
}

