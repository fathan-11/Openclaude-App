package com.example.repopattern.domain.usecase

import com.example.repopattern.data.model.User
import com.example.repopattern.data.repository.Resource
import com.example.repopattern.data.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUsersUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<Resource<List<User>>> = userRepository.getUsers()
}
