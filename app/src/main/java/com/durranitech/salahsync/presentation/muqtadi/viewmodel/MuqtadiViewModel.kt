package com.durranitech.salahsync.presentation.muqtadi.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.durranitech.salahsync.domain.model.Masjid
import com.durranitech.salahsync.domain.model.SalahTime
import com.durranitech.salahsync.domain.usecase.FetchMasjidDetailsUseCase
import com.durranitech.salahsync.domain.usecase.GetMuqtadiAnnouncementsUseCase
import com.durranitech.salahsync.domain.usecase.GetMuqtadiPrayerTimesUseCase
import com.durranitech.salahsync.domain.usecase.SignOutUseCase
import com.durranitech.salahsync.domain.usecase.SubscribeToMasjidNotificationUseCase
import com.durranitech.salahsync.domain.usecase.UnsubscribeFromMasjidUseCase
import com.durranitech.salahsync.presentation.imam.UiState
import com.durranitech.salahsync.util.Resource
import com.durranitech.salahsync.util.getNextPrayerSimple
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.publishOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
@HiltViewModel
class MuqtadiViewModel @Inject constructor(
    private val fetchMasjidDetailUseCase: FetchMasjidDetailsUseCase,
    private val getMuqtadiPrayerTimesUseCase: GetMuqtadiPrayerTimesUseCase,
    private val getAnnouncementUseCase: GetMuqtadiAnnouncementsUseCase,
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val subscribeToMasjidNotificationUseCase: SubscribeToMasjidNotificationUseCase,
    private val unsubscribeFromMasjidUseCase: UnsubscribeFromMasjidUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState(isLoading = false))
    val uiState = _uiState.asStateFlow()

    private var prayerTimesListener: ListenerRegistration? = null
    private var masjidListener: ListenerRegistration? = null

    private var countdownJob: Job? = null
    private var currentMasjidId: String? = null

    private val _masjids = MutableStateFlow<List<Masjid>>(emptyList())
    val masjids: StateFlow<List<Masjid>> = _masjids

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _membershipResolved = MutableStateFlow(false)
    val membershipResolved: StateFlow<Boolean> = _membershipResolved

    private val _joinedMasjidId = MutableStateFlow<String?>(null)
    val joinedMasjidId: StateFlow<String?> = _joinedMasjidId

    private val authUidFlow = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.uid)
        }
        firebaseAuth.addAuthStateListener(listener)
        trySend(firebaseAuth.currentUser?.uid)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }.distinctUntilChanged()

    init {
        observeAuthState()  // Remove fetchMasjids() from here
    }


    val filteredMasjids: StateFlow<List<Masjid>> = combine(masjids, _searchQuery) { list, query ->
        if (query.isBlank()) list
        else list.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.city.contains(query, ignoreCase = true) ||
                    it.address.contains(query, ignoreCase = true) ||
                    it.imamName.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private fun observeAuthState() {
        viewModelScope.launch {
            authUidFlow.collect { uid ->
                Log.d("MuqtadiViewModel", "Auth state changed — uid=$uid")
                when {
                    uid != null -> {
                        _membershipResolved.value = false
                        fetchMasjids()
                        checkMembership()
                    }
                    else -> {
                        cleanup()
                        currentMasjidId = null
                        _joinedMasjidId.value = null
                        _membershipResolved.value = true
                        _uiState.update { UiState(isLoading = false) }
                    }
                }
            }
        }
    }

    // ── Membership check ──────────────────────────────────────────────────────

    fun checkMembership() {
        if (_membershipResolved.value) {
            Log.d("MuqtadiViewModel", "checkMembership: already resolved, skipping")
            return
        }

        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            // Auth not ready yet — observeAuthState() will call us again when it is
            Log.w("MuqtadiViewModel", "checkMembership: currentUser null, deferring")
            return
        }

        Log.d("MuqtadiViewModel", "checkMembership: running for ${currentUser.email}")

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val masjidsSnapshot = firestore.collection("masjid").get().await()
                var foundMasjidId: String? = null

                for (masjidDoc in masjidsSnapshot.documents) {
                    val memberSnapshot = firestore
                        .collection("masjid")
                        .document(masjidDoc.id)
                        .collection("members")
                        .whereEqualTo("email", currentUser.email)
                        .get()
                        .await()

                    if (!memberSnapshot.isEmpty) {
                        foundMasjidId = masjidDoc.id
                        Log.d("MuqtadiViewModel", "Membership found: $foundMasjidId")
                        break
                    }
                }

                _joinedMasjidId.value = foundMasjidId
                _membershipResolved.value = true

                if (foundMasjidId != null) {
                    start(foundMasjidId)
                } else {
                    Log.d("MuqtadiViewModel", "No membership for ${currentUser.email}")
                    _uiState.update { it.copy(isLoading = false) }
                }

            } catch (e: Exception) {
                Log.e("MuqtadiViewModel", "checkMembership error: ${e.message}")
                _joinedMasjidId.value = null
                _membershipResolved.value = true
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    // ── Start masjid listeners ────────────────────────────────────────────────

    fun start(masjidId: String) {
        if (masjidId.isBlank() || currentMasjidId == masjidId) return
        currentMasjidId = masjidId
        _uiState.update { it.copy(isLoading = true) }
        listenToMasjidDetails(masjidId)
        listenToPrayerTimes(masjidId)
    }

    // ── Real-time: Masjid ─────────────────────────────────────────────────────

    private fun listenToMasjidDetails(masjidId: String) {
        masjidListener?.remove()
        masjidListener = firestore
            .collection("masjid")
            .document(masjidId)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                    return@addSnapshotListener
                }
                if (snapshot == null || !snapshot.exists()) {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Masjid not found") }
                    return@addSnapshotListener
                }

                val masjid = snapshot.toObject(Masjid::class.java)
                Log.d("MuqtadiViewModel", "Masjid — name=${masjid?.name}, imamId=${masjid?.imamId}")

                _uiState.update { it.copy(isLoading = false, masjid = masjid) }

                if (masjid?.imamId.isNullOrBlank()) {
                    Log.e("MuqtadiViewModel", "imamId blank on ${snapshot.id} — announcements unavailable")
                    return@addSnapshotListener
                }
                listenToAnnouncements(masjid!!.imamId)
            }
    }

    // ── Real-time: Prayer times ───────────────────────────────────────────────

    private fun listenToPrayerTimes(masjidId: String) {
        prayerTimesListener?.remove()
        prayerTimesListener = firestore
            .collection("masjid")
            .document(masjidId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || !snapshot.exists()) return@addSnapshotListener

                val map = snapshot.get("salahTimes") as? Map<*, *> ?: return@addSnapshotListener

                val salahTime = SalahTime(
                    fajrHour      = (map["fajrHour"]      as? Number)?.toInt() ?: 5,
                    fajrMinute    = (map["fajrMinute"]    as? Number)?.toInt() ?: 30,
                    dhuhrHour     = (map["dhuhrHour"]     as? Number)?.toInt() ?: 12,
                    dhuhrMinute   = (map["dhuhrMinute"]   as? Number)?.toInt() ?: 30,
                    asrHour       = (map["asrHour"]       as? Number)?.toInt() ?: 15,
                    asrMinute     = (map["asrMinute"]     as? Number)?.toInt() ?: 45,
                    maghribHour   = (map["maghribHour"]   as? Number)?.toInt() ?: 18,
                    maghribMinute = (map["maghribMinute"] as? Number)?.toInt() ?: 15,
                    ishaHour      = (map["ishaHour"]      as? Number)?.toInt() ?: 20,
                    ishaMinute    = (map["ishaMinute"]    as? Number)?.toInt() ?: 0,
                    jummahHour    = (map["jummahHour"]    as? Number)?.toInt() ?: 13,
                    jummahMinute  = (map["jummahMinute"]  as? Number)?.toInt() ?: 30
                )

                val nextPrayer = salahTime.getNextPrayerSimple()
                _uiState.update {
                    it.copy(
                        salahTime       = salahTime,
                        nextPrayerName  = nextPrayer.name,
                        nextPrayerTime  = nextPrayer.atMillis,
                        timeUntilPrayer = nextPrayer.remainingMillis,
                        isLoading       = false
                    )
                }
                startCountdown(nextPrayer.remainingMillis)
            }
    }

    private fun listenToAnnouncements(imamId: String) {
        viewModelScope.launch {
            getAnnouncementUseCase(imamId).collect { result ->
                when (result) {
                    is Resource.Loading -> _uiState.update{it.copy(isLoading = true)}
                    is Resource.Success -> _uiState.update { it.copy(announcements = result.data, isLoading = false) }
                    is Resource.Error   -> _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }

    // ── Countdown ─────────────────────────────────────────────────────────────

    private fun startCountdown(initialMillis: Long) {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            var remaining = initialMillis
            while (remaining > 0) {
                delay(1000)
                remaining -= 1000
                _uiState.update { it.copy(timeUntilPrayer = remaining) }
            }
            refreshNextPrayer()
        }
    }

    private fun refreshNextPrayer() {
        val salahTime = _uiState.value.salahTime ?: run {
            currentMasjidId?.let { listenToPrayerTimes(it) }
            return
        }
        val next = salahTime.getNextPrayerSimple()
        _uiState.update {
            it.copy(
                nextPrayerName  = next.name,
                nextPrayerTime  = next.atMillis,
                timeUntilPrayer = next.remainingMillis
            )
        }
        startCountdown(next.remainingMillis)
    }

    // ── Masjid list ───────────────────────────────────────────────────────────

     fun fetchMasjids() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val snapshot = firestore.collection("masjid").get().await()
                _masjids.value = snapshot.documents.mapNotNull {
                    it.toObject(Masjid::class.java)?.copy(id = it.id)
                }
            } catch (e: Exception) {
                _error.value = "Failed to load masjids: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ── Join masjid ───────────────────────────────────────────────────────────

    fun joinMasjid(masjid: Masjid, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val currentUser = firebaseAuth.currentUser ?: run { onError("User not logged in"); return }
        viewModelScope.launch {
            try {
                firestore
                    .collection("masjid")
                    .document(masjid.id)
                    .collection("members")
                    .document(currentUser.uid)
                    .set(mapOf(
                        "id"          to currentUser.uid,
                        "email"       to (currentUser.email ?: ""),
                        "name"        to (currentUser.displayName ?: ""),
                        "joinedDate"  to System.currentTimeMillis(),
                        "address"     to "",
                        "phoneNumber" to ""
                    )).await()

                subscribeToMasjidNotificationUseCase(masjid.id)
                _joinedMasjidId.value = masjid.id
                _membershipResolved.value = true
                start(masjid.id)
                onSuccess()
            } catch (e: Exception) {
                Log.e("MuqtadiViewModel", "joinMasjid: ${e.message}")
                onError("Failed to join: ${e.message}")
            }
        }
    }

    fun onSearchQueryChange(query: String) { _searchQuery.value = query }

    fun signOut() {
        cleanup()
        firebaseAuth.signOut()
    }

    private fun cleanup() {
        prayerTimesListener?.remove()
        masjidListener?.remove()
        countdownJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        cleanup()
    }

}
