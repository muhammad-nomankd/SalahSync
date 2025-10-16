package com.durranitech.salahsync.presentation.imam.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImamDashboardScreen(
    userName: String, userEmail: String, onSignOut: () -> Unit = {}, paddingValues: PaddingValues
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(Brush.linearGradient(listOf(Color(0xFFE6F4EA), Color(0xFFD0F0E0))))
    ) {
        /*  // Header
          TopAppBar(
              title = {
                  Column {
                      Text("SalahSync", color = Color(0xFF065F46), fontSize = 20.sp)
                      Text("Imam Dashboard", color = Color(0xFF047857), fontSize = 14.sp)
                  }
              },
              navigationIcon = {
                  Icon(
                      imageVector = Icons.Default.Star,
                      contentDescription = null,
                      tint = Color(0xFF047857),
                      modifier = Modifier.padding(start = 8.dp)
                  )
              },
              actions = {
                  Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(end = 8.dp)) {
                      Text(userName, color = Color(0xFF065F46), fontSize = 14.sp)
                      Text("Imam", color = Color(0xFF047857), fontSize = 12.sp)
                  }
                  IconButton(onClick = onSignOut) {
                      Icon(Icons.Default.ExitToApp, contentDescription = "Sign Out", tint = Color(0xFF047857))
                  }
              },
              colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White.copy(alpha = 0.9f))
          )

          Spacer(Modifier.height(16.dp))*/

        // Greeting
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
                    icon = Icons.Default.AccessTime, title = "Next Prayer", value = "Asr - 3:45 PM"
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
                    icon = Icons.Default.Notifications, title = "Pending Notifications", value = "3"
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
            "- Prophet Muhammad (ﷺ), Sahih al-Bukhari"
        )

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
                Triple(Icons.Default.CalendarToday, "Manage Schedule", Color(0xFF10B981)),
                Triple(Icons.Default.Notifications, "Send Announcement", Color(0xFF3B82F6)),
                Triple(Icons.Default.Group, "View Members", Color(0xFF8B5CF6)),
                Triple(Icons.Default.Settings, "Settings", Color(0xFFF97316))
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
    quote: String, author: String,
    // optional: pass custom icon vector resource id (defaults to R.drawable.ic_book_open if available)
    iconVectorRes: ImageVector = Icons.Default.MenuBook // replace with your drawable; fallback needed in your project
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
        // Gradient background box (keeps border from Card)
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


@Preview
@Composable
private fun ImamDashboardPreview() {
    MaterialTheme {
        ImamDashboardScreen(
            userName = "Imam Ahmed",
            userEmail = "imam@example.com",
            onSignOut = { println("Sign out clicked") },
            paddingValues = PaddingValues(16.dp)
        )
    }
}

