package com.durranitech.salahsync.presentation.imam.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ImamHomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onNavigateToSalahTimes: () -> Unit = {},
    onNavigateToAnnouncements: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {/* Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(Brush.linearGradient(listOf(Color(0xFFE6F4EA), Color(0xFFD0F0E0))))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Assalamu Alaikum, ${userName.split(" ").firstOrNull() ?: "Brother"}",
                fontSize = 22.sp,
                color = Color(0xFF065F46)
            )
            Text(
                "Welcome to your Imam dashboard. Manage your mosque's prayer times and community.",
                fontSize = 14.sp,
                color = Color(0xFF047857)
            )
        }

        Spacer(Modifier.height(16.dp))

        // Quick Stats
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.heightIn(max = 350.dp)
        ) {
            item {
                DashboardStat(
                    icon = Icons.Default.AccessTime,
                    title = "Next Prayer",
                    value = "Asr - 3:45 PM"
                )
            }
            item {
                DashboardStat(
                    icon = Icons.Default.CalendarToday,
                    title = "Today's Date",
                    value = "15 Rajab 1446"
                )
            }
            item {
                DashboardStat(
                    icon = Icons.Default.People, title = "Registered Members", value = "127"
                )
            }
            item {
                DashboardStat(
                    icon = Icons.Default.Notifications,
                    title = "Pending Notifications",
                    value = "3"
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Main Features
        Column(modifier = Modifier.padding(16.dp)) {
            PrayerTimesCard()
            Spacer(Modifier.height(16.dp))
            QuickActionsCard()
        }

        Spacer(Modifier.height(16.dp))

        // Bottom: Recent Activity and Pending Tasks
        Column(modifier = Modifier.padding(16.dp)) {
            RecentActivityCard()
            Spacer(Modifier.height(16.dp))
            PendingTasksCard()
        }
        QuoteOfTheDayCard(
            "The best among you are those who have the best manners and characte",
            "- Prophet Muhammad (ﷺ), Sahih al-Bukhari",
            iconVectorRes = Icons.AutoMirrored.Default.MenuBook
        )

    }*/

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold { paddingValues ->
        HomeContent(
            uiState = uiState,
            onViewAllSalahTimes = onNavigateToSalahTimes,
            onViewAllAnnouncements = onNavigateToAnnouncements,
            onViewAllActivities = {},
            modifier = Modifier.padding(paddingValues)
        )
    }
}
@Composable
fun HomeContent(
    uiState: HomeUiState,
    onViewAllSalahTimes: () -> Unit,
    onViewAllAnnouncements: () -> Unit,
    onViewAllActivities: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Prayer Time Card
        uiState.nextPrayer?.let { prayer ->
            NextPrayerCard(
                prayer = prayer,
                timeRemaining = uiState.timeUntilPrayer,
                onViewAllClick = onViewAllSalahTimes
            )
        }

        // Announcements
        AnnouncementsCard(
            announcements = uiState.announcements, onViewAllClick = onViewAllAnnouncements
        )


        Spacer(modifier = Modifier.height(16.dp))
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(userName: String) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "Assalamu Alaikum,",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = " 👋", style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
        }, actions = {
            IconButton(onClick = { /* Handle notification click */ }) {
                Icon(
                    imageVector = Icons.Outlined.Notifications, contentDescription = "Notifications"
                )
            }
        }, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun NextPrayerCard(
    prayer: PrayerTime, timeRemaining: TimeRemaining, onViewAllClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, MaterialTheme.shapes.extraLarge),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Prayer Info
                Column {
                    Text(
                        text = "NEXT PRAYER",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = prayer.name, style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = MaterialTheme.typography.displayLarge.fontSize * 0.85f
                        ), fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = prayer.time,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium
                    )
                }

                // Countdown
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "COUNTDOWN",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "-${timeRemaining.formatted()}",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onViewAllClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "View All Salah Times",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun AnnouncementsCard(
    announcements: List<Announcement>, onViewAllClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, MaterialTheme.shapes.extraLarge),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Masjid Announcements",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onViewAllClick) {
                    Text(
                        text = "View All",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            announcements.forEach { announcement ->
                Column {
                    Text(
                        text = announcement.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = announcement.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}
@Composable
fun RowScope.BottomNavItem(
    icon: ImageVector, label: String, selected: Boolean, onClick: () -> Unit
) {
    NavigationBarItem(
        icon = {
            Icon(
                imageVector = icon, contentDescription = label, modifier = Modifier.size(24.dp)
            )
        }, label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
        }, selected = selected, onClick = onClick, colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
            unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    )
}


