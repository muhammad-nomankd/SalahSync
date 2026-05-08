package com.durranitech.salahsync.presentation.imam.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Tag
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.durranitech.salahsync.domain.model.Masjid
import com.durranitech.salahsync.domain.model.UserRole
import com.durranitech.salahsync.presentation.authentication.viewModel.AuthViewModel
import com.durranitech.salahsync.presentation.imam.UiState
import com.durranitech.salahsync.presentation.imam.components.EmptyMasjidPlaceholder
import com.durranitech.salahsync.presentation.imam.viewmodel.ImamViewModel
import com.durranitech.salahsync.ui.theme.SalahSyncTheme

// ─── Colors ───────────────────────────────────────────────────────────────────
private val GreenPrimary = Color(0xFF1B8C5E)
private val GreenLight = Color(0xFF26A874)
private val GreenSurface = Color(0xFFE8F5EF)
private val CodeSurface = Color(0xFFF0FAF5)
private val LogoutRed = Color(0xFFE53935)

// ─── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun MasjidScreen(
    modifier: Modifier = Modifier,
    onEditDetails: () -> Unit = {},
    onViewAllMembers: () -> Unit = {},
    onAddAnnouncement: () -> Unit = {},
    onManageData: () -> Unit = {},
    onCreateMasjid: () -> Unit = {},
    onLogOut: () -> Unit = {},
    viewModel: ImamViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    MasjidScreenContent(
        state = state,
        modifier = modifier,
        onEditDetails = onEditDetails,
        onViewAllMembers = onViewAllMembers,
        onAddAnnouncement = onAddAnnouncement,
        onManageData = onManageData,
        onCreateMasjid = onCreateMasjid,
        onLogOut = onLogOut
    )
}

