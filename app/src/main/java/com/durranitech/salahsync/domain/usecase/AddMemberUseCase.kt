package com.durranitech.salahsync.domain.usecase

import com.durranitech.salahsync.domain.model.Member
import com.durranitech.salahsync.domain.repository.ImamRepository
import com.durranitech.salahsync.util.Resource
import javax.inject.Inject

class AddMemberUseCase @Inject constructor(
    private val imamRepository: ImamRepository
) {
    suspend operator fun invoke(member: Member): Resource<Unit> {
        return imamRepository.addMember(member)
    }
}