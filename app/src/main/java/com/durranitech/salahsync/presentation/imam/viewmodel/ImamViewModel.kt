package com.durranitech.salahsync.presentation.imam.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.durranitech.salahsync.domain.model.Announcement
import com.durranitech.salahsync.domain.model.Masjid
import com.durranitech.salahsync.domain.model.Member
import com.durranitech.salahsync.domain.model.SalahTime
import com.durranitech.salahsync.domain.usecase.AddAnnouncementUseCase
import com.durranitech.salahsync.domain.usecase.AddMemberUseCase
import com.durranitech.salahsync.domain.usecase.CreateMasjidUseCase
import com.durranitech.salahsync.domain.usecase.DeleteAnnouncementUseCase
import com.durranitech.salahsync.domain.usecase.DeleteMemberUseCase
import com.durranitech.salahsync.domain.usecase.GetAnnouncementsUseCase
import com.durranitech.salahsync.domain.usecase.GetMasjidDetailsUseCase
import com.durranitech.salahsync.domain.usecase.GetMembersUseCase
import com.durranitech.salahsync.domain.usecase.GetUpcomingPrayerTimeUseCase
import com.durranitech.salahsync.domain.usecase.UpdatePrayerTimesUseCase
import com.durranitech.salahsync.presentation.imam.ImamEvent
import com.durranitech.salahsync.presentation.imam.UiState
import com.durranitech.salahsync.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject
@HiltViewModel
class ImamViewModel @Inject constructor(
    private val getMasjidDetailsUseCase: GetMasjidDetailsUseCase,
    private val getUpcomingPrayerTimeUseCase: GetUpcomingPrayerTimeUseCase,
    private val getAnnouncementsUseCase: GetAnnouncementsUseCase,
    private val addAnnouncementUseCase: AddAnnouncementUseCase,
    private val deleteAnnouncementUseCase: DeleteAnnouncementUseCase,
    private val updatePrayerTimesUseCase: UpdatePrayerTimesUseCase,
    private val getMembersUseCase: GetMembersUseCase,
    private val addMemberUseCase: AddMemberUseCase,
    private val deleteMemberUseCase: DeleteMemberUseCase,
    private val createMasjidUseCase: CreateMasjidUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var masjidDetailsJob: Job? = null

    init {
        fetchMasjidDetails()
        fetchAnnouncements()
        fetchMembers()
        startCountdownTimer()
    }



    fun onImamEvent(event: ImamEvent) {
        when (event) {
            is ImamEvent.UpdatePrayerTimes -> updatePrayerTimes(event.salahTime)
            is ImamEvent.AddAnnouncement -> addAnnouncement(event.announcement)
            is ImamEvent.DeleteAnnouncement -> deleteAnnouncement(event.announcementId)
            is ImamEvent.AddMember -> addMember(event.member)
            is ImamEvent.DeleteMember -> deleteMember(event.memberId)
            is ImamEvent.ClearMessages -> clearMessages()
            is ImamEvent.RefreshData -> fetchMasjidDetails()
        }
    }


    fun fetchMasjidDetails() {
        masjidDetailsJob?.cancel()
        masjidDetailsJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            getMasjidDetailsUseCase().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }

                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                masjid = resource.data,
                                salahTime = resource.data.salahTimes
                            )
                        }
                        resource.data.salahTimes?.let { salahTime ->
                            calculateNextPrayer(salahTime)
                        }
                    }

                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = resource.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun createMasjid(masjid: Masjid) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = createMasjidUseCase(masjid)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "Masjid created successfully",
                            masjid = masjid
                        )
                    }
                    fetchMasjidDetails()
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

    fun updatePrayerTimes(salahTime: SalahTime) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = updatePrayerTimesUseCase(salahTime)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "Prayer times updated successfully",
                            salahTime = salahTime
                        )
                    }
                    calculateNextPrayer(salahTime)
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

    private fun calculateNextPrayer(salahTime: SalahTime) {
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis

        val prayers = listOf(
            "Fajr" to getTimeInMillis(salahTime.fajrHour?:0, salahTime.fajrMinute?:0),
            "Dhuhr" to getTimeInMillis(salahTime.dhuhrHour?:0, salahTime.dhuhrMinute?:0),
            "Asr" to getTimeInMillis(salahTime.asrHour?:0, salahTime.asrMinute?:0),
            "Maghrib" to getTimeInMillis(salahTime.maghribHour?:0, salahTime.maghribMinute?:0),
            "Isha" to getTimeInMillis(salahTime.ishaHour?:0, salahTime.ishaMinute?:0)
        )

        // Find next prayer today
        var nextPrayer = prayers.firstOrNull { (_, timeMillis) ->
            timeMillis > now
        }

        // If no more prayers today, first prayer tomorrow
        if (nextPrayer == null) {
            nextPrayer = prayers.first().let { (name, timeMillis) ->
                name to (timeMillis + 24 * 60 * 60 * 1000)
            }
        }

        _uiState.update {
            it.copy(
                nextPrayerName = nextPrayer.first,
                nextPrayerTime = nextPrayer.second,
                timeUntilPrayer = nextPrayer.second - now
            )
        }
    }

    private fun getTimeInMillis(hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    private fun startCountdownTimer() {
        viewModelScope.launch {
            while (true) {
                delay(1000)
                _uiState.value.salahTime?.let { salahTime ->
                    calculateNextPrayer(salahTime)
                }
            }
        }
    }

    private fun fetchAnnouncements() {
        viewModelScope.launch {
            getAnnouncementsUseCase().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.update{
                            it.copy(isLoading = true)
                        }
                    }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(announcements = resource.data ?: emptyList())
                        }
                    }

                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(errorMessage = resource.message)
                        }
                    }
                }
            }
        }
    }

    fun addAnnouncement(announcement: Announcement) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = addAnnouncementUseCase(announcement)) {
                is Resource.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
                is Resource.Success -> {
                    fetchAnnouncements()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "Announcement added successfully"
                        )
                    }
                }

                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    fun deleteAnnouncement(announcementId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = deleteAnnouncementUseCase(announcementId)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "Announcement deleted successfully"
                        )
                    }
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


    private fun fetchMembers() {
        viewModelScope.launch {
            getMembersUseCase().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.update {
                            it.copy(
                                members = it.members,
                                isLoading = false
                            )
                        }
                    }

                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(members = resource.data ?: emptyList())
                        }
                    }

                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                errorMessage = resource.message,
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
    }

    fun addMember(member: Member) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = addMemberUseCase(member)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "Member added successfully"
                        )
                    }
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
            _uiState.update { it.copy(isLoading = true) }

            when (val result = deleteMemberUseCase(memberId)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "Member deleted successfully"
                        )
                    }
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

    private fun clearMessages() {
        _uiState.update {
            it.copy(
                successMessage = null,
                errorMessage = null
            )
        }
    }
}
