package com.durranitech.salahsync.presentation.muqtadi.screens

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Mosque
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.durranitech.salahsync.domain.model.Announcement
import com.durranitech.salahsync.domain.model.Masjid
import com.durranitech.salahsync.domain.model.SalahTime
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
import kotlinx.coroutines.launch

// ─── Design tokens ────────────────────────────────────────────────────────────
private val IndigoPrimary = Color(0xFF3730A3)
private val IndigoMid = Color(0xFF4F46E5)
private val IndigoLight = Color(0xFF6366F1)
private val IndigoSurface = Color(0xFFEEF2FF)
private val GreenPrimary = Color(0xFF1B8C5E)
private val GreenSurface = Color(0xFFE8F5EF)
private val GoldAccent = Color(0xFFD97706)
private val LogoutRed = Color(0xFFE53935)

private val announcementAccents = listOf(
    IndigoMid, Color(0xFF0891B2), GoldAccent, Color(0xFF059669), Color(0xFF7C3AED)
)
private val prayerNames = listOf("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha")
private val prayerEmojis = listOf("🌄", "☀️", "🌤️", "🌅", "🌙")

// ─── Screen ───────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuqtadiDashboardScreen(
    paddingValues: PaddingValues = PaddingValues(),
    onNavigateToAnnouncements: () -> Unit = {},
    onNavigateToMasjid: () -> Unit = {},
    onNavigateToRoleSelectionScreen: () -> Unit = {},
    viewModel: MuqtadiViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val authState by authViewModel.state.collectAsStateWithLifecycle()
    val membershipResolved by viewModel.membershipResolved.collectAsStateWithLifecycle()
    val joinedMasjidId by viewModel.joinedMasjidId.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val filteredMasjids by viewModel.filteredMasjids.collectAsStateWithLifecycle()
    val isJoiningLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(filteredMasjids) {
        Log.d("masjid list", "$filteredMasjids")
    }

    MuqtadiDashboardContent(
        uiState = uiState,
        authState = authState,
        membershipResolved = membershipResolved,
        joinedMasjidId = joinedMasjidId,
        masjids = filteredMasjids,
        searchQuery = searchQuery,
        isJoiningLoading = isJoiningLoading,
        snackbarHostState = snackbarHostState,
        onNavigateToAnnouncements = onNavigateToAnnouncements,
        onNavigateToMasjid = onNavigateToMasjid,
        onLogout = {
            authViewModel.onAuthEvent(AuthIntent.LogOut)
            onNavigateToRoleSelectionScreen()
        },
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onJoinMasjid = { masjid ->
            viewModel.joinMasjid(
                masjid = masjid,
                onSuccess = { /* state update drives UI */ },
                onError = { msg ->
                    viewModel.viewModelScope.launch { snackbarHostState.showSnackbar(msg) }
                }
            )
        }
    )
}

// ─── Content Router ───────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuqtadiDashboardContent(
    uiState: UiState,
    authState: AuthState,
    membershipResolved: Boolean,
    joinedMasjidId: String?,
    masjids: List<Masjid>,
    searchQuery: String,
    isJoiningLoading: Boolean,
    snackbarHostState: SnackbarHostState,
    onNavigateToAnnouncements: () -> Unit,
    onNavigateToMasjid: () -> Unit,
    onLogout: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onJoinMasjid: (Masjid) -> Unit,
    muqtadiViewModel: MuqtadiViewModel = hiltViewModel()
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = authState.user?.name?.let { "Welcome, $it" } ?: "My Masjid",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
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
                                Icons.AutoMirrored.Outlined.Logout, "Sign Out",
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
                "no_masjid" -> {
                    NoMasjidScreen(
                        masjids = masjids,
                        searchQuery = searchQuery,
                        isJoiningLoading = isJoiningLoading,
                        onSearchQueryChange = onSearchQueryChange,
                        onJoinMasjid = onJoinMasjid,
                        modifier = Modifier.padding(innerPadding)
                    )
                }

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

// ─── No Masjid Screen ─────────────────────────────────────────────────────────

