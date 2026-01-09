package com.durranitech.salahsync.data.repository

import com.durranitech.salahsync.domain.model.Announcement
import com.durranitech.salahsync.domain.model.Masjid
import com.durranitech.salahsync.domain.model.Member
import com.durranitech.salahsync.domain.model.SalahTime
import com.durranitech.salahsync.domain.repository.ImamRepository
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

class ImamRepositoryImp(
    private val firestore: FirebaseFirestore, private val firebaseAuth: FirebaseAuth
) : ImamRepository {

    override suspend fun getMasjid(): Resource<Masjid> {
        return try {
            val currentUser =
                firebaseAuth.currentUser ?: return Resource.Error("User not authenticated")

            val querySnapshot =
                firestore.collection("masjid").whereEqualTo("imamId", currentUser.uid).get().await()

            val masjid = querySnapshot.toObjects(Masjid::class.java).firstOrNull()
            if (masjid != null) {
                Resource.Success(masjid)
            } else {
                Resource.Error("No masjid found for this imam")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unexpected error occurred while fetching masjid")
        }
    }

    override suspend fun createMasjid(masjid: Masjid): Resource<Unit> {
        return try {
            val currentUser =
                firebaseAuth.currentUser ?: return Resource.Error("User not authenticated")
            val existingMasjidQuery =
                firestore.collection("masjid").whereEqualTo("imamId", currentUser.uid).get().await()

            if (!existingMasjidQuery.isEmpty) {
                return Resource.Error("Masjid already exists for this imam.")
            }
            val masjidRef = firestore.collection("masjid").document()
            val masjidWithId = masjid.copy(id = masjidRef.id, imamId = currentUser.uid)

            masjidRef.set(masjidWithId).await()

            Resource.Success(Unit)

        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unexpected error occurred while creating masjid")
        }
    }


    override fun getMasjidDetails(): Flow<Resource<Masjid>> = flow {
        emit(Resource.Loading())

        try {
            val currentUser = firebaseAuth.currentUser
            if (currentUser == null) {
                emit(Resource.Error("User not authenticated"))
                return@flow
            }

            val masjidSnapshot = firestore.collection("masjid").get().await()

            if (masjidSnapshot.isEmpty) {
                emit(Resource.Error("No masjid found for this Imam"))
                return@flow
            }

            val masjid = masjidSnapshot.documents.first().toObject(Masjid::class.java)
            if (masjid != null) {
                emit(Resource.Success(masjid))
            } else {
                emit(Resource.Error("Failed to parse Masjid data"))
            }

        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Unexpected error occurred"))
        }
    }

    // Save/update prayer times as hour/minute fields
    override suspend fun updateMasjidPrayerTimes(prayer: SalahTime): Resource<Unit> {
        return try {
            val currentUser =
                firebaseAuth.currentUser ?: return Resource.Error("User not authenticated")

            val masjidQuery =
                firestore.collection("masjid").whereEqualTo("imamId", currentUser.uid).get().await()

            if (masjidQuery.isEmpty) {
                return Resource.Error("Masjid not found for this imam.")
            }

            val masjidDocId = masjidQuery.documents.first().id

            // Just save the SalahTime object—it will be mapped correctly
            firestore.collection("masjid").document(masjidDocId).update("salahTimes", prayer)
                .await()

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Unknown error occurred")
        }
    }

    // Fetch prayer times as hour/minute fields and map to SalahTime object
    override suspend fun getMasjidPrayerTimes(): Resource<SalahTime> {
        return try {
            val currentUser =
                firebaseAuth.currentUser ?: return Resource.Error("User not authenticated")

            val masjidQuery =
                firestore.collection("masjid").whereEqualTo("imamId", currentUser.uid).get().await()

            if (masjidQuery.isEmpty) {
                return Resource.Error("Masjid not found for this imam.")
            }

            val masjidDoc = masjidQuery.documents.first()
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


    override suspend fun addAnnouncements(announcement: Announcement): Resource<Unit> {
        return try {
            Resource.Loading()
            val currentUser = firebaseAuth.currentUser ?: return Resource.Error("No user logged in")

            val masjidQuery =
                firestore.collection("masjid").whereEqualTo("imamId", currentUser.uid).get().await()
            if (masjidQuery.isEmpty) {
                return Resource.Error("No masjid found for this user")
            }
            val masjidDocId = masjidQuery.documents.first().id
            val announcementRef =
                firestore.collection("masjid").document(masjidDocId).collection("announcements")
                    .document()

            val announcementWithId = announcement.copy(id = announcementRef.id)
            announcementRef.set(announcementWithId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unexpected error occurred while adding announcement")
        }


    }

    override suspend fun deleteAnnouncement(announcementId: String): Resource<Unit> {
        return try {
            val currentUser =
                firebaseAuth.currentUser ?: return Resource.Error("User not authenticated")

            val masjidQuery =
                firestore.collection("masjid").whereEqualTo("imamId", currentUser.uid).get().await()

            if (masjidQuery.isEmpty) {
                return Resource.Error("No masjid found for this user")
            }

            val masjidDocId = masjidQuery.documents.first().id

            firestore.collection("masjid").document(masjidDocId).collection("announcements")
                .document(announcementId).delete().await()

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unexpected error occurred while deleting announcement")
        }
    }


    override suspend fun getAnnouncements(): Flow<Resource<List<Announcement>>> = callbackFlow {
        trySend(Resource.Loading())
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            trySend(Resource.Error("User not authenticated"))
            close()
            return@callbackFlow
        }

        var listener: ListenerRegistration? = null

        firestore.collection("masjid").whereEqualTo("imamId", currentUser.uid).get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    trySend(Resource.Error("Masjid not found"))
                    close()
                    return@addOnSuccessListener
                }
                val masjidDocId = querySnapshot.documents.first().id
                listener =
                    firestore.collection("masjid").document(masjidDocId).collection("announcements")
                        .orderBy("date", Query.Direction.DESCENDING)
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



    override suspend fun addMember(member: Member): Resource<Unit> {
        return try {
            val currentUser =
                firebaseAuth.currentUser ?: return Resource.Error("User not authenticated")

            val masjidQuery =
                firestore.collection("masjid").whereEqualTo("imamId", currentUser.uid).get().await()

            if (masjidQuery.isEmpty) {
                return Resource.Error("No masjid found for this user")
            }

            val masjidDocId = masjidQuery.documents.first().id
            val memberRef =
                firestore.collection("masjid").document(masjidDocId).collection("members")
                    .document()

            val memberWithId = member.copy(id = memberRef.id)
            memberRef.set(memberWithId).await()

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unexpected error occurred while adding member")
        }
    }

    override suspend fun deleteMember(memberId: String): Resource<Unit> {
        return try {
            val currentUser =
                firebaseAuth.currentUser ?: return Resource.Error("User not authenticated")

            val masjidQuery =
                firestore.collection("masjid").whereEqualTo("imamId", currentUser.uid).get().await()

            if (masjidQuery.isEmpty) {
                return Resource.Error("No masjid found for this user")
            }

            val masjidDocId = masjidQuery.documents.first().id

            firestore.collection("masjid").document(masjidDocId).collection("members")
                .document(memberId).delete().await()

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unexpected error occurred while deleting member")
        }
    }

    override suspend fun getMembers(): Flow<Resource<List<Member>>> = callbackFlow {
        trySend(Resource.Loading())
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            trySend(Resource.Error("User not authenticated"))
            close()
            return@callbackFlow
        }

        var listener: ListenerRegistration? = null

        firestore.collection("masjid").whereEqualTo("imamId", currentUser.uid).get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    trySend(Resource.Error("Masjid not found"))
                    close()
                    return@addOnSuccessListener
                }
                val masjidDocId = querySnapshot.documents.first().id
                listener =
                    firestore.collection("masjid").document(masjidDocId).collection("members")
                        .orderBy("joinedDate", Query.Direction.DESCENDING)
                        .addSnapshotListener { snapshot, e ->
                            if (e != null) {
                                trySend(
                                    Resource.Error(
                                        e.localizedMessage ?: "An unexpected error occurred"
                                    )
                                )
                                return@addSnapshotListener
                            }
                            val members = snapshot?.toObjects(Member::class.java) ?: emptyList()
                            trySend(Resource.Success(members))
                        }
            }.addOnFailureListener {
                trySend(Resource.Error(it.localizedMessage ?: "Failed to fetch masjid"))
                close()
            }
        awaitClose { listener?.remove() }
    }

}
