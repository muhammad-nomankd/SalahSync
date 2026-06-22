package com.durranitech.salahsync.presentation.imam.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.durranitech.salahsync.domain.model.Member
import com.durranitech.salahsync.presentation.imam.components.AddMemberScreen
import com.durranitech.salahsync.presentation.imam.viewmodel.ImamViewModel
import formatDate
import java.util.UUID

// ─── Color Palette ────────────────────────────────────────────────────────────

private val GreenPrimary = Color(0xFF1B8C5E)
private val GreenSurface = Color(0xFFE8F5EF)
private val DeleteRed = Color(0xFFE53935)
private val DeleteSurface = Color(0xFFFFEBEE)

// Cycles per member card — same palette as AnnouncementsScreen for consistency
private val accentPalette = listOf(
    Color(0xFF1B8C5E),
    Color(0xFF5C6BC0),
    Color(0xFFFFA726),
    Color(0xFFE57373),
    Color(0xFF7E57C2),
    Color(0xFF00897B)
)

// Matching surface colors for top bars & badges
private val accentSurfaces = listOf(
    Color(0xFFE8F5EF),
    Color(0xFFEDE7F6),
    Color(0xFFFFF3E0),
    Color(0xFFFFEBEE),
    Color(0xFFEDE7F6),
    Color(0xFFE0F2F1)
)

