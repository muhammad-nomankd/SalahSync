package com.durranitech.salahsync.domain.usecase

import com.durranitech.salahsync.domain.repository.ImamRepository
import com.durranitech.salahsync.util.Resource
import javax.inject.Inject

class DeleteMemberUseCase @Inject constructor(
    private val imamRepository: ImamRepository
) {
    suspend operator fun invoke(memberId: String): Resource<Unit> {
        return imamRepository.deleteMember(memberId)
    }
}