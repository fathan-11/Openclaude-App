package com.openclaude.android.data.remote

import com.openclaude.android.data.model.GitStatus
import retrofit2.http.*

interface GitApiService {
    @GET("git/status")
    suspend fun getStatus(@Query("path") path: String = "/"): List<GitFileStatus>

    @GET("git/diff")
    suspend fun getDiff(@Query("file") file: String): GitDiffResult

    @GET("git/log")
    suspend fun getLog(@Query("path") path: String = "/", @Query("limit") limit: Int = 10): List<GitCommit>
}

data class GitFileStatus(
    val path: String,
    val status: String,
    val staged: Boolean = false,
    val pathOld: String? = null
)

data class GitDiffResult(
    val file: String,
    val oldContent: String,
    val newContent: String,
    val hunks: List<GitDiffHunk>
)

data class GitDiffHunk(
    val oldStart: Int,
    val oldCount: Int,
    val newStart: Int,
    val newCount: Int,
    val lines: List<String>
)

data class GitCommit(
    val sha: String,
    val message: String,
    val author: String,
    val date: Long
)
