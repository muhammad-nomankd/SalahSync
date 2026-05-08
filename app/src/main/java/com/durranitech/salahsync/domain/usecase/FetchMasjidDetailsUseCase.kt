package com.durranitech.salahsync.domain.usecase

import android.R.attr.name
import android.R.attr.password
import com.durranitech.salahsync.data.repository.MuqtadiRepositoryImp
import com.durranitech.salahsync.domain.model.UserRole
import com.durranitech.salahsync.domain.repository.AuthRepository
import com.durranitech.salahsync.domain.repository.MuqtadiRepository

class FetchMasjidDetailsUseCase(private val repository: MuqtadiRepository) {
    suspend operator fun invoke(masjidId: String) =
        repository.fetchMasjidDetails(masjidId)

}