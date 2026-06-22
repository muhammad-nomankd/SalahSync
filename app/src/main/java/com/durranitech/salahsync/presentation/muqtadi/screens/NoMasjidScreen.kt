/*
package com.durranitech.salahsync.presentation.muqtadi.screens

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
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Mosque
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.durranitech.salahsync.domain.model.Masjid
import com.durranitech.salahsync.ui.theme.SalahSyncTheme


// ─── Design tokens ────────────────────────────────────────────────────────────

private val IndigoSurface = Color(0xFFEEF2FF)
private val IndigoPrimary = Color(0xFF3730A3)
private val IndigoMid = Color(0xFF4F46E5)
private val IndigoLight = Color(0xFF6366F1)


@Composable
fun NoMasjidScreen(
    masjids: List<Masjid>,
    searchQuery: String,
    isJoiningLoading: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onJoinMasjid: (Masjid) -> Unit,
    modifier: Modifier = Modifier,
    onScanQr: () -> Unit,
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
            Column(modifier.fillMaxWidth()) {
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
                Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onScanQr,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Outlined.QrCodeScanner, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Scan QR code", style = MaterialTheme.typography.titleSmall)
            }
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

@Preview(showBackground = true)
@Composable
private fun NoMasjidScreenPreview() {
    SalahSyncTheme {
        NoMasjidScreen(
            masjids = listOf(
                Masjid(
                    id = "1",
                    name = "Masjid Al-Noor",
                    city = "New York",
                    imamName = "Sheikh Abdullah"
                ),
                Masjid(
                    id = "2",
                    name = "Central Mosque",
                    city = "London",
                    imamName = "Imam Ahmed"
                )
            ),
            searchQuery = "",
            isJoiningLoading = false,
            onSearchQueryChange = {},
            onJoinMasjid = {},
            onScanQr = {}
        )
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
}*/