// ─── Content Router ───────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MasjidScreenContent(
    state: UiState,
    modifier: Modifier = Modifier,
    onEditDetails: () -> Unit = {},
    onViewAllMembers: () -> Unit = {},
    onAddAnnouncement: () -> Unit = {},
    onManageData: () -> Unit = {},
    onCreateMasjid: () -> Unit = {},
    onLogOut: () -> Unit = {},
    authViewModel: AuthViewModel = hiltViewModel()

) {
    val hasMasjid = state.masjid != null &&
            state.masjid.imamName.isNotEmpty() &&
            state.masjid.imamId.isNotEmpty()

    LaunchedEffect(Unit) {
        authViewModel.getCurrentUserId()
    }
    val authState = authViewModel.state.collectAsStateWithLifecycle()
    val userId = authState.value.userId
    val userRole = authState.value.role
    val userMasjid = authState.value.masjidId

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "My Masjid",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        if (hasMasjid) {
                            Text(
                                text = state.masjid.name.ifBlank { "Manage your masjid" },
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                actions = {
                    if (hasMasjid) {
                        IconButton(onClick = onLogOut) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(LogoutRed.copy(alpha = 0.1f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.Logout,
                                    contentDescription = "Logout",
                                    tint = LogoutRed,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
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

        // ✅ Loading check first — don't flash empty state while fetching
        AnimatedContent(
            targetState = when {
                state.isLoading -> "loading"
                hasMasjid -> "content"
                userRole == UserRole.MUQTADI || userRole != null || userId != null || userMasjid != null -> "muqtadi_content"
                else -> "empty"
            },
            transitionSpec = {
                (fadeIn(tween(350)) + slideInVertically(tween(350)) { it / 16 }) togetherWith
                        fadeOut(tween(200))
            },
            label = "masjid_screen_state"
        ) { screenState ->
            when (screenState) {
                "loading" -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(
                                color = GreenPrimary,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = "Loading masjid details…",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                }

                "content" -> {
                    val masjid = state.masjid!!
                    MasjidDetailsContent(
                        masjidName = masjid.name,
                        masjidAddress = masjid.address ?: "",
                        imamName = masjid.imamName,
                        masjidCode = masjid.code ?: "",
                        innerPadding = innerPadding,
                        onEditDetails = onEditDetails
                    )
                }

                else -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        EmptyMasjidPlaceholder(onCreateMasjid = onCreateMasjid)
                    }
                }
            }
        }
    }
}

// ─── Details Content ──────────────────────────────────────────────────────────

@Composable
private fun MasjidDetailsContent(
    masjidName: String,
    masjidAddress: String,
    imamName: String,
    masjidCode: String,
    innerPadding: PaddingValues,
    onEditDetails: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ── Hero Banner ──────────────────────────────────────────────────────
        item {
            MasjidHeroBanner(masjidName = masjidName)
        }

        // ── Info Cards ───────────────────────────────────────────────────────
        item {
            InfoSection(
                masjidAddress = masjidAddress,
                imamName = imamName,
                masjidCode = masjidCode,
                onCopyCode = {
                    clipboardManager.setText(AnnotatedString(masjidCode))
                    Toast.makeText(context, "Code copied!", Toast.LENGTH_SHORT).show()
                }
            )
        }

        // ── Edit Button ──────────────────────────────────────────────────────
        item {
            Button(
                onClick = onEditDetails,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) {
                Icon(
                    imageVector = Icons.Outlined.EditNote,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Edit Masjid Details",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

// ─── Hero Banner ──────────────────────────────────────────────────────────────

@Composable
private fun MasjidHeroBanner(masjidName: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(listOf(GreenPrimary, GreenLight, Color(0xFF3FC28A)))
            )
    ) {
        // Decorative circles
        Box(
            modifier = Modifier
                .size(180.dp)
                .align(Alignment.TopEnd)
                .background(Color.White.copy(alpha = 0.06f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.BottomStart)
                .background(Color.White.copy(alpha = 0.06f), CircleShape)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "🕌",
                fontSize = 32.sp
            )
            Text(
                text = masjidName.ifBlank { "Your Masjid" },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ─── Info Section ─────────────────────────────────────────────────────────────

@Composable
private fun InfoSection(
    masjidAddress: String,
    imamName: String,
    masjidCode: String,
    onCopyCode: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            InfoRow(
                icon = Icons.Outlined.LocationOn,
                iconTint = GreenPrimary,
                label = "Address",
                value = masjidAddress.ifBlank { "No address set" }
            )

            RowDivider()

            InfoRow(
                icon = Icons.Outlined.Person,
                iconTint = Color(0xFF5C6BC0),
                label = "Imam",
                value = imamName.ifBlank { "Not set" }
            )

            RowDivider()

            // Code row with copy action
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            Color(0xFFFFA726).copy(alpha = 0.12f),
                            RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Tag,
                        contentDescription = null,
                        tint = Color(0xFFFFA726),
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Masjid Code",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Text(
                        text = masjidCode.ifBlank { "—" },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GreenPrimary,
                        letterSpacing = 2.sp
                    )
                }

                // Copy button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(GreenSurface, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    var copied by remember { mutableStateOf(false) }

                    IconButton(
                        onClick = {
                            onCopyCode()
                            copied = true
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ContentCopy,
                            contentDescription = "Copy code",
                            tint = GreenPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

// ─── Reusable Row ─────────────────────────────────────────────────────────────

@Composable
private fun InfoRow(
    icon: ImageVector,
    iconTint: Color,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(iconTint.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun RowDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
    )
}

// ─── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "Masjid Details")
@Composable
fun MasjidScreenPreview() {
    SalahSyncTheme {
        MasjidScreenContent(
            state = UiState(
                masjid = Masjid(
                    name = "Masjid Al-Noor",
                    address = "123 Islamic Way, Cairo, Egypt",
                    imamName = "Sheikh Abdullah",
                    imamId = "imam_001",
                    code = "NR-4821"
                )
            )
        )
    }
}

@Preview(showBackground = true, name = "Loading")
@Composable
fun MasjidScreenLoadingPreview() {
    SalahSyncTheme {
        MasjidScreenContent(state = UiState(isLoading = true))
    }
}

@Preview(showBackground = true, name = "Empty")
@Composable
fun MasjidScreenEmptyPreview() {
    SalahSyncTheme {
        MasjidScreenContent(state = UiState(masjid = null, isLoading = false))
    }
}