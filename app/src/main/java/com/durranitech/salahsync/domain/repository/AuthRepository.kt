package com.durranitech.salahsync.domain.repository

import com.durranitech.salahsync.domain.model.User
import com.durranitech.salahsync.domain.model.UserRole
import com.durranitech.salahsync.util.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun signIn(email: String, password: String): Flow<Resource<User>>
    fun signUp(name: String,email: String, password: String,role: UserRole): Flow<Resource<User>>

    fun getCurrentUser(): Flow<Resource<User>>
    fun signOut():Flow<Resource<Unit>>

    suspend fun getUserRole(userId: String): Resource<UserRole>

    suspend fun getCurrentUserId(): Resource<String>

    suspend fun getUserDetails():Flow<Resource<User>>



}