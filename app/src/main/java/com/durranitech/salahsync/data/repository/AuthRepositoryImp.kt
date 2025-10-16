package com.durranitech.salahsync.data.repository

import com.durranitech.salahsync.data.datastore.UserPreferencesManager
import com.durranitech.salahsync.domain.model.User
import com.durranitech.salahsync.domain.model.UserRole
import com.durranitech.salahsync.domain.repository.AuthRepository
import com.durranitech.salahsync.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.tasks.await

class AuthRepositoryImp(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore,
    private val prefs: UserPreferencesManager
) : AuthRepository {

    override fun signUp(
        name: String, email: String, password: String, role: UserRole
    ): Flow<Resource<User>> = callbackFlow {
        trySend(Resource.Loading())

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                authResult.user.let { firebaseUser ->
                    val user = User(
                        id = firebaseUser?.uid ?: "",
                        name = name,
                        email = firebaseUser?.email ?: "",
                        role = role
                    )
                    firebaseFirestore.collection("users").document(firebaseUser?.uid ?: "")
                        .set(user).addOnSuccessListener {
                            launch {
                                prefs.saveUserRole(role.name)
                                trySend(Resource.Success(user))
                            }
                        }.addOnFailureListener { e ->
                            trySend(Resource.Error(e.message ?: "Failed to save user"))
                        }
                }
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
                val userDoc = firebaseFirestore.collection("users")
                    .document(user.uid)
                    .get()
                    .await()

                if (userDoc.exists()) {
                    val userData = userDoc.toObject(User::class.java)
                    if (userData != null) {
                        userData.role?.let { prefs.saveUserRole(it.name) }
                        trySend(Resource.Success(userData))
                    } else {
                        trySend(Resource.Error("Failed to parse user data"))
                    }
                } else {
                    val userModel = User(
                        id = user.uid,
                        name = user.displayName ?: "",
                        email = user.email ?: "",
                        phone = ""
                    )
                    trySend(Resource.Success(userModel))
                }
            } else {
                trySend(Resource.Error("Login failed: No user returned"))
            }
        } catch (e: Exception) {
            trySend(Resource.Error(e.message ?: "Unknown error during sign in"))
        }
        awaitClose { }
    }

    override fun getCurrentUser(): Flow<Resource<User>> = callbackFlow {
        trySend(Resource.Loading())
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            Resource.Success(
                User(
                    currentUser.uid, currentUser.displayName ?: "", currentUser.email ?: ""
                )
            )
        } else {
            trySend(Resource.Error("User not logged in"))
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

        val catchedRole = prefs.readUserRole().firstOrNull()
        if (!catchedRole.isNullOrBlank()) {
            try {
                Resource.Success(UserRole.valueOf(catchedRole))
            } catch (e: Exception) {
                val currentUser = firebaseAuth.currentUser
                val doc =
                    firebaseFirestore.collection("users").document(currentUser?.uid ?: "").get()
                        .await()

                if (doc.exists()) {
                    val roleString = doc.getString("role")
                    val userRole = UserRole.valueOf(roleString ?: "")
                    Resource.Success(userRole)
                } else {
                    Resource.Error("User document not found")
                }
            } catch (e: Exception) {
                Resource.Error(e.message.toString())

            }
        } else {
            Resource.Error("User role not found")
        }
    } catch (e: Exception) {
        Resource.Error(e.message.toString())
    }


    override suspend fun getCurrentUserId(): Resource<String> {
        return try {
            val user = firebaseAuth.currentUser
            if (user != null) {
                Resource.Success(user.uid)
            } else {
                Resource.Error("User not logged in")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Unknown error")
        }
    }

}