package com.durranitech.salahsync.domain.usecase

import com.durranitech.salahsync.domain.model.Member
import com.durranitech.salahsync.domain.repository.ImamRepository
import com.durranitech.salahsync.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMembersUseCase @Inject constructor(
    private val imamRepository: ImamRepository
) {
    suspend operator fun invoke(): Flow<Resource<List<Member>>> {
        return imamRepository.getMembers()
    }
}