package com.example.sp2.ui.screens.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sp2.model.TaskList
import com.example.sp2.ui.components.EmptyState
import com.example.sp2.ui.components.TaskItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListDetailScreen(
    taskList: TaskList,
    onBack: () -> Unit,
    onEditTask: (Int) -> Unit,
    taskViewModel: TaskViewModel = viewModel()
) {

    // Observes tasks stored in Room
    val tasks by taskViewModel.tasks.collectAsState()

    // Only shows tasks that belong to this list
    val tasksForList = tasks.filter {
        it.listId == taskList.id
    }

    Scaffold(
        topBar = {

            TopAppBar(
                title = {
                    Text(
                        text = taskList.name
                    )
                },
                navigationIcon = {

                    IconButton(
                        onClick = onBack
                    ) {

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        // Empty list
        if (tasksForList.isEmpty()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(20.dp)
            ) {

                EmptyState(
                    title = "No tasks in this list",
                    description = "Tasks assigned to this list will appear here."
                )
            }

        } else {

            // Tasks inside the list
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(
                    items = tasksForList,
                    key = { it.id }
                ) { task ->

                    TaskItem(
                        task = task,

                        onEdit = {
                            onEditTask(task.id)
                        },

                        onToggle = {
                            taskViewModel.toggleTask(task)
                        },

                        onDelete = {
                            taskViewModel.deleteTask(task)
                        }
                    )
                }
            }
        }
    }
}