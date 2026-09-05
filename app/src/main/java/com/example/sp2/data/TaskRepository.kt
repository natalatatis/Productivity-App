package com.example.sp2.data

import com.example.sp2.data.local.dao.TaskDao
import com.example.sp2.data.local.toEntity
import com.example.sp2.data.local.toTask
import com.example.sp2.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Handles task data operations
class TaskRepository(
    private val taskDao: TaskDao
) {

    // Gets all tasks from Room
    val tasks: Flow<List<Task>> =
        taskDao.getAllTasks().map { entities ->
            entities.map { it.toTask() }
        }

    // Adds a task
    suspend fun addTask(task: Task) {
        taskDao.insertTask(
            task.copy(id = 0).toEntity()
        )
    }

    // Updates a task
    suspend fun updateTask(task: Task) {
        taskDao.updateTask(
            task.toEntity()
        )
    }

    // Deletes a task
    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(
            task.toEntity()
        )
    }

    // Changes task completion status
    suspend fun toggleTask(task: Task) {
        updateTask(
            task.copy(
                completed = !task.completed
            )
        )
    }
}