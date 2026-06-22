package com.durranitech.salahsync.presentation.muqtadi.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Mosque
import androidx.compose.material.icons.outlined.QrCodeScanner
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.OutlinedButtonDefaults
import com.durranitech.salahsync.domain.model.Announcement
import com.durranitech.salahsync.domain.model.Masjid
import com.durranitech.salahsync.domain.model.SalahTime
import com.durranitech.salahsync.domain.model.User
import com.durranitech.salahsync.presentation.authentication.AuthIntent
import com.durranitech.salahsync.presentation.authentication.AuthState
import com.durranitech.salahsync.presentation.authentication.viewModel.AuthViewModel
import com.durranitech.salahsync.presentation.imam.UiState
import com.durranitech.salahsync.presentation.muqtadi.viewmodel.MuqtadiViewModel
import com.durranitech.salahsync.ui.getTimeInMillis
import com.durranitech.salahsync.ui.theme.SalahSyncTheme
import com.durranitech.salahsync.util.formatCountdown
import com.durranitech.salahsync.util.formatTime
import formatDate

private val IndigoPrimary = Color(0xFF3730A3)
private val IndigoMid = Color(0xFF4F46E5)
private val IndigoLight = Color(0xFF6366F1)
private val IndigoSurface = Color(0xFFEEF2FF)
private val GreenPrimary = Color(0xFF1B8C5E)
private val GreenSurface = Color(0xFFE8F5EF)
private val GoldAccent = Color(0xFFD97706)
private val LogoutRed = Color(0xFFE53935)
private val BluePrimary = Color(0xFF1E40AF)
private val BlueLight = Color(0xFF3B82F6)
private val BlueSurface = Color(0xFFEFF6FF)

