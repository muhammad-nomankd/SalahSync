package com.durranitech.salahsync.data.repository

import android.R.attr.password
import android.util.Log.e
import com.durranitech.salahsync.domain.model.User
import com.durranitech.salahsync.domain.model.UserRole
import com.durranitech.salahsync.domain.repository.AuthRepository
import com.durranitech.salahsync.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AuthRepositoryImp(
    private val firebaseAuth: FirebaseAuth, private val firebaseFirestore: FirebaseFirestore
) : AuthRepository {

    override fun signUp(
        name: String,
        phone: String,
        email: String,
        password: String,
        role: UserRole
    ): Flow<Resource<User>> = callbackFlow {
        trySend(Resource.Loading())

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                authResult.user.let { firebaseUser ->
                    val user = User(
                        id = firebaseUser?.uid ?: "",
                        name = name,
                        phone = phone,
                        email = firebaseUser?.email ?: "",
                        role = role
                    )
                    firebaseFirestore.collection("users").document(firebaseUser?.uid ?: "")
                        .set(user).addOnSuccessListener {
                            trySend(Resource.Success(user))
                        }.addOnFailureListener { e ->
                            trySend(Resource.Error(e.message ?: "Failed to save user"))
                        }
                } ?: trySend(Resource.Error("User crreation failed"))

            }.addOnFailureListener { e ->
                trySend(Resource.Error(e.message.toString()))
            }
        awaitClose { }

    }

    override fun signIn(email: String, password: String): Flow<Resource<User>> = callbackFlow {
        trySend(Resource.Loading())
        try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                val userModel = User(user.uid, user.displayName ?: "", user.email ?: "")
                Resource.Success(userModel)
            } else {
                Resource.Error("Login failed")
            }
        } catch (e: Exception) {
            trySend(Resource.Error(e.message ?: "Unknown error"))
        }

        awaitClose { }
    }

    override fun getCurrentUser(): Flow<Resource<User>> = callbackFlow {
        trySend(Resource.Loading())
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            Resource.Success(
                User(
                    currentUser.uid,
                    currentUser.displayName ?: "",
                    currentUser.email ?: ""
                )
            )
        } else {
            Resource.Success(null)
        }
        awaitClose { }

    }

    override fun signOut(): Flow<Resource<Unit>> = callbackFlow {
        trySend(Resource.Loading())
        try {
            firebaseAuth.signOut()
            trySend(Resource.Success(Unit))
        } catch (e: Exception) {
            trySend(Resource.Error(e.message ?: "Unknown error"))
        }

        awaitClose { }

    }

    override suspend fun getUserRole(userId: String): Resource<UserRole> = try {
        val currentUser = firebaseAuth.currentUser
        val doc = firebaseFirestore.collection("users").document(currentUser?.uid?:"").get().await()

        if (doc.exists()){
            val roleString = doc.getString("role")
            val userRole = UserRole.valueOf(roleString?:"")
            Resource.Success(userRole)
        }else{
            Resource.Error("User document not found")
        }
    } catch (e: Exception) {
        Resource.Error(e.message.toString())

    }

    override suspend fun getCurrentUserId(): Resource<String> {
       return try {
            val user = firebaseAuth.currentUser
           if (user !=null){
               Resource.Success(user.uid)
           }else{
               Resource.Error("User not logged in")
           }
        } catch (e: Exception){
           Resource.Error(e.localizedMessage?:"Unknown error")
        }
    }

}