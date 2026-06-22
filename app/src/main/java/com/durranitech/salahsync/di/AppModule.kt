package com.durranitech.salahsync.di

import android.content.Context
import com.durranitech.salahsync.data.local.datastore.UserPreferencesManager
import com.durranitech.salahsync.data.repository.AuthRepositoryImp
import com.durranitech.salahsync.data.repository.ImamRepositoryImp
import com.durranitech.salahsync.data.repository.MuqtadiRepositoryImp
import com.durranitech.salahsync.data.repository.NotificationRepositoryImp
import com.durranitech.salahsync.data.repository.QrRepositoryImpl
import com.durranitech.salahsync.domain.repository.AuthRepository
import com.durranitech.salahsync.domain.repository.ImamRepository
import com.durranitech.salahsync.domain.repository.MuqtadiRepository
import com.durranitech.salahsync.domain.repository.NotificationRepository
import com.durranitech.salahsync.domain.repository.QrRepository
import com.durranitech.salahsync.domain.usecase.AddAnnouncementUseCase
import com.durranitech.salahsync.domain.usecase.AddMemberUseCase
import com.durranitech.salahsync.domain.usecase.AddOrUpdateSalahTimesUseCase
import com.durranitech.salahsync.domain.usecase.CreateMasjidUseCase
import com.durranitech.salahsync.domain.usecase.DeleteAnnouncementUseCase
import com.durranitech.salahsync.domain.usecase.DeleteMemberUseCase
import com.durranitech.salahsync.domain.usecase.FetchMasjidDetailsUseCase
import com.durranitech.salahsync.domain.usecase.GetAnnouncementsUseCase
import com.durranitech.salahsync.domain.usecase.GetCurrentUserIdUseCase
import com.durranitech.salahsync.domain.usecase.GetCurrentUserUseCase
import com.durranitech.salahsync.domain.usecase.GetMasjidUseCase
import com.durranitech.salahsync.domain.usecase.GetMembersUseCase
import com.durranitech.salahsync.domain.usecase.GetMuqtadiAnnouncementsUseCase
import com.durranitech.salahsync.domain.usecase.GetUpcomingPrayerTimeUseCase
import com.durranitech.salahsync.domain.usecase.GetUserDetailsUseCase
import com.durranitech.salahsync.domain.usecase.GetUserRoleUseCase
import com.durranitech.salahsync.domain.usecase.SignInUseCase
import com.durranitech.salahsync.domain.usecase.SignOutUseCase
import com.durranitech.salahsync.domain.usecase.SignUpUseCase
import com.durranitech.salahsync.domain.usecase.SubscribeToMasjidNotificationUseCase
import com.durranitech.salahsync.domain.usecase.UnsubscribeFromMasjidUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideUserPreferencesManager(@ApplicationContext context: Context): UserPreferencesManager {
        return UserPreferencesManager(context)
    }

    @Provides
    @Singleton
    fun providesMuqtadiRepository(
        firebaseAuth: FirebaseAuth, firestore: FirebaseFirestore
    ): MuqtadiRepository {
        return MuqtadiRepositoryImp(firebaseAuth, firestore)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuth: FirebaseAuth,
        firebaseFirestore: FirebaseFirestore,
        userPreferencesManager: UserPreferencesManager
    ): AuthRepository {
        return AuthRepositoryImp(firebaseAuth, firebaseFirestore, userPreferencesManager)
    }

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
    fun provideGetCurrentUserIdUseCase(repository: AuthRepository) =
        GetCurrentUserIdUseCase(repository)

    @Provides
    @Singleton
    fun getUserDetailUseCase(repository: AuthRepository) = GetUserDetailsUseCase(repository)

    @Provides
    @Singleton
    fun provideImamRepository(
        firebaseFirestore: FirebaseFirestore, firebaseAuth: FirebaseAuth
    ): ImamRepository {
        return ImamRepositoryImp(firebaseFirestore, firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideCreateMasjidUseCase(repository: ImamRepository) = CreateMasjidUseCase(repository)

    @Provides
    @Singleton
    fun provideGetMasjidUseCase(repository: ImamRepository) = GetMasjidUseCase(repository)

    @Provides
    @Singleton
    fun addOrUpdateSalahTimeUseCase(repository: ImamRepository) =
        AddOrUpdateSalahTimesUseCase(repository)

    @Provides
    @Singleton
    fun getSalahTimesUseCase(repository: ImamRepository) = GetUpcomingPrayerTimeUseCase(repository)

    @Provides
    @Singleton
    fun addAnnouncementUseCase(repository: ImamRepository) = AddAnnouncementUseCase(repository)

    @Provides
    @Singleton
    fun getAnnouncementsUseCase(repository: ImamRepository) = GetAnnouncementsUseCase(repository)

    @Provides
    @Singleton
    fun deleteAnnouncementUseCase(repository: ImamRepository) =
        DeleteAnnouncementUseCase(repository)

    @Provides
    @Singleton
    fun addMemberUseCase(repository: ImamRepository) = AddMemberUseCase(repository)

    @Provides
    @Singleton
    fun deleteMemberUseCase(repository: ImamRepository) = DeleteMemberUseCase(repository)

    @Provides
    @Singleton
    fun getMembersUseCase(repository: ImamRepository) = GetMembersUseCase(repository)

    @Provides
    @Singleton
    fun fetchMasjidDetailsUseCase(repository: MuqtadiRepository) = FetchMasjidDetailsUseCase(
        repository = repository
    )

    @Provides
    @Singleton
    fun fetchMuqtadiAnnouncements(repository: MuqtadiRepository) =
        GetMuqtadiAnnouncementsUseCase(repository)

    @Provides
    @Singleton
    fun providesNotificationRepository(): NotificationRepository {
        return NotificationRepositoryImp()
    }

    @Provides
    @Singleton
    fun providesSubscribeToMasjidNotificationUseCase(repository: NotificationRepository) =
        SubscribeToMasjidNotificationUseCase(repository)

    @Provides
    @Singleton
    fun providesUnsubscribeFromMasjidUseCase(repository: NotificationRepository) =
        UnsubscribeFromMasjidUseCase(repository)

    @Provides
    @Singleton
    fun bindQrRepository(@ApplicationContext context: Context): QrRepository =
        QrRepositoryImpl(context)
}