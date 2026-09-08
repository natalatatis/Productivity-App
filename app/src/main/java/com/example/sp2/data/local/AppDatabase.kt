package com.example.sp2.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.sp2.data.local.dao.NoteDao
import com.example.sp2.data.local.dao.TaskDao
import com.example.sp2.data.local.dao.TaskListDao
import com.example.sp2.data.local.entity.NoteEntity
import com.example.sp2.data.local.entity.TaskEntity
import com.example.sp2.data.local.entity.TaskListEntity

@Database(
    entities = [
        TaskEntity::class,
        NoteEntity::class,
        TaskListEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    abstract fun noteDao(): NoteDao

    abstract fun taskListDao(): TaskListDao
}