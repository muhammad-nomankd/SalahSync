package com.durranitech.salahsync.data.repository

import com.durranitech.salahsync.domain.model.Announcement
import com.durranitech.salahsync.domain.model.Masjid
import com.durranitech.salahsync.domain.model.Member
import com.durranitech.salahsync.domain.model.SalahTime
import com.durranitech.salahsync.domain.repository.MuqtadiRepository
import com.durranitech.salahsync.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class MuqtadiRepositoryImp(
    private val firebaseAuth: FirebaseAuth, private val firestore: FirebaseFirestore
) : MuqtadiRepository {

    override suspend fun fetchMasjidDetails(masjidId: String): Flow<Resource<Masjid>> = flow {
        emit(Resource.Loading())

        try {
            val user = firebaseAuth.currentUser
            if (user == null) {
                emit(Resource.Error("User not authenticated"))
                return@flow
            }

            val snapshot = firestore.collection("masjid").document(masjidId).get().await()

            if (!snapshot.exists()) {
                emit(Resource.Error("Masjid not found"))
                return@flow
            }

            val masjid = snapshot.toObject(Masjid::class.java)
            if (masjid != null) {
                emit(Resource.Success(masjid))
            } else {
                emit(Resource.Error("Failed to parse masjid"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Unexpected error"))
        }
    }

    override suspend fun getMasjidPrayerTimes(masjidId: String): Resource<SalahTime> {
        return try {
            val masjidDoc = firestore.collection("masjid").document(masjidId).get().await()

            if (!masjidDoc.exists()) {
                return Resource.Error("Masjid not found.")
            }

            val salahTimesMap = masjidDoc.get("salahTimes") as? Map<*, *>
                ?: return Resource.Error("Salah times not found for this masjid.")

            val salahTime = SalahTime(
                fajrHour = (salahTimesMap["fajrHour"] as? Number)?.toInt() ?: 5,
                fajrMinute = (salahTimesMap["fajrMinute"] as? Number)?.toInt() ?: 30,
                dhuhrHour = (salahTimesMap["dhuhrHour"] as? Number)?.toInt() ?: 12,
                dhuhrMinute = (salahTimesMap["dhuhrMinute"] as? Number)?.toInt() ?: 30,
                asrHour = (salahTimesMap["asrHour"] as? Number)?.toInt() ?: 15,
                asrMinute = (salahTimesMap["asrMinute"] as? Number)?.toInt() ?: 45,
                maghribHour = (salahTimesMap["maghribHour"] as? Number)?.toInt() ?: 18,
                maghribMinute = (salahTimesMap["maghribMinute"] as? Number)?.toInt() ?: 15,
                ishaHour = (salahTimesMap["ishaHour"] as? Number)?.toInt() ?: 20,
                ishaMinute = (salahTimesMap["ishaMinute"] as? Number)?.toInt() ?: 0,
                jummahHour = (salahTimesMap["jummahHour"] as? Number)?.toInt() ?: 13,
                jummahMinute = (salahTimesMap["jummahMinute"] as? Number)?.toInt() ?: 30
            )

            Resource.Success(salahTime)

        } catch (e: Exception) {
            Resource.Error(
                e.localizedMessage ?: "An unexpected error occurred while fetching salah times"
            )
        }
    }

    override suspend fun getMembers(masjidId: String): Flow<Resource<List<Member>>> = callbackFlow {
        trySend(Resource.Loading())
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            trySend(Resource.Error("User not authenticated"))
            close()
            return@callbackFlow
        }

        var listener: ListenerRegistration? = null

        listener = firestore.collection("masjid").document(masjidId).collection("members")
            .orderBy("joinedDate", Query.Direction.DESCENDING).addSnapshotListener { snapshot, e ->
                if (e != null) {
                    trySend(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
                    return@addSnapshotListener
                }
                val members = snapshot?.toObjects(Member::class.java) ?: emptyList()
                trySend(Resource.Success(members))
            }

        awaitClose { listener.remove() }
    }

    override suspend fun getAnnouncements(masjidId: String): Flow<Resource<List<Announcement>>> =
        callbackFlow {
            trySend(Resource.Loading())
            val currentUser = firebaseAuth.currentUser
            if (currentUser == null) {
                trySend(Resource.Error("User not authenticated"))
                close()
                return@callbackFlow
            }

            var listener: ListenerRegistration? = null

            firestore.collection("masjid").whereEqualTo("imamId", masjidId).get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty) {
                        trySend(Resource.Error("Masjid not found"))
                        close()
                        return@addOnSuccessListener
                    }
                    val masjidDocId = querySnapshot.documents.first().id
                    listener = firestore.collection("masjid").document(masjidDocId)
                        .collection("announcements").orderBy("date", Query.Direction.DESCENDING)
                        .addSnapshotListener { snapshot, e ->
                            if (e != null) {
                                trySend(
                                    Resource.Error(
                                        e.localizedMessage ?: "An unexpected error occurred"
                                    )
                                )
                                return@addSnapshotListener
                            }
                            val annoucements =
                                snapshot?.toObjects(Announcement::class.java) ?: emptyList()
                            trySend(Resource.Success(annoucements))

                        }
                }.addOnFailureListener {
                    trySend(Resource.Error(it.localizedMessage ?: "Failed to fetch masjid"))
                    close()
                }
            awaitClose { listener?.remove() }
        }


    override suspend fun logout(): Resource<Masjid> {
        TODO("Not yet implemented")
    }
}