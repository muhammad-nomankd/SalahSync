package com.durranitech.salahsync.presentation.imam.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.durranitech.salahsync.domain.model.Announcement
import com.durranitech.salahsync.domain.model.Masjid
import com.durranitech.salahsync.domain.model.Member
import com.durranitech.salahsync.domain.model.SalahTime
import com.durranitech.salahsync.domain.usecase.AddAnnouncementUseCase
import com.durranitech.salahsync.domain.usecase.AddMemberUseCase
import com.durranitech.salahsync.domain.usecase.AddOrUpdateSalahTimesUseCase
import com.durranitech.salahsync.domain.usecase.CreateMasjidUseCase
import com.durranitech.salahsync.domain.usecase.DeleteMemberUseCase
import com.durranitech.salahsync.domain.usecase.GetAnnouncementsUseCase
import com.durranitech.salahsync.domain.usecase.GetMasjidDetailsUseCase
import com.durranitech.salahsync.domain.usecase.GetMasjidUseCase
import com.durranitech.salahsync.domain.usecase.GetMembersUseCase
import com.durranitech.salahsync.domain.usecase.GetUpcommignPrayerTimeUseCase
import com.durranitech.salahsync.presentation.imam.ImamIntent
import com.durranitech.salahsync.presentation.imam.ImamUiState
import com.durranitech.salahsync.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import getNextPrayerSimple
import kotlinx.coroutines.Job
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
    private val getAnnouncementsUseCase: GetAnnouncementsUseCase,
    private val deleteAnnouncementUseCase: com.durranitech.salahsync.domain.usecase.DeleteAnnouncementUseCase,
    private val addMemberUseCase: AddMemberUseCase,
    private val deleteMemberUseCase: DeleteMemberUseCase,
    private val getMembersUseCase: GetMembersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ImamUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    private var countdownJob: Job? = null

    init {
        getMasjid()
        getMasjidDetails()
        fetchMasjidPrayerTime()
        getAnnouncements()
        getMembers()
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

            when (val result = createMasjidUseCase(masjid)) {
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
            when (val result = getMasjidUseCase()) {
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
            when (val result = addOrUpdateSalahTimesUseCase(salahTime)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Prayer times updated successfully!",
                        salahTime = salahTime
                    )
                    fetchMasjidPrayerTime()
                }

                is Resource.Error -> _uiState.value = _uiState.value.copy(
                    isLoading = false, errorMessage = result.message
                )

                is Resource.Loading -> _uiState.value = _uiState.value.copy(isLoading = true)
            }
        }
    }

    fun fetchMasjidPrayerTime() {
        countdownJob?.cancel()

        viewModelScope.launch {
            when (val result = getSalahTimesUseCase()) {
                is Resource.Success -> {
                    val salahTime = result.data
                    val nextPrayer = salahTime.getNextPrayerSimple()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        salahTime = salahTime,
                        nextPrayerName = nextPrayer?.name,
                        nextPrayerTime = nextPrayer?.atMillis,
                        timeUntilPrayer = nextPrayer?.remainingMillis ?: 0L
                    )
                    var countdown = nextPrayer?.remainingMillis ?: 0L
                    if (countdown > 0L) {
                        countdownJob = viewModelScope.launch {
                            while (countdown > 0) {
                                delay(1000)
                                countdown -= 1000
                                _uiState.update {
                                    it.copy(timeUntilPrayer = countdown)
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
                    isLoading = true, errorMessage = null, successMessage = null
                )
            }
            when (val result = addAnnouncementUseCase(announcement)) {
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
                            errorMessage = result.message, isLoading = false
                        )
                    }

                    is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                }
            }

        }
    }

    fun deleteAnnouncement(announcementId: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    successMessage = null
                )
            }
            val result = deleteAnnouncementUseCase(announcementId)

            when (result) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "Announcement deleted successfully"
                        )
                    }
                    // Refresh the list after deletion
                    getAnnouncements()
                }

                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }

                is Resource.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    fun getMembers() {
        viewModelScope.launch {
            getMembersUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> _uiState.update {
                        it.copy(members = result.data, isLoading = false)
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

    fun addMember(member: Member) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    successMessage = null
                )
            }
            val result = addMemberUseCase(member)

            when (result) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "Member added successfully"
                        )
                    }
                    // Refresh the list after addition
                    getMembers()
                }

                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }

                is Resource.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    fun deleteMember(memberId: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    successMessage = null
                )
            }
            val result = deleteMemberUseCase(memberId)

            when (result) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "Member deleted successfully"
                        )
                    }
                    // Refresh the list after deletion
                    getMembers()
                }

                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }

                is Resource.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }


}