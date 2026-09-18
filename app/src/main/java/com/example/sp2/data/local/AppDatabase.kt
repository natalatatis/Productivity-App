package com.example.sp2.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.sp2.data.local.dao.AlarmDao
import com.example.sp2.data.local.dao.AppUsageDao
import com.example.sp2.data.local.dao.HabitDao
import com.example.sp2.data.local.dao.NoteDao
import com.example.sp2.data.local.dao.NoteListDao
import com.example.sp2.data.local.dao.TaskDao
import com.example.sp2.data.local.dao.TaskListDao
import com.example.sp2.data.local.entity.AlarmEntity
import com.example.sp2.data.local.entity.AppUsageLogEntity
import com.example.sp2.data.local.entity.AppUsageStateEntity
import com.example.sp2.data.local.entity.HabitEntity
import com.example.sp2.data.local.entity.NoteEntity
import com.example.sp2.data.local.entity.NoteListEntity
import com.example.sp2.data.local.entity.TaskEntity
import com.example.sp2.data.local.entity.TaskListEntity

@Database(
    entities = [
        TaskEntity::class,
        NoteEntity::class,
        NoteListEntity::class,
        TaskListEntity::class,
        HabitEntity::class,
        AppUsageStateEntity::class,
        AppUsageLogEntity::class,
        AlarmEntity::class
    ],
    version = 11,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    abstract fun noteDao(): NoteDao

    abstract fun noteListDao(): NoteListDao

    abstract fun taskListDao(): TaskListDao

    abstract fun habitDao(): HabitDao

    abstract fun appUsageDao(): AppUsageDao

    abstract fun alarmDao(): AlarmDao
}