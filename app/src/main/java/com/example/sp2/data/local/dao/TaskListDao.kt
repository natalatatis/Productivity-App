package com.example.sp2.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.sp2.data.local.entity.TaskListEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskListDao {

    // Gets every task list
    @Query("SELECT * FROM task_lists ORDER BY name ASC")
    fun getAllLists(): Flow<List<TaskListEntity>>

    // Gets one list
    @Query("SELECT * FROM task_lists WHERE id = :listId LIMIT 1")
    suspend fun getListById(listId: Int): TaskListEntity?

    // Creates a list
    @Insert
    suspend fun insertList(taskList: TaskListEntity): Long

    // Updates a list
    @Update
    suspend fun updateList(taskList: TaskListEntity)

    // Deletes a list
    @Delete
    suspend fun deleteList(taskList: TaskListEntity)
}