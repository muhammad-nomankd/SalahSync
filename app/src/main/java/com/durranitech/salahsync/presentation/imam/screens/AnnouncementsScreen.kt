package com.durranitech.salahsync.presentation.imam.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.durranitech.salahsync.domain.model.Announcement
import com.durranitech.salahsync.presentation.imam.components.CreateAnnouncementScreen
import com.durranitech.salahsync.presentation.imam.components.EmptyAnnouncementScreen
import com.durranitech.salahsync.presentation.imam.viewmodel.ImamViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

// ─── Color Palette ────────────────────────────────────────────────────────────

private val GreenPrimary = Color(0xFF1B8C5E)
private val GreenLight = Color(0xFFE8F5EF)
private val GreenBorder = Color(0xFFB6DEC9)
private val GreenText = Color(0xFF0F6E56)

private val AmberAccent = Color(0xFFFFA726)
private val AmberSurface = Color(0xFFFFF3E0)
private val AmberText = Color(0xFFE65100)

private val PurpleAccent = Color(0xFF5C6BC0)
private val PurpleSurface = Color(0xFFEDE7F6)
private val PurpleText = Color(0xFF4527A0)

private val DeleteRed = Color(0xFFE53935)
private val DeleteSurface = Color(0xFFFFEBEE)


