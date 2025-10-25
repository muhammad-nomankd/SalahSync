package com.durranitech.salahsync.presentation.imam.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.durranitech.salahsync.domain.model.Announcement
import com.durranitech.salahsync.domain.model.Member
import com.durranitech.salahsync.presentation.imam.components.EmptyMasjidPlaceholder
import com.durranitech.salahsync.presentation.imam.viewmodel.ImamViewModel

@Composable
fun MasjidScreen(
    modifier: Modifier = Modifier,
    onEditDetails: () -> Unit = {},
    onShareMasjid: () -> Unit = {},
    onViewAllMembers: () -> Unit = {},
    onAddAnnouncement: () -> Unit = {},
    onManageData: () -> Unit = {},
    viewModel: ImamViewModel = hiltViewModel(),
    onCreateMasjid: () -> Unit = {}
) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val imamName = state.masjid?.imamName ?:""
    val imamId = state.masjid?.imamId ?:""
    val masjidCode = state.masjid?.code ?:""
    val masjidName = state.masjid?.name?:""
    val masjidAddress = state.masjid?.address?:""

    Scaffold { padding ->
        if (state.masjid != null){
            MasjidDetailsScreenContent(
                imamName = imamName,
                imamId = imamId,
                masjidCode = masjidCode,
                masjidName = masjidName,
                masjidAddress = masjidAddress,
                padding = padding,
                onEditDetails = onEditDetails,
                onViewAllMembers = onViewAllMembers,
                onAddAnnouncement = onAddAnnouncement,
                onManageData = onManageData,
                onCreateMasjid = onCreateMasjid

            )
        } else if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            EmptyMasjidPlaceholder(onCreateMasjid = {onCreateMasjid()})
        }
    }
}

@Composable
fun MasjidDetailsScreenContent(
    imamName: String,
    imamId: String,
    masjidCode: String,
    masjidName: String,
    masjidAddress: String,
    padding: PaddingValues,
    onEditDetails: () -> Unit = {},
    onViewAllMembers: () -> Unit = {},
    onAddAnnouncement: () -> Unit = {},
    onManageData: () -> Unit = {},
    onCreateMasjid: () -> Unit = {}
) {

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Main Masjid Info Card
        item {
            MasjidInfoCard(
                imamName,imamId,masjidCode,masjidName,masjidAddress,
                onEditDetails = onEditDetails, onCopyCode = { code ->
                    clipboardManager.setText(AnnotatedString(code))
                    Toast.makeText(context, "Copied to clipboard $code", Toast.LENGTH_SHORT).show()

                })
        }

        // Linked Members Section
        item {
            LinkedMembersSection(
                members = listOf(Member(name = "John Doe", "imam@gmail.com")),
                onViewAll = onViewAllMembers
            )
        }

        // Past Announcements Section
        item {
            PastAnnouncementsSection(
                announcements = listOf(
                    Announcement(
                        id = "123456",
                        title = "Eid Al-Fitr Announcement",
                        description = "Eid Al-Fitr will be celebrated on June 15th. Please join us for special prayers and festivities.",
                        date = System.currentTimeMillis().toString()
                    )
                )
            )
        }

        // Action Buttons
        item {
            ActionButtons(
                onAddAnnouncement = onAddAnnouncement, onManageData = onManageData
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MasjidDetailsTopBar(
    onEditClick: () -> Unit, onShareClick: () -> Unit
) {
    TopAppBar(
        title = {
        Text(
            text = "Masjid Details",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
    }, actions = {
        IconButton(onClick = onEditClick) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = "Edit",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        IconButton(onClick = onShareClick) {
            Icon(
                imageVector = Icons.Outlined.Share,
                contentDescription = "Share",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }, colors = TopAppBarDefaults.topAppBarColors(
        containerColor = Color.Transparent
    )
    )
}

@Composable
private fun MasjidInfoCard(imamName: String,imamId: String,masjidCode: String,masjidName: String, masjidAddress: String,
    onEditDetails: () -> Unit, onCopyCode: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Masjid Name
            Text(
                text = masjidName ,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Address
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = masjidAddress,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Imam
            Text(
                text = imamName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Unique Code Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Unique Masjid Code:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                        )
                        Text(
                            text = masjidCode,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }

                    FilledIconButton(
                        onClick = { onCopyCode(masjidCode) },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ContentCopy,
                            contentDescription = "Copy code",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Edit Details Button
            OutlinedButton(
                onClick = onEditDetails,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Text(
                    text = "Edit Details",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun LinkedMembersSection(
    members: List<Member>, onViewAll: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Linked Members",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                TextButton(onClick = onViewAll) {
                    Text(
                        text = "View All Members",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(members) { member ->
                    MemberItem(member = member)
                }
            }
        }
    }
}

@Composable
private fun MemberItem(member: Member) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.width(80.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFFB74D), Color(0xFFFF9800)
                        )
                    )
                ), contentAlignment = Alignment.Center
        ) {
            // Use member initial or icon
            Text(
                text = member.name.first().toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Text(
            text = member.name,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun PastAnnouncementsSection(
    announcements: List<Announcement>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Past Announcements",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            announcements.forEach { announcement ->
                AnnouncementItem(announcement = announcement)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun AnnouncementItem(announcement: Announcement) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.5f),
        onClick = { /* Navigate to detail */ }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = announcement.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = announcement.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = announcement.title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun ActionButtons(
    onAddAnnouncement: () -> Unit, onManageData: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Add Announcement Button
        Button(
            onClick = onAddAnnouncement,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp, pressedElevation = 8.dp
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Add New Announcement",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // Manage Data Button
        OutlinedButton(
            onClick = onManageData,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            ),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Storage,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Manage Masjid Data",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

