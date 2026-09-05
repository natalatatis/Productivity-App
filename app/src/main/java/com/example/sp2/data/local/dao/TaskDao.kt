package com.example.sp2.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.sp2.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

// Defines the database operations for tasks
@Dao
interface TaskDao {

    // Gets all tasks
    @Query("SELECT * FROM tasks ORDER BY id DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    // Adds a task and returns its generated ID
    @Insert
    suspend fun insertTask(task: TaskEntity): Long

    // Updates a task
    @Update
    suspend fun updateTask(task: TaskEntity)

    // Deletes a task
    @Delete
    suspend fun deleteTask(task: TaskEntity)
}