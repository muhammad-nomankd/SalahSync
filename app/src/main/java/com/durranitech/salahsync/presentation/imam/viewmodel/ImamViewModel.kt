package com.durranitech.salahsync.presentation.imam.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.durranitech.salahsync.domain.model.Announcement
import com.durranitech.salahsync.domain.model.Masjid
import com.durranitech.salahsync.domain.model.SalahTime
import com.durranitech.salahsync.domain.usecase.AddAnnouncementUseCase
import com.durranitech.salahsync.domain.usecase.AddOrUpdateSalahTimesUseCase
import com.durranitech.salahsync.domain.usecase.CreateMasjidUseCase
import com.durranitech.salahsync.domain.usecase.GetAnnouncementsUseCase
import com.durranitech.salahsync.domain.usecase.GetMasjidDetailsUseCase
import com.durranitech.salahsync.domain.usecase.GetMasjidUseCase
import com.durranitech.salahsync.domain.usecase.GetUpcommignPrayerTimeUseCase
import com.durranitech.salahsync.presentation.imam.ImamIntent
import com.durranitech.salahsync.presentation.imam.ImamUiState
import com.durranitech.salahsync.util.Resource
import com.durranitech.salahsync.util.getNextPrayerAndCountdown
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImamViewModel @Inject constructor(
    private val createMasjidUseCase: CreateMasjidUseCase,
    private val getMasjidUseCase: GetMasjidUseCase,
    private val getMasjidDetailsUseCase: GetMasjidDetailsUseCase,
    private val addOrUpdateSalahTimesUseCase: AddOrUpdateSalahTimesUseCase,
    private val getSalahTimesUseCase: GetUpcommignPrayerTimeUseCase,
    private val addAnnouncementUseCase: AddAnnouncementUseCase,
    private val getAnnouncementsUseCase: GetAnnouncementsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ImamUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        getMasjid()
        getMasjidDetails()
        fetchMasjidPrayerTime()
        getAnnouncements()
    }

    fun onImamEvent(intent: ImamIntent) {
        when (intent) {
            ImamIntent.ClearMessage -> TODO()
            ImamIntent.LoadSalahTimes -> TODO()
            is ImamIntent.UpdateSalahTimes -> TODO()
            is ImamIntent.createMasjid -> createMasjid(intent.masjid)
            is ImamIntent.updateMasjid -> TODO()
            ImamIntent.getMasjid -> getMasjid()
        }

    }

    private fun createMasjid(masjid: Masjid) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = createMasjidUseCase(masjid)
            when (result) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "Masjid created successfully ✅",
                            masjid = masjid
                        )
                    }
                }

                is Resource.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = result.message)
                    }
                }

                is Resource.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }


    fun getMasjid() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = getMasjidUseCase()
            when (result) {
                is Resource.Error -> _uiState.value = _uiState.value.copy(
                    isLoading = false, errorMessage = "Masjid fetched successfully ✅"
                )

                is Resource.Loading -> _uiState.value = _uiState.value.copy(isLoading = true)
                is Resource.Success -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = null,
                    masjid = result.data,
                    successMessage = "Masjid fetched successfully ✅"
                )
            }
        }
    }

    fun getMasjidDetails() {
        viewModelScope.launch {
            getMasjidDetailsUseCase().collect { result ->
                when (result) {
                    is Resource.Loading -> _uiState.value = _uiState.value.copy(isLoading = true)
                    is Resource.Success -> _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        masjid = result.data,
                        successMessage = "Masjid details loaded ✅"
                    )

                    is Resource.Error -> _uiState.value = _uiState.value.copy(
                        isLoading = false, errorMessage = result.message
                    )
                }
            }
        }
    }

    fun updatePrayerTimes(salahTime: SalahTime) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = addOrUpdateSalahTimesUseCase(salahTime)

            when (result) {
                is Resource.Success -> _uiState.value = _uiState.value.copy(
                    isLoading = false, successMessage = "Prayer times updated successfully!"
                )

                is Resource.Error -> _uiState.value = _uiState.value.copy(
                    isLoading = false, errorMessage = result.message
                )

                is Resource.Loading -> _uiState.value = _uiState.value.copy(isLoading = true)
            }
        }
    }

    fun fetchMasjidPrayerTime() {
        viewModelScope.launch {
            when (val result = getSalahTimesUseCase()) {
                is Resource.Success -> {
                    val salahTime = result.data
                    Log.d("SalahTime", "Salah time: $salahTime")
                    if (salahTime != null) {
                        val nextPrayer = salahTime.getNextPrayerAndCountdown()
                        val nextPrayerName = nextPrayer?.first
                        var timeUntilPrayer = nextPrayer?.second ?: 0L

                        // Calculate the absolute time of the next prayer
                        val nextPrayerAbsoluteTime = System.currentTimeMillis() + timeUntilPrayer

                        Log.d("SalahTime", "Salah time: $nextPrayer, $timeUntilPrayer, $nextPrayerAbsoluteTime")

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            salahTime = salahTime,
                            nextPrayerName = nextPrayerName,
                            nextPrayerTime = nextPrayerAbsoluteTime,
                            timeUntilPrayer = timeUntilPrayer
                        )

                        viewModelScope.launch {
                            while (timeUntilPrayer > 0) {
                                delay(1000)
                                timeUntilPrayer -= 1000
                                _uiState.update {
                                    it.copy(timeUntilPrayer = timeUntilPrayer)
                                }
                            }
                            fetchMasjidPrayerTime()
                        }
                    }
                }

                is Resource.Error -> _uiState.update {
                    it.copy(isLoading = false, errorMessage = result.message)
                }

                is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
            }
        }
    }

    fun addAnnouncement(announcement: Announcement) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    successMessage = null
                )
            }
            val result = addAnnouncementUseCase(announcement)

            when (result) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false, successMessage = "Announcement added successfully"
                        )
                    }
                    getAnnouncements()
                }

                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false, errorMessage = result.message
                        )
                    }
                }

                is Resource.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }


    fun getAnnouncements() {
        viewModelScope.launch {

            getAnnouncementsUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> _uiState.update {
                        it.copy(announcements = result.data, isLoading = false)
                    }

                    is Resource.Error -> _uiState.update {
                        it.copy(
                            errorMessage = result.message,
                            isLoading = false
                        )
                    }

                    is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                }
            }

        }
    }


}