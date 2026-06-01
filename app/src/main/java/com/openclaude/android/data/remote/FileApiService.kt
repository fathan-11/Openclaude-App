package com.openclaude.android.data.remote

import com.openclaude.android.data.model.*
import retrofit2.http.*

interface FileApiService {
    @GET("files/list")
    suspend fun listFiles(@Query("path") path: String = "/"): List<FileNode>
    
    @GET("files/read")
    suspend fun readFile(@Query("path") path: String): FileContent
    
    @GET("files/search")
    suspend fun searchCode(@Query("query") query: String, @Query("path") path: String = "/"): List<SearchResult>
    
    @GET("files/diff")
    suspend fun getDiff(@Query("file") file: String): DiffResult
}

data class FileContent(
    val path: String,
    val content: String,
    val language: String,
    val size: Long,
    val lineCount: Int
)
