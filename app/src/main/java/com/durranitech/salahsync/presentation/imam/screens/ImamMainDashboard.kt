package com.durranitech.salahsync.presentation.imam.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged

private val GreenPrimary = Color(0xFF1B8C5E)
private val GreenSurface = Color(0xFFE8F5EF)

private data class NavItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeCount: Int? = null,
    val hasDot: Boolean = false
)

private val navItems = listOf(
    NavItem("Home",          Icons.Filled.Home,      Icons.Outlined.Home),
    NavItem("Announcements", Icons.Filled.Campaign,  Icons.Outlined.Campaign, hasDot = true),
    NavItem("Masjid",        Icons.Filled.Mosque,    Icons.Outlined.Mosque),
    NavItem("Members",       Icons.Filled.Group,     Icons.Outlined.Group, hasDot = true)
)

@OptIn(
    ExperimentalMaterial3Api::class,
    androidx.compose.foundation.ExperimentalFoundationApi::class
)
@Composable
fun ImamMainDashboard(
    userName: String = "",
    userEmail: String = "",
    onLogOut: () -> Unit = {},
    paddingValue: PaddingValues = PaddingValues(),
    onCreateMasjid: () -> Unit = {},
    onCreateNewAnnouncement: () -> Unit = {},
    onNavigateToUpdateSalahTimes: () -> Unit = {},
    // ── FIX: add this parameter so MainRoute can wire navigation into the pager ──
    onShowQrCode: () -> Unit = {}
) {
    val pagerState = rememberPagerState(pageCount = { navItems.size })
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }

    // Pager swipe → update bottom nav only when page fully settles
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }
            .distinctUntilChanged()
            .collect { settledPage ->
                selectedIndex = settledPage
            }
    }

    // Bottom nav tap → instant jump (no animation, no feedback loop)
    LaunchedEffect(selectedIndex) {
        if (pagerState.currentPage != selectedIndex) {
            pagerState.scrollToPage(selectedIndex)
        }
    }

    Scaffold(
        bottomBar = {
            ImamBottomBar(
                selectedIndex = selectedIndex,
                onItemSelected = { index -> selectedIndex = index }
            )
        },
        contentWindowInsets = WindowInsets(0),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            userScrollEnabled = true,
            beyondViewportPageCount = 1
        ) { page ->
            when (page) {
                0 -> ImamHomeScreen(
                    paddingValues = PaddingValues(),
                    onNavigateToUpdateSalahTimes = onNavigateToUpdateSalahTimes,
                    onCreateNewAnnouncement = onCreateNewAnnouncement
                )
                1 -> AnnouncementsScreen()
                2 -> MasjidScreen(
                    onCreateMasjid = onCreateMasjid,
                    onLogOut = onLogOut,
                    onShowQrCode = onShowQrCode
                )
                3 -> MembersScreen()
            }
        }
    }
}

@Composable
private fun ImamBottomBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        windowInsets = WindowInsets.navigationBars
    ) {
        navItems.forEachIndexed { index, item ->
            val isSelected = selectedIndex == index

            val iconScale by animateFloatAsState(
                targetValue = if (isSelected) 1.15f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "icon_scale_$index"
            )

            NavigationBarItem(
                selected = isSelected,
                onClick = { onItemSelected(index) },
                icon = {
                    BadgedBox(badge = {
                        when {
                            item.badgeCount != null -> Badge { Text(item.badgeCount.toString()) }
                            item.hasDot -> Badge(containerColor = GreenPrimary)
                        }
                    }) {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.title,
                            modifier = Modifier
                                .size(24.dp)
                                .scale(iconScale)
                        )
                    }
                },
                label = { Text(item.title, style = MaterialTheme.typography.labelSmall) },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = GreenPrimary,
                    selectedTextColor = GreenPrimary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
                    indicatorColor = GreenSurface
                )
            )
        }
    }
}