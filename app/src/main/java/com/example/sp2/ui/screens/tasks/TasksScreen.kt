package com.example.sp2.ui.screens.tasks

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sp2.R
import com.example.sp2.model.Task
import com.example.sp2.ui.components.EmptyState
import com.example.sp2.ui.components.PriorityChip
import com.example.sp2.ui.components.SectionHeader

// Displays the list of tasks
@Composable
fun TasksScreen(
    onAddTask: () -> Unit = {},
    onEditTask: (Int) -> Unit = {},
    taskViewModel: TaskViewModel = viewModel()
) {

    // Observes the tasks stored in Room
    val tasks by taskViewModel.tasks.collectAsState()

    Scaffold(
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

            if (tasks.isEmpty()) {

                EmptyState(
                    title = stringResource(R.string.tasks_empty_title),
                    description = stringResource(
                        R.string.tasks_empty_description
                    )
                )

            } else {

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    items(
                        items = tasks,
                        key = { it.id }
                    ) { task ->

                        TaskRow(
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
}

// Displays an individual task row
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TaskRow(
    task: Task,
    onEdit: () -> Unit,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {

    // Controls the long-press menu
    var showMenu by remember {
        mutableStateOf(false)
    }

    Box {

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = {
                        // Normal click can later open task details if wanted
                    },
                    onLongClick = {
                        showMenu = true
                    }
                ),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(
                alpha = 0.4f
            )
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
                        onToggle()
                    }
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {

                    // Task title
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        textDecoration = if (task.completed) {
                            TextDecoration.LineThrough
                        } else {
                            TextDecoration.None
                        }
                    )

                    // Task description
                    if (task.description.isNotBlank()) {
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    // Task priority
                    PriorityChip(
                        priority = task.priority
                    )
                }
            }
        }

        // Menu shown after long pressing the task
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
                        stringResource(
                            R.string.task_edit
                        )
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Edit,
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
                        stringResource(
                            R.string.task_delete
                        )
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Delete,
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