private val announcementAccents = listOf(
    IndigoMid, Color(0xFF0891B2), GoldAccent, Color(0xFF059669), Color(0xFF7C3AED)
)
private val prayerNames = listOf("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha")
private val prayerEmojis = listOf("🌄", "☀️", "🌤️", "🌅", "🌙")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuqtadiDashboardScreen(
    paddingValues: PaddingValues = PaddingValues(),
    onNavigateToAnnouncements: () -> Unit = {},
    onNavigateToMasjid: () -> Unit = {},
    onNavigateToRoleSelectionScreen: () -> Unit = {},
    onNavigateToScanQr: () -> Unit = {},
    onNavigateToBrowseMasjid: () -> Unit = {},
    viewModel: MuqtadiViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val authState by authViewModel.state.collectAsStateWithLifecycle()
    val membershipResolved by viewModel.membershipResolved.collectAsStateWithLifecycle()
    val joinedMasjidId by viewModel.joinedMasjidId.collectAsStateWithLifecycle()
    val isJoiningLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    MuqtadiDashboardContent(
        uiState = uiState,
        authState = authState,
        membershipResolved = membershipResolved,
        joinedMasjidId = joinedMasjidId,
        isJoiningLoading = isJoiningLoading,
        snackbarHostState = snackbarHostState,
        onNavigateToAnnouncements = onNavigateToAnnouncements,
        onNavigateToMasjid = onNavigateToMasjid,
        onNavigateToScanQr = onNavigateToScanQr,
        onNavigateToBrowseMasjid = onNavigateToBrowseMasjid,
        onLogout = {
            authViewModel.onAuthEvent(AuthIntent.LogOut)
            onNavigateToRoleSelectionScreen()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuqtadiDashboardContent(
    uiState: UiState,
    authState: AuthState,
    membershipResolved: Boolean,
    joinedMasjidId: String?,
    isJoiningLoading: Boolean,
    snackbarHostState: SnackbarHostState,
    onNavigateToAnnouncements: () -> Unit,
    onNavigateToMasjid: () -> Unit,
    onNavigateToScanQr: () -> Unit,
    onNavigateToBrowseMasjid: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = authState.user?.name?.let { "Welcome, $it" } ?: "My Masjid",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(LogoutRed.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.AutoMirrored.Outlined.Logout,
                                contentDescription = "Sign Out",
                                tint = LogoutRed,
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

        val screenState = when {
            !membershipResolved -> "loading"
            joinedMasjidId == null -> "no_masjid"
            else -> "home"
        }

        AnimatedContent(
            targetState = screenState,
            transitionSpec = {
                (fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 16 }) togetherWith
                        fadeOut(tween(200))
            },
            label = "muqtadi_state"
        ) { state ->
            when (state) {
                "loading" -> MuqtadiLoadingState(Modifier.padding(innerPadding))

                "no_masjid" -> NoMasjidScreen(
                    onScanQr = onNavigateToScanQr,
                    onBrowseMasjid = onNavigateToBrowseMasjid,
                    isLoading = isJoiningLoading,
                    modifier = Modifier.padding(innerPadding)
                )

                else -> MuqtadiHomeScreen(
                    uiState = uiState,
                    onViewAllAnnouncements = onNavigateToAnnouncements,
                    onNavigateToMasjid = onNavigateToMasjid,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}


@Composable
fun NoMasjidScreen(
    modifier: Modifier = Modifier,
    onScanQr: () -> Unit,
    onBrowseMasjid: () -> Unit,
    isLoading: Boolean = false
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(BlueSurface, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Mosque,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = BlueLight
                )
            }

            Spacer(Modifier.height(22.dp))

            Text(
                text = "No masjid joined yet",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Join a masjid to see prayer times\nand announcements from your community.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(Modifier.height(28.dp))

            Button(
                onClick = onScanQr,
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.QrCodeScanner,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Scan QR code",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), thickness = 0.5.dp)
                Text(
                    text = "or",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
                HorizontalDivider(modifier = Modifier.weight(1f), thickness = 0.5.dp)
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onBrowseMasjid,
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    BluePrimary.copy(alpha = 0.5f)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Find a masjid",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}


@Composable
fun MuqtadiHomeScreen(
    uiState: UiState,
    onViewAllAnnouncements: () -> Unit,
    onNavigateToMasjid: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 16 }
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MasjidMembershipChip(
                masjidName = uiState.masjid?.name ?: "My Masjid",
                onClick = onNavigateToMasjid
            )
            NextPrayerHeroCard(state = uiState)
            if (!uiState.isLoading && uiState.salahTime != null) {
                PrayerTimeStrip(state = uiState)
            }
            AnnouncementsSection(
                uiState = uiState,
                onViewAllAnnouncements = onViewAllAnnouncements
            )
            uiState.errorMessage?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}


@Composable
private fun MasjidMembershipChip(masjidName: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .clickable { onClick() }
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(IndigoSurface, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) { Text("🕌", fontSize = 22.sp) }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    masjidName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(
                        Modifier
                            .size(6.dp)
                            .background(IndigoLight, CircleShape)
                    )
                    Text(
                        "Muqtadi · Active Member",
                        style = MaterialTheme.typography.labelSmall,
                        color = IndigoPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Icon(
                Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}


@Composable
private fun NextPrayerHeroCard(state: UiState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(IndigoPrimary, IndigoMid, IndigoLight)))
    ) {
        Box(
            Modifier
                .size(180.dp)
                .align(Alignment.TopEnd)
                .background(Color.White.copy(alpha = 0.06f), CircleShape)
        )
        Box(
            Modifier
                .size(110.dp)
                .align(Alignment.BottomStart)
                .background(Color.White.copy(alpha = 0.05f), CircleShape)
        )

        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "NEXT PRAYER",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.65f),
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Medium
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            state.nextPrayerName ?: "—",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            formatTime(state.nextPrayerTime ?: 0L),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .background(
                                Color.White.copy(alpha = 0.15f),
                                RoundedCornerShape(14.dp)
                            )
                            .border(
                                0.5.dp,
                                Color.White.copy(alpha = 0.25f),
                                RoundedCornerShape(14.dp)
                            )
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text("⏱", fontSize = 16.sp)
                            Text(
                                formatCountdown(state.timeUntilPrayer ?: 0L),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                "remaining",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun PrayerTimeStrip(state: UiState) {
    val salahTime = state.salahTime ?: return
    val prayers = listOf(
        Triple("Fajr", "🌄", getTimeInMillis(salahTime.fajrHour ?: 0, salahTime.fajrMinute ?: 0)),
        Triple(
            "Dhuhr",
            "☀️",
            getTimeInMillis(salahTime.dhuhrHour ?: 0, salahTime.dhuhrMinute ?: 0)
        ),
        Triple("Asr", "🌤️", getTimeInMillis(salahTime.asrHour ?: 0, salahTime.asrMinute ?: 0)),
        Triple(
            "Maghrib",
            "🌅",
            getTimeInMillis(salahTime.maghribHour ?: 0, salahTime.maghribMinute ?: 0)
        ),
        Triple("Isha", "🌙", getTimeInMillis(salahTime.ishaHour ?: 0, salahTime.ishaMinute ?: 0))
    )
    val nowMillis = System.currentTimeMillis()
    val nextPrayerIndex = prayers.indexOfFirst { (_, _, millis) -> millis > nowMillis }
        .let { if (it == -1) 0 else it }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            "Today's Prayer Times",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column {
                Row(modifier = Modifier.fillMaxWidth()) {
                    prayerNames.forEachIndexed { index, name ->
                        val isActive = index == nextPrayerIndex
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(if (isActive) IndigoSurface else Color.Transparent)
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(prayerEmojis[index], fontSize = if (isActive) 20.sp else 16.sp)
                            Text(
                                name,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isActive) IndigoPrimary
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
                                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
                            )
                            Text(
                                formatTime(prayers[index].third),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isActive) IndigoMid
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Box(
                                Modifier
                                    .size(if (isActive) 5.dp else 0.dp)
                                    .background(IndigoMid, CircleShape)
                            )
                        }
                        if (index < prayerNames.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier
                                    .height(72.dp)
                                    .width(0.5.dp)
                                    .align(Alignment.CenterVertically),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(IndigoSurface.copy(alpha = 0.6f))
                        .padding(horizontal = 16.dp, vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Current: ${prayerNames.getOrElse(nextPrayerIndex) { "—" }}",
                        style = MaterialTheme.typography.labelSmall,
                        color = IndigoPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}


@Composable
private fun AnnouncementsSection(uiState: UiState, onViewAllAnnouncements: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Announcements",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (uiState.announcements.isNotEmpty()) {
                    Box(
                        Modifier
                            .background(IndigoSurface, RoundedCornerShape(20.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            "${uiState.announcements.size}",
                            style = MaterialTheme.typography.labelSmall,
                            color = IndigoPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            if (uiState.announcements.size > 3) {
                TextButton(onClick = onViewAllAnnouncements) {
                    Text(
                        "View all",
                        style = MaterialTheme.typography.labelMedium,
                        color = IndigoMid
                    )
                }
            }
        }

        when {
            uiState.isLoading -> Box(
                Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = IndigoMid, strokeWidth = 2.dp)
            }

            uiState.announcements.isEmpty() -> Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        RoundedCornerShape(14.dp)
                    )
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("📢", fontSize = 28.sp)
                    Text(
                        "No announcements yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Text(
                        "Your imam will post updates here",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
                    )
                }
            }

            else -> {
                uiState.announcements.take(3).forEachIndexed { index, announcement ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(tween(300, delayMillis = index * 70)) +
                                slideInVertically(tween(300, delayMillis = index * 70)) { it / 3 }
                    ) {
                        MuqtadiAnnouncementCard(announcement = announcement, index = index)
                    }
                }
                if (uiState.announcements.size > 3) {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        TextButton(onClick = onViewAllAnnouncements) {
                            Text(
                                "+${uiState.announcements.size - 3} more",
                                color = IndigoMid,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun MuqtadiAnnouncementCard(announcement: Announcement, index: Int) {
    val accent = announcementAccents[index % announcementAccents.size]
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                Modifier
                    .width(4.dp)
                    .height(100.dp)
                    .background(Brush.verticalGradient(listOf(accent, accent.copy(alpha = 0.3f))))
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        Modifier
                            .size(32.dp)
                            .background(accent.copy(alpha = 0.1f), RoundedCornerShape(9.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.Campaign,
                            contentDescription = null,
                            tint = accent,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        announcement.title.replaceFirstChar { it.uppercaseChar() },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
                Text(
                    announcement.description.replaceFirstChar { it.uppercaseChar() },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )
                Box(
                    Modifier
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            RoundedCornerShape(5.dp)
                        )
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                ) {
                    Text(
                        formatDate(announcement.date),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                    )
                }
            }
        }
    }
}


@Composable
fun MuqtadiLoadingState(modifier: Modifier = Modifier) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        AnimatedVisibility(visible = visible, enter = scaleIn(tween(400)) + fadeIn(tween(400))) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(IndigoSurface, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = IndigoMid,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Text(
                    "Checking membership…",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}


@Preview(showBackground = true, name = "No Masjid")
@Composable
private fun NoMasjidPreview() {
    SalahSyncTheme {
        NoMasjidScreen(onScanQr = {}, onBrowseMasjid = {})
    }
}

@Preview(showBackground = true, name = "Home")
@Composable
private fun HomePreview() {
    SalahSyncTheme {
        MuqtadiHomeScreen(
            uiState = UiState(
                masjid = Masjid(name = "Grand Mosque"),
                salahTime = SalahTime(5, 0, 13, 0, 16, 30, 18, 45, 20, 15),
                announcements = listOf(
                    Announcement(
                        "1",
                        "Friday Khutbah",
                        "Jummah at 1:30 PM",
                        System.currentTimeMillis()
                    )
                ),
                nextPrayerName = "Asr",
                nextPrayerTime = System.currentTimeMillis() + 3600000,
                timeUntilPrayer = 3600000
            ),
            onViewAllAnnouncements = {}
        )
    }
}

@Preview(showBackground = true, name = "Dashboard")
@Composable
private fun MuqtadiDashboardContentPreview() {
    SalahSyncTheme {
        MuqtadiDashboardContent(
            uiState = UiState(
                masjid = Masjid(name = "Grand Mosque"),
                salahTime = SalahTime(5, 0, 13, 0, 16, 30, 18, 45, 20, 15),
                announcements = listOf(
                    Announcement(
                        "1",
                        "Friday Khutbah",
                        "Jummah at 1:30 PM",
                        System.currentTimeMillis()
                    )
                ),
                nextPrayerName = "Asr",
                nextPrayerTime = System.currentTimeMillis() + 3600000,
                timeUntilPrayer = 3600000
            ),
            authState = AuthState(user = User(name = "Noman")),
            membershipResolved = true,
            joinedMasjidId = "1",
            isJoiningLoading = false,
            snackbarHostState = remember { SnackbarHostState() },
            onNavigateToAnnouncements = {},
            onNavigateToMasjid = {},
            onNavigateToScanQr = {},
            onNavigateToBrowseMasjid = {},
            onLogout = {}
        )
    }
}