package com.example.sp2.ui.screens.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.sp2.R
import com.example.sp2.data.TaskManager
import com.example.sp2.model.Priority
import com.example.sp2.model.Task
import com.example.sp2.ui.components.HabitStreakCard
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    onEditTask: (Int) -> Unit = {}
) {

    // Gets the current tasks from the TaskManager
    val tasks = TaskManager.tasks

    // Only displays tasks that have not been completed
    val activeTasks = tasks.filter {
        !it.completed
    }

    // Keeps track of which task is currently expanded (only one at a time)
    var expandedTaskId by remember {
        mutableStateOf<Int?>(null)
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
            HabitStreakCard(streak = 7)
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
                    text = stringResource(R.string.tasks_empty_title),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

        } else {

            items(
                items = activeTasks,
                key = { it.id }
            ) { task ->

                TaskCard(
                    task = task,
                    isExpanded = expandedTaskId == task.id,
                    onToggleExpand = {
                        expandedTaskId = if (expandedTaskId == task.id) {
                            null
                        } else {
                            task.id
                        }
                    },
                    onEdit = {
                        onEditTask(task.id)
                    },
                    onDelete = {
                        TaskManager.deleteTask(task)
                    },
                    onToggleComplete = {
                        TaskManager.toggleTask(task)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TaskCard(
    task: Task,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleComplete: () -> Unit
) {

    // Controls the visibility of the edit/delete menu shown on long press
    var showMenu by remember {
        mutableStateOf(false)
    }

    Box {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = onToggleExpand,
                    onLongClick = {
                        showMenu = true
                    }
                )
        ) {

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // Marks the task as already completed
                    IconButton(
                        onClick = onToggleComplete
                    ) {
                        Icon(
                            imageVector = if (task.completed) {
                                Icons.Filled.CheckCircle
                            } else {
                                Icons.Outlined.CheckCircle
                            },
                            contentDescription = stringResource(
                                R.string.task_mark_done
                            )
                        )
                    }

                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Extra details, only shown when the task is expanded
                if (isExpanded) {

                    if (task.description.isNotEmpty()) {
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    if (task.date != null || task.time != null) {

                        val dateText = task.date?.format(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        ) ?: ""

                        val timeText = task.time?.format(
                            DateTimeFormatter.ofPattern("HH:mm")
                        ) ?: ""

                        Text(
                            text = "$dateText $timeText".trim(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

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

        // Menu shown when the task card is long pressed
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = {
                showMenu = false
            }
        ) {

            DropdownMenuItem(
                text = {
                    Text(stringResource(R.string.task_edit))
                },
                leadingIcon = {
                    Icon(Icons.Default.Edit, contentDescription = null)
                },
                onClick = {
                    showMenu = false
                    onEdit()
                }
            )

            DropdownMenuItem(
                text = {
                    Text(stringResource(R.string.task_delete))
                },
                leadingIcon = {
                    Icon(Icons.Default.Delete, contentDescription = null)
                },
                onClick = {
                    showMenu = false
                    onDelete()
                }
            )
        }
    }
}

// Returns the localized text for each priority level
@Composable
private fun priorityText(
    priority: Priority
): String {

    return when (priority) {

        Priority.NONE -> stringResource(R.string.task_no_priority)
        Priority.LOW -> stringResource(R.string.priority_low)
        Priority.MEDIUM -> stringResource(R.string.priority_medium)
        Priority.HIGH -> stringResource(R.string.priority_high)
    }
}