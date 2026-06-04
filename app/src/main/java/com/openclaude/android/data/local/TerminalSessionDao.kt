package com.openclaude.android.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

data class TerminalSessionEntity(
    @PrimaryKey val id: String,
    val name: String,
    val workingDirectory: String,
    val historyJson: String,
    val createdAt: Long,
    val lastActiveAt: Long,
    val isActive: Boolean = true
)

@Dao
interface TerminalSessionDao {
    @Query("SELECT * FROM terminal_sessions ORDER BY lastActiveAt DESC")
    fun getAllSessions(): Flow<List<TerminalSessionEntity>>

    @Query("SELECT * FROM terminal_sessions WHERE id = :id")
    suspend fun getSession(id: String): TerminalSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: TerminalSessionEntity)

    @Update
    suspend fun updateSession(session: TerminalSessionEntity)

    @Delete
    suspend fun deleteSession(session: TerminalSessionEntity)

    @Query("DELETE FROM terminal_sessions")
    suspend fun deleteAllSessions()

    @Query("SELECT * FROM terminal_sessions WHERE isActive = 1")
    fun getActiveSessions(): Flow<List<TerminalSessionEntity>>
}
