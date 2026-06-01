package com.example.repopattern.data.repository

import com.example.repopattern.data.local.LocalDataSource
import com.example.repopattern.data.model.User
import com.example.repopattern.data.model.toDomain
import com.example.repopattern.data.model.toDomainList
import com.example.repopattern.data.model.toEntities
import com.example.repopattern.data.model.toEntity
import com.example.repopattern.data.remote.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

interface UserRepository {
    fun getUsers(): Flow<Resource<List<User>>>
    fun getUserById(id: Int): Flow<Resource<User>>
}

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : UserRepository {

    override fun getUsers(): Flow<Resource<List<User>>> = flow {
        emit(Resource.Loading)

        val cachedEntities = localDataSource.getUsers().first()
        if (cachedEntities.isNotEmpty()) {
            emit(Resource.Success(cachedEntities.toDomainList()))
        }

        try {
            val remoteDtos = remoteDataSource.getUsers()
            val entities = remoteDtos.toEntities()
            localDataSource.insertUsers(entities)

            val freshUsers = localDataSource.getUsers().first()
            emit(Resource.Success(freshUsers.toDomainList()))
        } catch (e: Exception) {
            if (cachedEntities.isEmpty()) {
                emit(Resource.Error(e.message ?: "An unexpected error occurred"))
            }
        }
    }

    override fun getUserById(id: Int): Flow<Resource<User>> = flow {
        emit(Resource.Loading)

        val cachedUser = localDataSource.getUserById(id)
        if (cachedUser != null) {
            emit(Resource.Success(cachedUser.toDomain()))
        }

        try {
            val remoteDto = remoteDataSource.getUserById(id)
            localDataSource.insertUsers(listOf(remoteDto.toEntity()))

            val freshUser = localDataSource.getUserById(id)
            if (freshUser != null) {
                emit(Resource.Success(freshUser.toDomain()))
            }
        } catch (e: Exception) {
            if (cachedUser == null) {
                emit(Resource.Error(e.message ?: "User not found"))
            }
        }
    }
}
