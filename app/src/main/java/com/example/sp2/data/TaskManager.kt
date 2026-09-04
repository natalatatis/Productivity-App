package com.example.sp2.data

import androidx.compose.runtime.mutableStateListOf
import com.example.sp2.model.Priority
import com.example.sp2.model.ReminderFrequency
import com.example.sp2.model.RepeatFrequency
import com.example.sp2.model.Task
import java.time.LocalDate
import java.time.LocalTime

object TaskManager {

    val tasks = mutableStateListOf<Task>()

    private var nextId = 1

    fun addTask(
        title: String,
        description: String,
        priority: Priority,
        date: LocalDate? = null,
        time: LocalTime? = null,
        reminder: ReminderFrequency = ReminderFrequency.NONE,
        repeat: RepeatFrequency = RepeatFrequency.NONE
    ) {
        tasks.add(
            Task(
                id = nextId++,
                title = title,
                description = description,
                priority = priority,
                date = date,
                time = time,
                reminder = reminder,
                repeat = repeat
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