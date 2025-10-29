package com.durranitech.salahsync.data.repository

import androidx.room.util.query
import com.durranitech.salahsync.domain.model.Announcement
import com.durranitech.salahsync.domain.model.Masjid
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
            val currentUser = firebaseAuth.currentUser
                ?: return Resource.Error("User not authenticated")
            val existingMasjidQuery = firestore.collection("masjid")
                .whereEqualTo("imamId", currentUser.uid)
                .get()
                .await()

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

            val masjidSnapshot =
                firestore.collection("masjid").get().await()

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

            firestore.collection("masjid").document(masjidDocId).update("salahTimes", prayer)
                .await()

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Unknown error occurred")
        }
    }



    override suspend fun getMasjidPrayerTimes(): Resource<SalahTime> {
        return try {
            val currentUser = firebaseAuth.currentUser
                ?: return Resource.Error("User not authenticated")

            val masjidQuery = firestore.collection("masjid")
                .whereEqualTo("imamId", currentUser.uid)
                .get()
                .await()

            if (masjidQuery.isEmpty) {
                return Resource.Error("Masjid not found for this imam.")
            }

            val masjidDoc = masjidQuery.documents.first()
            val salahTimesMap = masjidDoc.get("salahTimes") as? Map<*, *>
                ?: return Resource.Error("Salah times not found for this masjid.")

            // Safely parse possible Firestore Number or Timestamp values to Long
            val salahTime = SalahTime(
                fajr = (salahTimesMap["fajr"] as? Number)?.toLong()
                    ?: (salahTimesMap["fajr"] as? com.google.firebase.Timestamp)?.toDate()?.time
                    ?: 0L,
                dhuhr = (salahTimesMap["dhuhr"] as? Number)?.toLong()
                    ?: (salahTimesMap["dhuhr"] as? com.google.firebase.Timestamp)?.toDate()?.time
                    ?: 0L,
                asr = (salahTimesMap["asr"] as? Number)?.toLong()
                    ?: (salahTimesMap["asr"] as? com.google.firebase.Timestamp)?.toDate()?.time
                    ?: 0L,
                maghrib = (salahTimesMap["maghrib"] as? Number)?.toLong()
                    ?: (salahTimesMap["maghrib"] as? com.google.firebase.Timestamp)?.toDate()?.time
                    ?: 0L,
                isha = (salahTimesMap["isha"] as? Number)?.toLong()
                    ?: (salahTimesMap["isha"] as? com.google.firebase.Timestamp)?.toDate()?.time
                    ?: 0L,
                jummah = (salahTimesMap["jummah"] as? Number)?.toLong()
                    ?: (salahTimesMap["jummah"] as? com.google.firebase.Timestamp)?.toDate()?.time
                    ?: 0L
            )

            Resource.Success(salahTime)

        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An unexpected error occurred while fetching salah times")
        }
    }

    override suspend fun addAnnouncements(announcement: Announcement): Resource<Unit> {
        return try {
            Resource.Loading()
            val currentUser = firebaseAuth.currentUser?: return Resource.Error("No user logged in")

            val masjidQuery = firestore.collection("masjid").whereEqualTo("imamId",currentUser.uid)
                .get()
                .await()
            if (masjidQuery.isEmpty){
                return Resource.Error("No masjid found for this user")
            }
            val masjidDocId = masjidQuery.documents.first().id
            val announcementRef = firestore.collection("masjid")
                .document(masjidDocId)
                .collection("announcements")
                .document()

            val announcementWithId = announcement.copy(id = announcementRef.id)
            announcementRef.set(announcementWithId)
            Resource.Success(Unit)
        } catch (e: Exception){
            Resource.Error(e.message ?: "An unexpected error occurred while adding announcement")
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

        firestore.collection("masjid")
            .whereEqualTo("imamId", currentUser.uid)
            .get().addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    trySend(Resource.Error("Masjid not found"))
                    close()
                    return@addOnSuccessListener
                }
                val masjidDocId = querySnapshot.documents.first().id
                listener = firestore.collection("masjid")
                    .document(masjidDocId)
                    .collection("announcements")
                    .orderBy("date", Query.Direction.DESCENDING)
                    .addSnapshotListener { snapshot, e ->
                        if (e != null) {
                            trySend(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
                            return@addSnapshotListener
                        }
                        val annoucements = snapshot?.toObjects(Announcement::class.java) ?: emptyList()
                        trySend(Resource.Success(annoucements))

                    }
            }.addOnFailureListener {
                trySend(Resource.Error(it.localizedMessage ?: "Failed to fetch masjid"))
                close()
            }
        awaitClose { listener?.remove() }
    }


}
