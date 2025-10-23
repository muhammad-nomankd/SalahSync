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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PrayerTime(
    val name: String, val time: String, val isNext: Boolean = false
)

data class Announcement(
    val id: String, val title: String, val description: String, val timestamp: Long
)

data class Activity(
    val id: String, val title: String, val description: String, val daysAgo: Int
)

data class HomeUiState(
    val userName: String = "Noman",
    val nextPrayer: PrayerTime? = null,
    val timeUntilPrayer: TimeRemaining = TimeRemaining(1, 23, 45),
    val announcements: List<Announcement> = emptyList(),
    val recentActivities: List<Activity> = emptyList(),
    val isLoading: Boolean = false
)

data class TimeRemaining(
    val hours: Int, val minutes: Int, val seconds: Int
) {
    fun formatted(): String = "${hours.toString().padStart(2, '0')}:${
        minutes.toString().padStart(2, '0')
    }:${seconds.toString().padStart(2, '0')}"
}


class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
        startCountdownTimer()
    }

    private fun loadInitialData() {
        _uiState.update { currentState ->
            currentState.copy(
                nextPrayer = PrayerTime("Dhuhr", "1:30 PM", true), announcements = listOf(
                    Announcement(
                        id = "1",
                        title = "Community Iftar this Weekend",
                        description = "Join us for a community iftar this Saturday after Maghrib prayer. All are welcome.",
                        timestamp = System.currentTimeMillis()
                    )
                ), recentActivities = listOf(
                    Activity(
                        id = "1",
                        title = "Recent Donation",
                        description = "JazakAllah Khair for your generous contributions. May Allah accept it from you.",
                        daysAgo = 2
                    )
                )
            )
        }
    }

    private fun startCountdownTimer() {
        viewModelScope.launch {
            while (true) {
                delay(1000L)
                _uiState.update { currentState ->
                    val current = currentState.timeUntilPrayer
                    val newTime = when {
                        current.seconds > 0 -> TimeRemaining(
                            current.hours, current.minutes, current.seconds - 1
                        )

                        current.minutes > 0 -> TimeRemaining(
                            current.hours, current.minutes - 1, 59
                        )

                        current.hours > 0 -> TimeRemaining(
                            current.hours - 1, 59, 59
                        )

                        else -> TimeRemaining(0, 0, 0)
                    }
                    currentState.copy(timeUntilPrayer = newTime)
                }
            }
        }
    }
}

