package com.durranitech.salahsync.di

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import com.durranitech.salahsync.data.datastore.UserPreferencesManager
import com.durranitech.salahsync.data.repository.AuthRepositoryImp
import com.durranitech.salahsync.domain.repository.AuthRepository
import com.durranitech.salahsync.domain.usecase.GetCurrentUserIdUseCase
import com.durranitech.salahsync.domain.usecase.GetCurrentUserUseCase
import com.durranitech.salahsync.domain.usecase.GetUserRoleUseCase
import com.durranitech.salahsync.domain.usecase.SignInUseCase
import com.durranitech.salahsync.domain.usecase.SignOutUseCase
import com.durranitech.salahsync.domain.usecase.SignUpUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AuthModule {

    @Provides
    @Singleton
    fun providesFirebaseAuth(): FirebaseAuth{
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun providesFirebaseFirestore(): FirebaseFirestore{
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun providesAuthRepository(firebaseAuth: FirebaseAuth,firebaseFirestore: FirebaseFirestore,userPreferencesManager: UserPreferencesManager): AuthRepository{
        return AuthRepositoryImp(firebaseAuth,firebaseFirestore,userPreferencesManager)
    }

    @Provides
    @Singleton
    fun providesGetCurrentUserUseCase(authRepository: AuthRepository): GetCurrentUserUseCase{
        return GetCurrentUserUseCase(authRepository)
    }
    @Provides
    @Singleton
    fun providesSignInUseCase(authRepository: AuthRepository): SignInUseCase{
        return SignInUseCase(authRepository)
    }
    @Provides
    @Singleton
    fun providesSignUpUseCase(authRepository: AuthRepository): SignUpUseCase{
        return SignUpUseCase(authRepository)
    }
    @Provides
    @Singleton
    fun providesSignOutUseCase(authRepository: AuthRepository): SignOutUseCase {
        return SignOutUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun getUserRoleUseCase(authRepository: AuthRepository): GetUserRoleUseCase {
        return GetUserRoleUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun getCurrentUserIdUseCase(authRepository: AuthRepository): GetCurrentUserIdUseCase {
        return GetCurrentUserIdUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun providesGetCurrentUserIdUseCase(@ApplicationContext context: Context): UserPreferencesManager {
        return UserPreferencesManager(context)
    }






}