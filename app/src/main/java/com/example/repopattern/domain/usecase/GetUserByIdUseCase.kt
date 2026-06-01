package com.example.repopattern.domain.usecase

import com.example.repopattern.data.model.User
import com.example.repopattern.data.repository.Resource
import com.example.repopattern.data.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserByIdUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(userId: Int): Flow<Resource<User>> = userRepository.getUserById(userId)
}
