package com.example.sp2.ui.screens.tasks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sp2.data.TaskListRepository
import com.example.sp2.data.local.DatabaseProvider
import com.example.sp2.model.TaskList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskListViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val database =
        DatabaseProvider.getDatabase(application)

    private val repository =
        TaskListRepository(
            taskListDao = database.taskListDao(),
            taskDao = database.taskDao()
        )

    // Lists available to the UI
    val taskLists: StateFlow<List<TaskList>> =
        repository.taskLists.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Creates a new list
    fun addList(name: String) {

        if (name.isBlank()) {
            return
        }

        viewModelScope.launch {

            repository.addList(
                name.trim()
            )
        }
    }

    // Renames a list
    fun updateList(taskList: TaskList) {

        viewModelScope.launch {

            repository.updateList(
                taskList
            )
        }
    }

    // Deletes a list and its tasks
    fun deleteList(taskList: TaskList) {

        viewModelScope.launch {

            repository.deleteList(
                taskList
            )
        }
    }
}