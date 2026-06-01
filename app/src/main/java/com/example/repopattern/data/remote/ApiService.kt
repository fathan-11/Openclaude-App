package com.example.repopattern.data.remote

import com.example.repopattern.data.model.UserDetailResponse
import com.example.repopattern.data.model.UserListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("api/users")
    suspend fun getUsers(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 12
    ): UserListResponse

    @GET("api/users/{id}")
    suspend fun getUserById(@Path("id") id: Int): UserDetailResponse
}
