package com.durranitech.salahsync.presentation.muqtadi.screens

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.durranitech.salahsync.presentation.authentication.viewModel.AuthViewModel
import com.durranitech.salahsync.presentation.imam.screens.PrayerTimesCard

@Composable
fun MuqtadiDashboard(
    userName: String = "Brother", onSignOut: () -> Unit = {}, toRoleSelectionScreen: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFE8F0FE), Color(0xFFE0E7FF))
    )



    Scaffold(topBar = {

    }, floatingActionButton = {
        IconButton(onClick = {viewModel.signOut()
            if(viewModel.state.value.isUserAuthenticated == false){
                toRoleSelectionScreen()
            }else{
                viewModel.signOut()
            }

        }
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = "logout",
                Modifier.size(48.dp), tint = Color.Red)
        }
    }, content = { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(innerPadding)
                .padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier.heightIn(max = 350.dp),
                    horizontalArrangement = Arrangement.spacedBy(
                        8.dp
                    )
                ) {
                    item {
                        StatCard(
                            Icons.Default.DateRange,
                            "Today's Date",
                            "15 Rajab 1446",
                            Color(0xFF6366F1)
                        )
                    }
                    item {
                        StatCard(
                            Icons.Default.Schedule,
                            "Next Prayer",
                            "Asr - 3:45 PM",
                            Color(0xFF3B82F6)
                        )

                    }
                }
            }
            item {
                PrayerTimesCard()
            }
            item {
                QuickActionsCardMuqtadi()
            }
            item {
                AnnouncementsSection()
            }
            item {
                EventsSection()
            }
            item {
                QuoteSection()
            }
        }
    })
}

// ---------------- Header ----------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuqtadiHeader(userName: String, onSignOut: () -> Unit) {
    TopAppBar(title = {
        Column {
            Text("SalahSync", color = Color(0xFF1E40AF), fontWeight = FontWeight.Bold)
            Text("Muqtadi Dashboard", fontSize = 12.sp, color = Color(0xFF2563EB))
        }
    }, actions = {
        Column(horizontalAlignment = Alignment.End) {
            Text(userName, color = Color(0xFF1E40AF), fontSize = 14.sp)
            Text("Muqtadi", color = Color(0xFF2563EB), fontSize = 12.sp)
        }
        IconButton(onClick = onSignOut) {
            Icon(
                Icons.Default.Logout, contentDescription = "Sign Out", tint = Color(0xFF2563EB)
            )
        }
    })
}

