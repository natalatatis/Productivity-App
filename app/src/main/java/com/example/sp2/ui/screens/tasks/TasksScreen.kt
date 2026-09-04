package com.example.sp2.ui.screens.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.sp2.R
import com.example.sp2.data.TaskManager
import com.example.sp2.model.Task
import com.example.sp2.ui.components.EmptyState
import com.example.sp2.ui.components.PriorityChip
import com.example.sp2.ui.components.SectionHeader

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
            SectionHeader(
                title = stringResource(R.string.tasks_title)
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // Shows a message when there are no tasks
            if (tasks.isEmpty()) {

                EmptyState(
                    title = stringResource(R.string.tasks_empty_title),
                    description = stringResource(R.string.tasks_empty_description)
                )

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
                        TaskRow(
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

// Displays an individual task row, using the same rounded/container
// visual language as HabitStreakCard and the Home screen's task cards
@Composable
private fun TaskRow(
    task: Task,
    onEdit: () -> Unit
) {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Marks the task as completed or incomplete
            Checkbox(
                checked = task.completed,
                onCheckedChange = {
                    TaskManager.toggleTask(task)
                }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {

                // Displays the task title
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (task.completed) {
                        TextDecoration.LineThrough
                    } else {
                        TextDecoration.None
                    }
                )

                // Displays the description if one exists
                if (task.description.isNotBlank()) {

                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Displays the task priority as a chip
                PriorityChip(priority = task.priority)
            }

            // Button for editing the task
            IconButton(
                onClick = onEdit
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(
                        R.string.task_edit
                    )
                )
            }

            // Button for deleting the task
            IconButton(
                onClick = {
                    TaskManager.deleteTask(task)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(
                        R.string.task_delete
                    )
                )
            }
        }
    }
}