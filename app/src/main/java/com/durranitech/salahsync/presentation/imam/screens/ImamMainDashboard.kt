package com.durranitech.salahsync.presentation.imam.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mosque
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
import com.durranitech.salahsync.presentation.imam.components.AddUpdateSalahTimingsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImamMainDashboard(
    userName: String, userEmail: String, onSignOut: () -> Unit = {}, paddingValue: PaddingValues,onCreateMasjid: () -> Unit = {}
) {
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
            0 -> ImamHomeScreen(paddingValues, onNavigateToSalahTimes = {selectedIndex = 4})
            1 -> AnnouncementsScreen()
            2 -> MasjidScreen(onCreateMasjid = onCreateMasjid)
            3 -> MembersScreen()
            4 -> AddUpdateSalahTimingsScreen(onNavigateBackToHomeScreen = {selectedIndex = 0})
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
