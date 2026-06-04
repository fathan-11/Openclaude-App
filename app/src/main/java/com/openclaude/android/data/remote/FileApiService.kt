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

    @POST("files/create")
    suspend fun createFile(@Body request: CreateFileRequest): FileOperationResult

    @PUT("files/write")
    suspend fun writeFile(@Body request: WriteFileRequest): FileOperationResult

    @DELETE("files/delete")
    suspend fun deleteFile(@Query("path") path: String): FileOperationResult

    @POST("files/mkdir")
    suspend fun createDirectory(@Body request: CreateDirRequest): FileOperationResult

    @POST("files/rename")
    suspend fun renameFile(@Body request: RenameFileRequest): FileOperationResult
}

data class FileContent(
    val path: String,
    val content: String,
    val language: String,
    val size: Long,
    val lineCount: Int
)

data class CreateFileRequest(
    val path: String,
    val content: String = ""
)

data class WriteFileRequest(
    val path: String,
    val content: String
)

data class CreateDirRequest(
    val path: String
)

data class RenameFileRequest(
    val oldPath: String,
    val newPath: String
)

data class FileOperationResult(
    val success: Boolean,
    val message: String = "",
    val path: String = ""
)
