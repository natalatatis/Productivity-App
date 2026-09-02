package com.example.sp2.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.sp2.R
import com.example.sp2.data.TaskManager
import com.example.sp2.model.Priority
import com.example.sp2.model.Task
import com.example.sp2.ui.components.HabitStreakCard

@Composable
fun HomeScreen() {

    // Gets the current tasks from the TaskManager
    val tasks = TaskManager.tasks

    // Only displays tasks that have not been completed
    val activeTasks = tasks.filter {
        !it.completed
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Greeting
        item {

            Text(
                text = stringResource(R.string.home_greeting),
                style = MaterialTheme.typography.headlineMedium
            )
        }

        // Streak
        item {

            HabitStreakCard(
                streak = 7
            )
        }

        // Today's tasks section
        item {

            Text(
                text = stringResource(R.string.home_today),
                style = MaterialTheme.typography.titleLarge
            )
        }

        // Dynamic tasks
        if (activeTasks.isEmpty()) {

            item {

                Text(
                    text = stringResource(
                        R.string.tasks_empty_title
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

        } else {

            items(
                items = activeTasks,
                key = { it.id }
            ) { task ->

                TaskCard(task)
            }
        }
    }
}

@Composable
private fun TaskCard(
    task: Task
) {

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            // Task title
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium
            )

            // Description
            if (task.description.isNotEmpty()) {

                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Priority
            Text(
                text = priorityText(task.priority),
                style = MaterialTheme.typography.labelMedium,
                color = when (task.priority) {
                    Priority.NONE -> MaterialTheme.colorScheme.onSurfaceVariant
                    Priority.LOW -> MaterialTheme.colorScheme.primary
                    Priority.MEDIUM -> MaterialTheme.colorScheme.tertiary
                    Priority.HIGH -> MaterialTheme.colorScheme.error
                }
            )
        }
    }
}

@Composable
private fun priorityText(
    priority: Priority
): String {

    return when (priority) {

        Priority.NONE -> stringResource(
            R.string.task_no_priority
        )

        Priority.LOW -> stringResource(
            R.string.priority_low
        )

        Priority.MEDIUM -> stringResource(
            R.string.priority_medium
        )

        Priority.HIGH -> stringResource(
            R.string.priority_high
        )
    }
}