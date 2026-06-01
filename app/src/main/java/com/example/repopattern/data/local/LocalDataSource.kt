package com.example.repopattern.data.local

import com.example.repopattern.data.model.UserEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface LocalDataSource {
    fun getUsers(): Flow<List<UserEntity>>
    suspend fun getUserById(id: Int): UserEntity?
    suspend fun insertUsers(users: List<UserEntity>)
    suspend fun deleteAllUsers()
}

@Singleton
class LocalDataSourceImpl @Inject constructor(
    private val userDao: UserDao
) : LocalDataSource {
    override fun getUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()
    override suspend fun getUserById(id: Int): UserEntity? = userDao.getUserById(id)
    override suspend fun insertUsers(users: List<UserEntity>) = userDao.insertUsers(users)
    override suspend fun deleteAllUsers() = userDao.deleteAll()
}
