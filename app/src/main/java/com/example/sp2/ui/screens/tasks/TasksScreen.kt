package com.example.sp2.ui.screens.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.sp2.R
import com.example.sp2.data.TaskManager
import com.example.sp2.ui.components.TaskItem

// Displays the list of tasks
@Composable
fun TasksScreen(
    onAddTask: () -> Unit = {},
    onEditTask: (Int) -> Unit = {}
) {

    // Gets the current tasks
    val tasks = TaskManager.tasks

    Scaffold(
        // Button for adding a new task
        floatingActionButton = {

            FloatingActionButton(
                onClick = onAddTask
            ) {

                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(
                        R.string.task_add
                    )
                )
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
        ) {

            // Screen title
            Text(
                text = stringResource(
                    R.string.tasks_title
                ),
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // Shows a message when there are no tasks
            if (tasks.isEmpty()) {

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Text(
                        text = stringResource(
                            R.string.tasks_empty_title
                        ),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = stringResource(
                            R.string.tasks_empty_description
                        )
                    )
                }

            } else {

                // Displays the list of tasks
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    items(
                        items = tasks,
                        key = { it.id }
                    ) { task ->

                        // Displays each task
                        TaskItem(
                            task = task,
                            onEdit = {
                                onEditTask(task.id)
                            }
                        )
                    }
                }
            }
        }
    }
}