// ─── Screen ───────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementsScreen(
    viewModel: ImamViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val announcements = uiState.announcements

    var isCreating by remember { mutableStateOf(false) }
    var announcementToDelete by remember { mutableStateOf<String?>(null) }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    // ── Delete Dialog ────────────────────────────────────────────────────────
    announcementToDelete?.let { id ->
        DeleteConfirmationDialog(
            onConfirmDelete = {
                viewModel.deleteAnnouncement(id)
                announcementToDelete = null
            },
            onDismiss = { announcementToDelete = null }
        )
    }

    // ── Screen transition: list ↔ create ─────────────────────────────────────
    AnimatedContent(
        targetState = isCreating,
        transitionSpec = {
            if (targetState) {
                (slideInVertically(tween(350)) { it } + fadeIn(tween(350))) togetherWith
                        (slideOutVertically(tween(350)) { -it / 4 } + fadeOut(tween(200)))
            } else {
                (slideInVertically(tween(350)) { -it / 4 } + fadeIn(tween(350))) togetherWith
                        (slideOutVertically(tween(350)) { it } + fadeOut(tween(200)))
            }
        },
        label = "announcements_screen_transition"
    ) { creating ->

        if (creating) {
            CreateAnnouncementScreen(
                onNavigateBack = { isCreating = false },
                onPostAnnouncement = { title, content ->
                    viewModel.addAnnouncement(
                        Announcement(
                            id = UUID.randomUUID().toString(),
                            title = title.trim(),
                            description = content.trim(),
                            date = System.currentTimeMillis()
                        )
                    )
                    isCreating = false
                },
                state = uiState
            )
        } else {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        TopAppBar(
                            title = {
                                Column {
                                    Text(
                                        text = "Masjid Board",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${announcements.size} announcement${if (announcements.size != 1) "s" else ""}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                }
                            },
                            actions = {
                                // Search icon
                                IconButton(onClick = { /* TODO: open search */ }) {
                                    Icon(
                                        imageVector = Icons.Outlined.Search,
                                        contentDescription = "Search announcements",
                                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                                // More options icon
                                IconButton(onClick = { /* TODO: open menu */ }) {
                                    Icon(
                                        imageVector = Icons.Outlined.MoreVert,
                                        contentDescription = "More options",
                                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            scrollBehavior = scrollBehavior
                        )
                    }
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { isCreating = true },
                        containerColor = GreenPrimary,
                        contentColor = Color.White,
                        elevation = FloatingActionButtonDefaults.elevation(0.dp),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .size(52.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "New announcement",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                floatingActionButtonPosition = FabPosition.End,
                containerColor = MaterialTheme.colorScheme.background,
                contentWindowInsets = WindowInsets.safeDrawing
            ) { innerPadding ->

                when {
                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = GreenPrimary, strokeWidth = 2.dp)
                        }
                    }

                    announcements.isEmpty() -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            contentAlignment = Alignment.Center
                        ) {
                            EmptyAnnouncementScreen(
                                onAddNewAnnouncement = { isCreating = true },
                                onManageMasjidData = {}
                            )
                        }
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            contentPadding = PaddingValues(
                                start = 16.dp, end = 16.dp,
                                top = 12.dp, bottom = 100.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            // ── Announcement cards ──────────────────────────
                            itemsIndexed(
                                items = announcements,
                                key = { _, a -> a.id }
                            ) { index, announcement ->
                                AnimatedVisibility(
                                    visible = true,
                                    enter = fadeIn(tween(300, delayMillis = index * 60)) +
                                            slideInVertically(
                                                tween(300, delayMillis = index * 60)
                                            ) { it / 3 }
                                ) {
                                    AnnouncementCard(
                                        announcement = announcement,
                                        index = index,
                                        onDeleteClick = { announcementToDelete = announcement.id }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── Pinned Banner ────────────────────────────────────────────────────────────

@Composable
private fun PinnedBanner(announcement: Announcement) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(GreenLight)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Outlined.PushPin,
            contentDescription = null,
            tint = GreenPrimary,
            modifier = Modifier
                .size(18.dp)
                .padding(top = 1.dp)
        )
        Column {
            Text(
                text = "📌 Pinned — ${announcement.title}",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = GreenText
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = announcement.description,
                fontSize = 12.sp,
                color = GreenPrimary,
                lineHeight = 18.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ─── Announcement Card ────────────────────────────────────────────────────────

private data class CardStyle(
    val topBarColor: Color,
    val badgeBg: Color,
    val badgeFg: Color,
    val tagBg: Color,
    val tagFg: Color,
    val tagLabel: String,
    val icon: ImageVector
)

private fun cardStyleForIndex(index: Int): CardStyle = when (index % 3) {
    0 -> CardStyle(
        topBarColor = GreenPrimary,
        badgeBg = GreenLight,
        badgeFg = GreenPrimary,
        tagBg = GreenLight,
        tagFg = GreenText,
        tagLabel = "General",
        icon = Icons.Outlined.Campaign
    )

    1 -> CardStyle(
        topBarColor = AmberAccent,
        badgeBg = AmberSurface,
        badgeFg = AmberAccent,
        tagBg = AmberSurface,
        tagFg = AmberText,
        tagLabel = "Event",
        icon = Icons.Outlined.CalendarMonth
    )

    else -> CardStyle(
        topBarColor = PurpleAccent,
        badgeBg = PurpleSurface,
        badgeFg = PurpleAccent,
        tagBg = PurpleSurface,
        tagFg = PurpleText,
        tagLabel = "Education",
        icon = Icons.Outlined.School
    )
}

@Composable
private fun AnnouncementCard(
    announcement: Announcement,
    index: Int,
    onDeleteClick: () -> Unit
) {
    val style = cardStyleForIndex(index)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // ── Thin colored top bar ────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(style.topBarColor)
            )

            // ── Card body ───────────────────────────────────────────────────
            Column(modifier = Modifier.padding(14.dp, 14.dp, 14.dp, 14.dp)) {

                // Header: icon badge + title + delete
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        // Icon badge
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(style.badgeBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = style.icon,
                                contentDescription = null,
                                tint = style.badgeFg,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            // Category tag pill
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(style.tagBg)
                                    .padding(horizontal = 7.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = style.tagLabel.uppercase(),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = style.tagFg,
                                    letterSpacing = 0.6.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = announcement.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Delete button
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete announcement",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Description
                Text(
                    text = announcement.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 19.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Footer: time label + Read more
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🕐  ${formatAnnouncementDate(announcement.date)}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }
}

// ─── Delete Dialog ────────────────────────────────────────────────────────────

@Composable
private fun DeleteConfirmationDialog(
    onConfirmDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(DeleteSurface, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null,
                    tint = DeleteRed,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        title = {
            Text(
                text = "Delete Announcement",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Text(
                text = "This announcement will be permanently removed from the masjid board. This action cannot be undone.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirmDelete,
                colors = ButtonDefaults.buttonColors(containerColor = DeleteRed),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Delete", fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

// ─── Helpers ──────────────────────────────────────────────────────────────────

private fun formatAnnouncementDate(millis: Long): String {
    val diff = System.currentTimeMillis() - millis
    return when {
        diff < 60_000L -> "Just now"
        diff < 3_600_000L -> "${diff / 60_000}m ago"
        diff < 86_400_000L -> "${diff / 3_600_000}h ago"
        diff < 172_800_000L -> "Yesterday"
        else -> SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(millis))
    }
}