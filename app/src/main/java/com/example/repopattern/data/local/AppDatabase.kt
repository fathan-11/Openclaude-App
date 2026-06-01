package com.example.repopattern.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.repopattern.data.model.UserEntity

@Database(entities = [UserEntity::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
