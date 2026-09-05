package com.example.sp2.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.sp2.data.local.dao.NoteDao
import com.example.sp2.data.local.dao.TaskDao
import com.example.sp2.data.local.entity.NoteEntity
import com.example.sp2.data.local.entity.TaskEntity

// Defines the local Room database
@Database(
    entities = [
        TaskEntity::class,
        NoteEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // Provides access to task database operations
    abstract fun taskDao(): TaskDao

    // Provides access to note database operations
    abstract fun noteDao(): NoteDao
}