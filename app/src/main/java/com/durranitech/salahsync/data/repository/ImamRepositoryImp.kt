package com.durranitech.salahsync.data.repository

import com.durranitech.salahsync.domain.model.Masjid
import com.durranitech.salahsync.domain.repository.ImamRepository
import com.durranitech.salahsync.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class ImamRepositoryImp(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : ImamRepository {

    override suspend fun getMasjid(): Resource<Masjid> {
        return try {
            val currentUser = firebaseAuth.currentUser
                ?: return Resource.Error("User not authenticated")

            val querySnapshot = firestore.collection("masjid")
                .whereEqualTo("imamId", currentUser.uid)
                .get()
                .await()

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
            val uid = firebaseAuth.currentUser?.uid ?: return Resource.Error("No user found")
            val masjidRef = firestore.collection("users").document(uid).collection("masjid").document()

            val masjidWithId = masjid.copy(id = masjidRef.id)

            masjidRef.set(masjidWithId).await()
            Resource.Success(Unit)

        }catch (e:Exception){
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

            val masjidSnapshot = firestore.collection("users")
                .document(currentUser.uid)
                .collection("masjid")
                .limit(1)
                .get()
                .await()

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
}