// ─── Screen ───────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembersScreen(
    viewModel: ImamViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var showAddMember by remember { mutableStateOf(false) }
    var memberToDelete by remember { mutableStateOf<Member?>(null) }

    LaunchedEffect(state.successMessage) {
        state.successMessage?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
    }
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
    }

    memberToDelete?.let { member ->
        DeleteMemberDialog(
            memberName = member.name,
            onConfirm = { viewModel.deleteMember(member.id); memberToDelete = null },
            onDismiss = { memberToDelete = null }
        )
    }

    AnimatedContent(
        targetState = showAddMember,
        transitionSpec = {
            if (targetState) {
                (slideInVertically(tween(350)) { it } + fadeIn(tween(350))) togetherWith
                        (slideOutVertically(tween(350)) { -it / 4 } + fadeOut(tween(200)))
            } else {
                (slideInVertically(tween(350)) { -it / 4 } + fadeIn(tween(350))) togetherWith
                        (slideOutVertically(tween(350)) { it } + fadeOut(tween(200)))
            }
        },
        label = "members_screen_transition"
    ) { adding ->

        if (adding) {
            AddMemberScreen(
                onNavigateBack = { showAddMember = false },
                onAddMember = { name, email, phone, address ->
                    viewModel.addMember(
                        Member(
                            id = UUID.randomUUID().toString(),
                            name = name.trim(),
                            email = email.trim(),
                            phoneNumber = phone.trim(),
                            address = address.trim(),
                            joinedDate = System.currentTimeMillis()
                        )
                    )
                    showAddMember = false
                }
            )
        } else {
            Scaffold(
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
                                        text = "Community",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${state.members.size} registered member${if (state.members.size != 1) "s" else ""}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                }
                            },
                            actions = {
                                IconButton(onClick = { /* TODO: search */ }) {
                                    Icon(
                                        imageVector = Icons.Outlined.Search,
                                        contentDescription = "Search members",
                                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                                IconButton(onClick = { /* TODO: menu */ }) {
                                    Icon(
                                        imageVector = Icons.Outlined.MoreVert,
                                        contentDescription = "More options",
                                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    }
                },
                floatingActionButton = {
                    androidx.compose.material3.FloatingActionButton(
                        onClick = { showAddMember = true },
                        containerColor = GreenPrimary,
                        contentColor = Color.White,
                        elevation = androidx.compose.material3.FloatingActionButtonDefaults.elevation(
                            0.dp
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .size(52.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Add,
                            contentDescription = "Add member",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                containerColor = MaterialTheme.colorScheme.background
            ) { innerPadding ->

                when {
                    state.isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                CircularProgressIndicator(color = GreenPrimary, strokeWidth = 2.dp)
                                Text(
                                    text = "Loading members…",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }

                    state.members.isEmpty() -> {
                        EmptyMembersState(innerPadding)
                    }

                    else -> {
                        MembersList(
                            members = state.members,
                            padding = innerPadding,
                            onDelete = { memberToDelete = it }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatPill(
    label: String,
    value: String,
    color: Color,
    bg: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .padding(vertical = 10.dp, horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = color.copy(alpha = 0.7f),
            fontWeight = FontWeight.Medium
        )
    }
}

// ─── Members List ─────────────────────────────────────────────────────────────

@Composable
private fun MembersList(
    members: List<Member>,
    padding: PaddingValues,
    onDelete: (Member) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentPadding = PaddingValues(
            start = 16.dp, end = 16.dp,
            top = 12.dp, bottom = 100.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(
            items = members,
            key = { _, m -> m.id }
        ) { index, member ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(tween(300, delayMillis = index * 50)) +
                        slideInVertically(tween(300, delayMillis = index * 50)) { it / 3 }
            ) {
                MemberCard(
                    member = member,
                    colorIndex = index,
                    onDelete = onDelete
                )
            }
        }
    }
}

// ─── Member Card ──────────────────────────────────────────────────────────────

@Composable
private fun MemberCard(
    member: Member,
    colorIndex: Int,
    onDelete: (Member) -> Unit
) {
    val accent = accentPalette[colorIndex % accentPalette.size]
    val surface = accentSurfaces[colorIndex % accentSurfaces.size]

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // ── Thin colored top bar (matches AnnouncementsScreen pattern) ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(accent)
            )

            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                // ── Header: avatar + name + joined + delete ──────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar circle
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(accent),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = member.name.trim()
                                .firstOrNull()
                                ?.uppercase() ?: "",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = member.name,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(3.dp))
                        // Joined date pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(surface)
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Joined ${formatDate(member.joinedDate)}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = accent,
                                letterSpacing = 0.3.sp
                            )
                        }
                    }

                    // Delete button — subtle
                    IconButton(
                        onClick = { onDelete(member) },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Remove member",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // ── Divider ──────────────────────────────────────────────────
                HorizontalDivider(
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                )

                // ── Contact rows ─────────────────────────────────────────────
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (member.email.isNotBlank()) {
                        ContactRow(icon = Icons.Outlined.Email, tint = accent, text = member.email)
                    }
                    if (member.phoneNumber.isNotBlank()) {
                        ContactRow(
                            icon = Icons.Outlined.Phone,
                            tint = accent,
                            text = member.phoneNumber
                        )
                    }
                    if (member.address.isNotBlank()) {
                        ContactRow(
                            icon = Icons.Outlined.LocationOn,
                            tint = accent,
                            text = member.address
                        )
                    }
                }
            }
        }
    }
}

// ─── Contact Row ─────────────────────────────────────────────────────────────

@Composable
private fun ContactRow(
    icon: ImageVector,
    tint: Color,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint.copy(alpha = 0.7f),
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// ─── Empty State ──────────────────────────────────────────────────────────────

@Composable
private fun EmptyMembersState(padding: PaddingValues) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = scaleIn(tween(500)) + fadeIn(tween(500))
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(GreenSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.People,
                        contentDescription = null,
                        tint = GreenPrimary,
                        modifier = Modifier.size(44.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(400, delayMillis = 150))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No members yet",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Add your first community member\nusing the button below.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}

// ─── Delete Dialog ────────────────────────────────────────────────────────────

@Composable
private fun DeleteMemberDialog(
    memberName: String,
    onConfirm: () -> Unit,
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
                text = "Remove Member",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Text(
                text = "$memberName will be permanently removed from the masjid community. This cannot be undone.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = DeleteRed),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Remove", fontWeight = FontWeight.SemiBold)
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