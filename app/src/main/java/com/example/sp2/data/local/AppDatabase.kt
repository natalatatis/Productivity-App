package com.example.sp2.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.sp2.data.local.dao.TaskDao
import com.example.sp2.data.local.entity.TaskEntity

// Defines the local Room database
@Database(
    entities = [
        TaskEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // Provides access to task database operations
    abstract fun taskDao(): TaskDao
}