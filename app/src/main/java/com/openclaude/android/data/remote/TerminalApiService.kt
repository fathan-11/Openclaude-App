package com.openclaude.android.data.remote

import com.openclaude.android.data.model.*
import retrofit2.http.*

interface TerminalApiService {
    @POST("terminal/run")
    suspend fun runCommand(@Body command: TerminalCommand): TerminalCommand

    @POST("terminal/kill/{id}")
    suspend fun killCommand(@Path("id") commandId: String)

    @GET("terminal/output/{id}")
    suspend fun getOutput(@Path("id") commandId: String): List<TerminalLine>

    @GET("terminal/sessions")
    suspend fun getSessions(): List<TerminalSession>
}
