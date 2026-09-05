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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sp2.R
import com.example.sp2.model.Task
import com.example.sp2.ui.components.EmptyState
import com.example.sp2.ui.components.HabitStreakCard
import com.example.sp2.ui.components.PriorityChip
import com.example.sp2.ui.components.SectionHeader
import com.example.sp2.ui.screens.tasks.TaskViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    onEditTask: (Int) -> Unit = {},
    taskViewModel: TaskViewModel = viewModel()
) {

    // Observes the tasks stored in Room
    val tasks by taskViewModel.tasks.collectAsState()

    // Gets today's date
    val today = LocalDate.now()

    // Only displays today's incomplete tasks
    val todayTasks = tasks.filter {
        it.date == today && !it.completed
    }

    // Keeps track of which task is currently expanded
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
            SectionHeader(
                title = stringResource(R.string.home_today)
            )
        }

        // Shows a message when there are no tasks for today
        if (todayTasks.isEmpty()) {

            item {
                EmptyState(
                    title = stringResource(R.string.tasks_empty_title),
                    description = stringResource(R.string.tasks_empty_description)
                )
            }

        } else {

            // Displays today's tasks
            items(
                items = todayTasks,
                key = { it.id }
            ) { task ->

                TaskCard(
                    task = task,
                    isExpanded = expandedTaskId == task.id,

                    // Expands or collapses the task card
                    onToggleExpand = {
                        expandedTaskId = if (expandedTaskId == task.id) {
                            null
                        } else {
                            task.id
                        }
                    },

                    // Opens the task editor
                    onEdit = {
                        onEditTask(task.id)
                    },

                    // Deletes the task from Room
                    onDelete = {
                        taskViewModel.deleteTask(task)
                    },

                    // Marks the task as complete or incomplete
                    onToggleComplete = {
                        taskViewModel.toggleTask(task)
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

    // Controls the visibility of the edit/delete menu
    var showMenu by remember {
        mutableStateOf(false)
    }

    Box {

        // Rounded task card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = onToggleExpand,
                    onLongClick = {
                        showMenu = true
                    }
                ),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(
                alpha = 0.4f
            )
        ) {

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // Marks the task as completed
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
                            ),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Task title
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Task priority
                Row(
                    modifier = Modifier.padding(start = 48.dp)
                ) {
                    PriorityChip(
                        priority = task.priority
                    )
                }

                // Extra task information
                if (isExpanded) {

                    Column(
                        modifier = Modifier.padding(start = 48.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {

                        // Description
                        if (task.description.isNotEmpty()) {
                            Text(
                                text = task.description,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        // Date and time
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
                    }
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

            // Edit option
            DropdownMenuItem(
                text = {
                    Text(
                        stringResource(R.string.task_edit)
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null
                    )
                },
                onClick = {
                    showMenu = false
                    onEdit()
                }
            )

            // Delete option
            DropdownMenuItem(
                text = {
                    Text(
                        stringResource(R.string.task_delete)
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null
                    )
                },
                onClick = {
                    showMenu = false
                    onDelete()
                }
            )
        }
    }
}