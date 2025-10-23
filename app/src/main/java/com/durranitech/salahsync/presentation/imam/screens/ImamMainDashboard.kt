package com.durranitech.salahsync.presentation.imam.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Mosque
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.durranitech.salahsync.presentation.imam.BottomNavigationItem
import com.durranitech.salahsync.presentation.muqtadi.screens.PrayerTimesCard
import com.durranitech.salahsync.presentation.muqtadi.screens.QuickActionsCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImamMainDashboard(
    userName: String, userEmail: String, onSignOut: () -> Unit = {}, paddingValue: PaddingValues
) {
    val scrollState = rememberScrollState()
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
    val items = listOf(
        BottomNavigationItem(
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        hasNews = false,
        badgeCount = null,
        onNavigate = {}), BottomNavigationItem(
        title = "Announcements",
        selectedIcon = Icons.Filled.Campaign,
        unselectedIcon = Icons.Outlined.Campaign,
        hasNews = true,
        badgeCount = 2,
        onNavigate = {}), BottomNavigationItem(
        title = "Masjid",
        selectedIcon = Icons.Filled.Mosque,
        unselectedIcon = Icons.Outlined.Mosque,
        hasNews = false,
        badgeCount = null,
        onNavigate = {}), BottomNavigationItem(
        title = "Members",
        selectedIcon = Icons.Filled.Group,
        unselectedIcon = Icons.Outlined.Group,
        hasNews = true,
        badgeCount = null,
        onNavigate = {}))
    Scaffold(
        bottomBar = {
            NavigationBar(
                windowInsets = NavigationBarDefaults.windowInsets,
                tonalElevation = 16.dp,
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            ) {

                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedIndex == index, onClick = {
                        selectedIndex = index
                    }, icon = {
                        BadgedBox(badge = {
                            if (item.badgeCount != null) {
                                Badge {
                                    Text(item.badgeCount.toString())
                                }
                            } else if (item.hasNews) {
                                Badge()
                            }
                        }) {
                            Icon(
                                imageVector = if (index == selectedIndex) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title,
                                modifier = Modifier.size(if (index == selectedIndex) 28.dp else 24.dp)
                            )
                        }


                    }, alwaysShowLabel = true, colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                    )

                }
            }
        }, containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
    ) { paddingValues ->
        when (selectedIndex) {
            0 -> ImamHomeScreen()
            1 -> AnnouncementsScreen()
            2 -> MasjidDetailsScreen()

            3 -> MembersScreen()
        }

    }


}


@Composable
fun DashboardStat(icon: ImageVector, title: String, value: String) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = Color(0xFF10B981),
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(title, color = Color(0xFF059669), fontSize = 14.sp)
                Text(value, color = Color(0xFF065F46), fontSize = 16.sp)
            }
        }
    }
}


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

@Composable
fun RecentActivityCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Recent Activity",
                color = Color(0xFF065F46),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(12.dp))

            val activities = listOf(
                Triple("Prayer time updated for Maghrib", "2 hours ago", Color(0xFF10B981)),
                Triple("New member registered: Ali Rahman", "5 hours ago", Color(0xFF3B82F6)),
                Triple("Announcement sent to 127 members", "1 day ago", Color(0xFF8B5CF6))
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                activities.forEach { (title, time, color) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(color, CircleShape)
                                .align(Alignment.CenterVertically)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(title, color = color.darken(), fontSize = 14.sp)
                            Text(time, color = color.copy(alpha = 0.7f), fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

fun Color.darken(factor: Float = 0.8f): Color {
    return Color(
        red = red * factor, green = green * factor, blue = blue * factor, alpha = alpha
    )
}

@Composable
fun PendingTasksCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Pending Tasks",
                color = Color(0xFF065F46),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(12.dp))

            val tasks = listOf(
                Triple("Update Ramadan schedule", "High", Color(0xFFFACC15)),
                Triple("Review member applications (3)", "Medium", Color(0xFF3B82F6)),
                Triple("Prepare Friday khutbah", "Normal", Color(0xFF9CA3AF))
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                tasks.forEach { (task, priority, color) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = false, onCheckedChange = {})
                            Spacer(Modifier.width(8.dp))
                            Text(task, color = Color(0xFF374151), fontSize = 14.sp)
                        }
                        Box(
                            modifier = Modifier
                                .background(
                                    color.copy(alpha = 0.2f), RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(priority, color = color.darken(), fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}


// Color constants (picked to match the screenshot)
private val GradientLeft = Color(0xFFE9F7FF)     // pale blue
private val GradientRight = Color(0xFFF5EAFB)    // pale pink
private val BorderColor = Color(0xFFCEE8FF)      // light border blue
private val IconAndTitleBlue = Color(0xFF2563EB) // main blue for icon & title
private val QuoteBlue = Color(0xFF2B63D6)        // quote text blue
private val AuthorBg = Color(0xFF1E40AF)         // dark blue bar behind author
private val AuthorText = Color.White

@Composable
fun QuoteOfTheDayCard(
    quote: String, author: String, iconVectorRes: ImageVector
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(GradientLeft, GradientRight)
                    )
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // top icon
                Icon(
                    imageVector = iconVectorRes,
                    contentDescription = "Quote icon",
                    tint = IconAndTitleBlue,
                    modifier = Modifier.size(36.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Title
                Text(
                    text = "Quote of the Day",
                    color = IconAndTitleBlue,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Quote text (italic centered)
                Text(
                    text = "“$quote”",
                    color = QuoteBlue,
                    fontSize = 18.sp,
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Author highlighted as a pill
                Box(
                    modifier = Modifier
                        .wrapContentWidth()
                        .heightIn(min = 28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(AuthorBg)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = author,
                        color = AuthorText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

}


/*
@Preview
@Composable
private fun ImamDashboardPreview() {
    MaterialTheme {
        ImamDashboardScreen(
            userName = "Imam Ahmed",
            userEmail = "imam@example.com",
            onSignOut = { println("Sign out clicked") },
            paddingValue = PaddingValues(16.dp)
        )
    }
}

*/
