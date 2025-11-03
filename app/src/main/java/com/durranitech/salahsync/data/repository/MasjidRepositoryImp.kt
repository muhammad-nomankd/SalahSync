/*
package com.durranitech.salahsync.data.repository

import com.durranitech.salahsync.domain.model.Announcement
import com.durranitech.salahsync.domain.model.SalahTime
import com.durranitech.salahsync.domain.repository.MasjidRepository
import com.durranitech.salahsync.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MasjidRepositoryImp (
    private val firebaseFirestore: FirebaseFirestore
) : MasjidRepository {
    override suspend fun getSalahTimes(masjidId: String): Resource<SalahTime> {
        return try {
            val snapshot = firebaseFirestore.collection("masjid")
                .document(masjidId)
                .get()
                .await()

            if (!snapshot.exists()) {
                return Resource.Error("Masjid not found for ID: $masjidId")
            }

            val salahMap = snapshot.get("salahTimes") as? Map<String, Long>

            if (salahMap == null) {
                Resource.Error("Salah times not found for this masjid")
            } else {
                val salahTime = SalahTime(
                    fajr = salahMap["fajr"] ?: 0L,
                    dhuhr = salahMap["dhuhr"] ?: 0L,
                    asr = salahMap["asr"] ?: 0L,
                    maghrib = salahMap["maghrib"] ?: 0L,
                    isha = salahMap["isha"] ?: 0L,
                    jummah = salahMap["jummah"] ?: 0L
                )
                Resource.Success(salahTime)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error("Failed to load salah times $e")
        }
    }
    override suspend fun updateSalahTimes(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun postAnnouncement(announcement: Announcement): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getAnnouncements(): List<Announcement> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAnnouncement(announcement: Announcement): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun updateAnnouncement(announcement: Announcement): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getNextPrayerTime(): SalahTime? {
        TODO("Not yet implemented")
    }
}*/
