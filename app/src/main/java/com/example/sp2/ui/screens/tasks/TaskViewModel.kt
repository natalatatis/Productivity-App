package com.example.sp2.ui.screens.tasks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sp2.data.TaskRepository
import com.example.sp2.data.local.DatabaseProvider
import com.example.sp2.model.Priority
import com.example.sp2.model.ReminderFrequency
import com.example.sp2.model.RepeatFrequency
import com.example.sp2.model.Task
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

// Handles task data for the UI
class TaskViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val database =
        DatabaseProvider.getDatabase(application)

    private val repository =
        TaskRepository(database.taskDao())

    // Tasks observed from Room
    val tasks: StateFlow<List<Task>> =
        repository.tasks.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Adds a task
    fun addTask(
        title: String,
        description: String,
        priority: Priority,
        date: LocalDate? = null,
        time: LocalTime? = null,
        reminder: ReminderFrequency = ReminderFrequency.NONE,
        repeat: RepeatFrequency = RepeatFrequency.NONE
    ) {
        viewModelScope.launch {
            repository.addTask(
                Task(
                    id = 0,
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
    }

    // Updates a task
    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    // Deletes a task
    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    // Changes completion status
    fun toggleTask(task: Task) {
        viewModelScope.launch {
            repository.toggleTask(task)
        }
    }
}