@Composable
private fun NoMasjidScreen(
    masjids: List<Masjid>,
    searchQuery: String,
    isJoiningLoading: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onJoinMasjid: (Masjid) -> Unit,
    modifier: Modifier = Modifier
) {
    var showList by remember { mutableStateOf(false) }
    var joiningId by remember { mutableStateOf<String?>(null) }

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    // Ambient pulse
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            tween(1800, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            tween(1800, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ── Hero illustration ─────────────────────────────────────────────
        AnimatedVisibility(
            visible = visible,
            enter = scaleIn(tween(500, easing = EaseOutBack)) + fadeIn(tween(500))
        ) {
            Box(contentAlignment = Alignment.Center) {
                // Outer pulse ring
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .scale(pulseScale)
                        .background(IndigoLight.copy(alpha = pulseAlpha * 0.15f), CircleShape)
                )
                // Mid ring
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .background(IndigoSurface, CircleShape)
                )
                // Icon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            Brush.linearGradient(listOf(IndigoPrimary, IndigoMid)),
                            RoundedCornerShape(22.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.Mosque, null,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(28.dp))

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(500, delayMillis = 100)) +
                    slideInVertically(tween(500, delayMillis = 100)) { it / 3 }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "No Masjid Connected",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Join a masjid community to receive prayer\ntimes and announcements in real-time.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }
        }

        Spacer(Modifier.height(28.dp))

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(500, delayMillis = 200))
        ) {
            // Feature pills row
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FeaturePill("🕌", "Prayer Times", Modifier.weight(1f))
                FeaturePill("📢", "Announcements", Modifier.weight(1f))
                FeaturePill("👥", "Community", Modifier.weight(1f))
            }
        }

        Spacer(Modifier.height(28.dp))

        // ── CTA button ────────────────────────────────────────────────────
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(500, delayMillis = 280))
        ) {
            Button(
                onClick = { showList = !showList },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
            ) {
                Icon(
                    Icons.Outlined.Mosque, null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (showList) "Hide Masjid List" else "Browse Masjids",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // ── Masjid list ───────────────────────────────────────────────────
        AnimatedVisibility(
            visible = showList,
            enter = fadeIn(tween(300)) + slideInVertically(tween(300)) { -it / 4 },
            exit = fadeOut(tween(200)) + slideOutVertically(tween(200)) { -it / 4 }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Search field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = {
                        Text(
                            "Search by name, city or imam…",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Search, null,
                            tint = if (searchQuery.isNotEmpty()) IndigoPrimary
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = IndigoSurface.copy(alpha = 0.5f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                            alpha = 0.3f
                        ),
                        focusedBorderColor = IndigoPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
                )

                when {
                    masjids.isEmpty() && searchQuery.isBlank() -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                    RoundedCornerShape(14.dp)
                                )
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                CircularProgressIndicator(
                                    color = IndigoPrimary,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    "Loading masjids…",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }

                    masjids.isEmpty() && searchQuery.isNotBlank() -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                    RoundedCornerShape(14.dp)
                                )
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("🔍", fontSize = 28.sp)
                                Text(
                                    "No masjids match $searchQuery",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    else -> {
                        Text(
                            text = "${masjids.size} masjid${if (masjids.size != 1) "s" else ""} available",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                        )

                        masjids.forEachIndexed { index, masjid ->
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn(tween(250, delayMillis = index * 50)) +
                                        slideInVertically(
                                            tween(
                                                250,
                                                delayMillis = index * 50
                                            )
                                        ) { it / 3 }
                            ) {
                                MasjidListCard(
                                    masjid = masjid,
                                    isJoining = joiningId == masjid.id && isJoiningLoading,
                                    onJoinClick = {
                                        joiningId = masjid.id
                                        onJoinMasjid(masjid)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

// ─── Masjid List Card ─────────────────────────────────────────────────────────

@Composable
private fun MasjidListCard(
    masjid: Masjid,
    isJoining: Boolean,
    onJoinClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.98f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy),
        label = "card_scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Mosque icon badge
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(IndigoSurface, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("🕌", fontSize = 22.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = masjid.name.ifBlank { "Unnamed Masjid" },
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                // City row
                if (masjid.city.isNotBlank() || masjid.address.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Icon(
                            Icons.Outlined.LocationOn, null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = masjid.city.ifBlank { masjid.address },
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                // Imam row
                if (masjid.imamName.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        modifier = Modifier.padding(top = 1.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Person, null,
                            tint = IndigoPrimary.copy(alpha = 0.6f),
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = "Imam: ${masjid.imamName}",
                            style = MaterialTheme.typography.labelSmall,
                            color = IndigoPrimary.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Join button
            Button(
                onClick = {
                    pressed = true
                    onJoinClick()
                },
                enabled = !isJoining,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = IndigoPrimary,
                    disabledContainerColor = IndigoPrimary.copy(alpha = 0.5f)
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
            ) {
                if (isJoining) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(14.dp)
                    )
                } else {
                    Text(
                        "Join",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ─── Feature Pill ─────────────────────────────────────────────────────────────

@Composable
private fun FeaturePill(emoji: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(IndigoSurface.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(emoji, fontSize = 20.sp)
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = IndigoPrimary,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

// ─── Home Screen ─────────────────────────────────────────────────────────────

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
            AnnouncementsSection(uiState = uiState, onViewAllAnnouncements = onViewAllAnnouncements)
            uiState.errorMessage?.let {
                Text(
                    it, color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall, modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

// ─── Membership Chip ──────────────────────────────────────────────────────────

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
                    masjidName, style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis
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
                        "Muqtadi · Active Member", style = MaterialTheme.typography.labelSmall,
                        color = IndigoPrimary, fontWeight = FontWeight.Medium
                    )
                }
            }
            Icon(
                Icons.Outlined.ChevronRight,
                null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// ─── Next Prayer Hero ─────────────────────────────────────────────────────────

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
                            .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(14.dp))
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

// ─── Prayer Time Strip ────────────────────────────────────────────────────────

@Composable
private fun PrayerTimeStrip(state: UiState) {
    val salahTime = state.salahTime ?: return
    val times = listOf(
        getTimeInMillis(salahTime.fajrHour ?: 0, salahTime.fajrMinute ?: 0),
        getTimeInMillis(salahTime.dhuhrHour ?: 0, salahTime.dhuhrMinute ?: 0),
        getTimeInMillis(salahTime.asrHour ?: 0, salahTime.asrMinute ?: 0),
        getTimeInMillis(salahTime.maghribHour ?: 0, salahTime.maghribMinute ?: 0),
        getTimeInMillis(salahTime.ishaHour ?: 0, salahTime.ishaMinute ?: 0)
    )
    val activeIndex = times.indexOfLast { System.currentTimeMillis() >= it }.coerceAtLeast(0)

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            "Today's Prayer Times",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Card(
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column {
                Row(modifier = Modifier.fillMaxWidth()) {
                    prayerNames.forEachIndexed { index, name ->
                        val isActive = index == activeIndex
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
                                name, style = MaterialTheme.typography.labelSmall,
                                color = if (isActive) IndigoPrimary else MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.45f
                                ),
                                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
                            )
                            Text(
                                formatTime(times[index]),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isActive) IndigoMid else MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.7f
                                )
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
                        "Current: ${prayerNames.getOrElse(activeIndex) { "—" }}",
                        style = MaterialTheme.typography.labelSmall,
                        color = IndigoPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

// ─── Announcements ────────────────────────────────────────────────────────────

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
                        "No announcements yet", style = MaterialTheme.typography.bodyMedium,
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
                                "+${uiState.announcements.size - 3} more", color = IndigoMid,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── Announcement Card ────────────────────────────────────────────────────────

@Composable
fun MuqtadiAnnouncementCard(announcement: Announcement, index: Int) {
    val accent = announcementAccents[index % announcementAccents.size]
    Card(
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
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
                            null,
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
                        formatDate(announcement.date), style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                    )
                }
            }
        }
    }
}

// ─── Loading State ────────────────────────────────────────────────────────────

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
                    "Checking membership…", style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

// ─── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "No Masjid")
@Composable
private fun NoMasjidPreview() {
    SalahSyncTheme {
        NoMasjidScreen(
            masjids = listOf(
                Masjid(name = "Masjid Al-Noor", city = "Peshawar", imamName = "Sheikh Abdullah"),
                Masjid(name = "Jamia Masjid", city = "Lahore", imamName = "Mufti Hassan")
            ),
            searchQuery = "", isJoiningLoading = false,
            onSearchQueryChange = {}, onJoinMasjid = {}
        )
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
