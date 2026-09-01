package com.example.sp2.data

import androidx.compose.runtime.mutableStateListOf
import com.example.sp2.model.Task

object TaskManager {

    val tasks = mutableStateListOf<Task>()

    private var nextId = 1

    fun addTask(
        title: String,
        description: String,
        priority: com.example.sp2.model.Priority
    ) {
        tasks.add(
            Task(
                id = nextId++,
                title = title,
                description = description,
                priority = priority
            )
        )
    }

    fun updateTask(task: Task) {
        val index = tasks.indexOfFirst {
            it.id == task.id
        }

        if (index != -1) {
            tasks[index] = task
        }
    }

    fun deleteTask(task: Task) {
        tasks.remove(task)
    }

    fun toggleTask(task: Task) {
        updateTask(
            task.copy(
                completed = !task.completed
            )
        )
    }
}