package com.durranitech.salahsync.domain.usecase

import com.durranitech.salahsync.domain.model.Masjid
import com.durranitech.salahsync.domain.repository.ImamRepository
import com.durranitech.salahsync.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMasjidDetailsUseCase @Inject constructor(
    private val repository: ImamRepository
) {
    operator fun invoke(): Flow<Resource<Masjid>> = repository.getMasjidDetails()
}