package com.durranitech.salahsync.data.repository

import androidx.compose.ui.input.key.Key.Companion.U
import com.durranitech.salahsync.domain.repository.NotificationRepository
import com.durranitech.salahsync.util.Resource
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

class NotificationRepositoryImp: NotificationRepository {
    override suspend fun subscribeToMasjid(masjidId: String): Resource<Unit> {
        return try {
            FirebaseMessaging.getInstance()
                .subscribeToTopic("masjid_${masjidId}_general")
                .await()
            Resource.Success(Unit)
        }catch (e: Exception){
            Resource.Error(e.message?:"Failed to Subscribe")

        }
    }

    override suspend fun unsubscribeFromMasjid(masjidId: String): Resource<Unit> {
       return try {
           FirebaseMessaging.getInstance()
               .unsubscribeFromTopic("masjid_${masjidId}_general")
               .await()
           Resource.Success(Unit)
       } catch (e: Exception){
           Resource.Error(e.message?:"Failed to unsubscribe")
       }
    }
}