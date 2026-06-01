package com.openclaude.android.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.openclaude.android.data.model.ChatMessage
import com.openclaude.android.data.model.Conversation

@Database(
    entities = [ChatMessage::class, Conversation::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao

    companion object {
        const val DATABASE_NAME = "openclaude_db"
    }
}
