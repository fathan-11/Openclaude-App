package com.openclaude.android.data.remote

import com.openclaude.android.data.model.*
import retrofit2.http.*

interface GitHubApiService {
    @GET("user/repos")
    suspend fun getRepos(@Query("sort") sort: String = "updated", @Query("per_page") perPage: Int = 30): List<GitHubRepo>
    
    @GET("repos/{owner}/{repo}")
    suspend fun getRepo(@Path("owner") owner: String, @Path("repo") repo: String): GitHubRepo
    
    @GET("repos/{owner}/{repo}/pulls")
    suspend fun getPRs(@Path("owner") owner: String, @Path("repo") repo: String, @Query("state") state: String = "open"): List<GitHubPR>
    
    @GET("repos/{owner}/{repo}/pulls/{number}")
    suspend fun getPR(@Path("owner") owner: String, @Path("repo") repo: String, @Path("number") number: Int): GitHubPR
    
    @GET("repos/{owner}/{repo}/issues")
    suspend fun getIssues(@Path("owner") owner: String, @Path("repo") repo: String, @Query("state") state: String = "open"): List<GitHubIssue>
    
    @GET("repos/{owner}/{repo}/commits")
    suspend fun getCommits(@Path("owner") owner: String, @Path("repo") repo: String, @Query("per_page") perPage: Int = 30): List<GitHubCommit>
    
    @GET("repos/{owner}/{repo}/pulls/{number}/reviews")
    suspend fun getReviews(@Path("owner") owner: String, @Path("repo") repo: String, @Path("number") number: Int): List<GitHubReview>
    
    @POST("repos/{owner}/{repo}/issues")
    suspend fun createIssue(@Path("owner") owner: String, @Path("repo") repo: String, @Body issue: Map<String, String>): GitHubIssue
    
    @POST("repos/{owner}/{repo}/pulls")
    suspend fun createPR(@Path("owner") owner: String, @Path("repo") repo: String, @Body pr: Map<String, Any>): GitHubPR
}
