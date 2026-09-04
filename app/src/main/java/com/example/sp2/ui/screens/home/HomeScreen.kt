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
import com.example.sp2.model.Task
import com.example.sp2.ui.components.EmptyState
import com.example.sp2.ui.components.HabitStreakCard
import com.example.sp2.ui.components.PriorityChip
import com.example.sp2.ui.components.SectionHeader
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
            SectionHeader(
                title = stringResource(R.string.home_today)
            )
        }

        // Dynamic tasks
        if (activeTasks.isEmpty()) {

            item {
                EmptyState(
                    title = stringResource(R.string.tasks_empty_title),
                    description = stringResource(R.string.tasks_empty_description)
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

        // Rounded, container-colored surface matching HabitStreakCard's language
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
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ) {

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
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
                            ),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Priority chip, always visible (compact, matches the new rounded style)
                Row(
                    modifier = Modifier.padding(start = 48.dp)
                ) {
                    PriorityChip(priority = task.priority)
                }

                // Extra details, only shown when the task is expanded
                if (isExpanded) {

                    Column(
                        modifier = Modifier.padding(start = 48.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {

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