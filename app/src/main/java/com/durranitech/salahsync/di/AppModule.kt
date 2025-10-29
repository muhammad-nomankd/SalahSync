package com.durranitech.salahsync.di

import android.content.Context
import com.durranitech.salahsync.data.datastore.UserPreferencesManager
import com.durranitech.salahsync.data.repository.AuthRepositoryImp
import com.durranitech.salahsync.data.repository.ImamRepositoryImp
import com.durranitech.salahsync.domain.repository.AuthRepository
import com.durranitech.salahsync.domain.repository.ImamRepository
import com.durranitech.salahsync.domain.usecase.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // ✅ FirebaseAuth
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    // ✅ Firestore
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    // ✅ DataStore Manager (fix name here)
    @Provides
    @Singleton
    fun provideUserPreferencesManager(@ApplicationContext context: Context): UserPreferencesManager {
        return UserPreferencesManager(context)
    }

    // ✅ Auth Repository
    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuth: FirebaseAuth,
        firebaseFirestore: FirebaseFirestore,
        userPreferencesManager: UserPreferencesManager
    ): AuthRepository {
        return AuthRepositoryImp(firebaseAuth, firebaseFirestore, userPreferencesManager)
    }

    // ✅ Auth-related UseCases
    @Provides
    @Singleton
    fun provideSignInUseCase(repository: AuthRepository) = SignInUseCase(repository)

    @Provides
    @Singleton
    fun provideSignUpUseCase(repository: AuthRepository) = SignUpUseCase(repository)

    @Provides
    @Singleton
    fun provideSignOutUseCase(repository: AuthRepository) = SignOutUseCase(repository)

    @Provides
    @Singleton
    fun provideGetCurrentUserUseCase(repository: AuthRepository) = GetCurrentUserUseCase(repository)

    @Provides
    @Singleton
    fun provideGetUserRoleUseCase(repository: AuthRepository) = GetUserRoleUseCase(repository)

    @Provides
    @Singleton
    fun provideGetCurrentUserIdUseCase(repository: AuthRepository) = GetCurrentUserIdUseCase(repository)

    @Provides
    @Singleton
    fun getUserDetailUseCase(repository: AuthRepository) = GetUserDetailsUseCase(repository)

    // ✅ Imam Repository
    @Provides
    @Singleton
    fun provideImamRepository(
        firebaseFirestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth
    ): ImamRepository {
        return ImamRepositoryImp(firebaseFirestore, firebaseAuth)
    }

    // ✅ Imam UseCases
    @Provides
    @Singleton
    fun provideCreateMasjidUseCase(repository: ImamRepository) = CreateMasjidUseCase(repository)

    @Provides
    @Singleton
    fun provideGetMasjidUseCase(repository: ImamRepository) = GetMasjidUseCase(repository)

    @Provides
    @Singleton
    fun addOrUpdateSalahTimeUseCase(repository: ImamRepository) = AddOrUpdateSalahTimesUseCase(repository)

    @Provides
    @Singleton
    fun getSalahTimesUseCase(repository: ImamRepository) = GetUpcommignPrayerTimeUseCase(repository)

    @Provides
    @Singleton
    fun addAnnouncementUseCase(repository: ImamRepository) = AddAnnouncementUseCase(repository)

    @Provides
    @Singleton
    fun getAnnouncementsUseCase(repository: ImamRepository) = GetAnnouncementsUseCase(repository)


}
