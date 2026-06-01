package com.example.repopattern.data.remote

import com.example.repopattern.data.model.UserDto
import javax.inject.Inject
import javax.inject.Singleton

interface RemoteDataSource {
    suspend fun getUsers(): List<UserDto>
    suspend fun getUserById(id: Int): UserDto
}

@Singleton
class RemoteDataSourceImpl @Inject constructor(
    private val apiService: ApiService
) : RemoteDataSource {
    override suspend fun getUsers(): List<UserDto> {
        return apiService.getUsers().data
    }

    override suspend fun getUserById(id: Int): UserDto {
        return apiService.getUserById(id).data
    }
}