@Composable
fun QuickActionsCardMuqtadi() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Quick Actions",
                color = Color(0xFF065F46),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(12.dp))

            val actions = listOf(
                Triple(Icons.Default.CalendarToday, "Prayer Reminder", Color(0xFF10B981)),
                Triple(Icons.Default.Notifications, "Announcement", Color(0xFF3B82F6)),
                Triple(Icons.Default.Group, "Event Calender", Color(0xFF8B5CF6)),
                Triple(Icons.Default.Person, "My Profile", Color(0xFFF97316))
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.heightIn(max = 250.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(actions.size) { i ->
                    val (icon, title, tint) = actions[i]
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(tint.copy(alpha = 0.1f))
                            .border(1.dp, tint.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                            .clickable { /* Hand
                            le click */
                            }
                            .padding(vertical = 16.dp, horizontal = 8.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                icon,
                                contentDescription = title,
                                tint = tint,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                title, color = tint, fontSize = 14.sp, textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}


// ---------------- Greeting ----------------
@Composable
fun GreetingSection(userName: String) {
    Column {
        Text(
            text = "Assalamu Alaikum, $userName 👋",
            color = Color(0xFF1E3A8A),
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Welcome to your Muqtadi dashboard. Stay connected with your mosque community.",
            color = Color(0xFF2563EB),
            fontSize = 14.sp
        )
    }
}

// ---------------- Quick Stats ----------------
@Composable
fun QuickStatsSection() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        StatCard(Icons.Default.Schedule, "Next Prayer", "Asr - 3:45 PM", Color(0xFF3B82F6))
        Spacer(modifier = Modifier.height(12.dp))
        StatCard(Icons.Default.DateRange, "Today's Date", "15 Rajab 1446", Color(0xFF6366F1))
    }
}

@Composable
fun StatCard(
    icon: ImageVector, label: String, value: String, tint: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, tint.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(label, color = Color(0xFF2563EB), fontSize = 12.sp)
                Text(
                    value,
                    color = Color(0xFF1E3A8A),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// ---------------- Prayer Times ----------------
@Composable
fun PrayerTimesCard() {
    val prayers = listOf(
        Triple("Fajr", "5:30 AM", "5:45 AM"),
        Triple("Dhuhr", "12:15 PM", "12:30 PM"),
        Triple("Asr", "3:45 PM", "4:00 PM"),
        Triple("Maghrib", "6:20 PM", "6:25 PM"),
        Triple("Isha", "7:45 PM", "8:00 PM")
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()
            ) {
                Text("Today's Prayer Times", color = Color(0xFF065F46), fontSize = 16.sp)
                TextButton(onClick = {}) { Text("Edit Times", color = Color(0xFF047857)) }
            }

            prayers.forEachIndexed { index, (name, time, jamaah) ->
                val bgColor = when (index) {
                    2 -> Color(0xFFE6F4EA)
                    else -> Color(0xFFF9FAFB)
                }
                Box(
                    Modifier
                        .fillMaxWidth()
                        .background(bgColor, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(name, color = Color(0xFF065F46), fontSize = 14.sp)
                            Text("Jamaah: $jamaah", color = Color.Gray, fontSize = 12.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(time, color = Color(0xFF047857), fontSize = 14.sp)
                            if (index == 2) Text(
                                "Next Prayer", color = Color(0xFF10B981), fontSize = 10.sp
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

// ---------------- Quick Actions ----------------
@Composable
fun QuickActionsSection() {
    Column {
        Text("Quick Actions", color = Color(0xFF1E3A8A), fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))
        val actions = listOf(
            Pair(Icons.Default.Notifications, "Announcements"),
            Pair(Icons.Default.Event, "Events Calendar"),
            Pair(Icons.Default.Settings, "My Profile"),
            Pair(Icons.Default.Alarm, "Prayer Reminders")
        )
        Column {
            actions.chunked(2).forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    row.forEach { (icon, title) ->
                        QuickActionCard(icon, title)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun QuickActionCard(icon: ImageVector, title: String) {
    Card(
        modifier = Modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F9FF)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = Color(0xFF2563EB),
                modifier = Modifier.size(32.dp)
            )
            Text(title, color = Color(0xFF1E3A8A), fontSize = 13.sp)
        }
    }
}

// ---------------- Announcements ----------------
@Composable
fun AnnouncementsSection() {
    Column {
        Text("Recent Announcements", color = Color(0xFF1E3A8A), fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))
        AnnouncementCard(
            "Ramadan Schedule Updated",
            "The Ramadan prayer schedule has been updated. Taraweeh will start at 8:30 PM.",
            "2 hours ago"
        )
        AnnouncementCard(
            "Friday Khutbah Topic",
            "This Friday's khutbah will discuss the importance of community unity.",
            "1 day ago"
        )
        AnnouncementCard(
            "Quran Study Circle",
            "Join us every Saturday after Maghrib for Quran study and discussion.",
            "3 days ago"
        )
    }
}

@Composable
fun AnnouncementCard(title: String, detail: String, time: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, color = Color(0xFF1E3A8A), fontWeight = FontWeight.Medium)
            Text(detail, color = Color(0xFF2563EB), fontSize = 12.sp)
            Text(time, color = Color.Gray, fontSize = 11.sp)
        }
    }
}

// ---------------- Events ----------------
@Composable
fun EventsSection() {
    Column {
        Text("Upcoming Events", color = Color(0xFF1E3A8A), fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))
        EventCard(
            "Jummah Prayer", "Friday", "1:00 PM - 2:00 PM", "Main Prayer Hall", Color(0xFF3B82F6)
        )
        EventCard(
            "Quran Study Circle", "Saturday", "After Maghrib", "Study Room", Color(0xFF8B5CF6)
        )
        EventCard(
            "Community Iftar",
            "Next Week",
            "6:20 PM (Maghrib Time)",
            "Community Hall",
            Color(0xFF10B981)
        )
    }
}

@Composable
fun EventCard(title: String, day: String, time: String, location: String, color: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(title, color = color.darken(0.3f), fontWeight = FontWeight.Medium)
                Text(day, color = color, fontSize = 12.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(time, color = color, fontSize = 12.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Place,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(location, color = color, fontSize = 12.sp)
            }
        }
    }
}

// ---------------- Quote ----------------
@Composable
fun QuoteSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E7FF)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.MenuBook,
                contentDescription = null,
                tint = Color(0xFF2563EB),
                modifier = Modifier.size(40.dp)
            )
            Text("Quote of the Day", color = Color(0xFF1E3A8A), fontWeight = FontWeight.SemiBold)
            Text(
                "\"The best among you are those who have the best manners and character.\"",
                textAlign = TextAlign.Center,
                color = Color(0xFF2563EB),
                fontStyle = FontStyle.Italic
            )
            Text(
                "- Prophet Muhammad (ﷺ), Sahih al-Bukhari",
                fontSize = 12.sp,
                color = Color(0xFF1E40AF)
            )
        }
    }
}

@Composable
fun QuickActionsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Quick Actions",
                color = Color(0xFF065F46),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(12.dp))

            val actions = listOf(
                Triple(Icons.Default.CalendarToday, "Prayer Reminder", Color(0xFF10B981)),
                Triple(Icons.Default.Notifications, "Announcement", Color(0xFF3B82F6)),
                Triple(Icons.Default.Group, "Events Calender", Color(0xFF8B5CF6)),
                Triple(Icons.Default.Settings, "My Profile", Color(0xFFF97316))
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.heightIn(max = 250.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(actions.size) { i ->
                    val (icon, title, tint) = actions[i]
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(tint.copy(alpha = 0.1f))
                            .border(1.dp, tint.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                            .clickable { /* Handle click */ }
                            .padding(vertical = 16.dp, horizontal = 8.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                icon,
                                contentDescription = title,
                                tint = tint,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                title,
                                color = tint,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

// Small color darken helper
fun Color.darken(factor: Float): Color {
    return Color(red * (1 - factor), green * (1 - factor), blue * (1 - factor))
}


@Preview
@Composable
private fun MuqtadiDashbaordPrev() {
    MuqtadiDashboard("Noman", toRoleSelectionScreen = {})

}