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
import androidx.compose.material.icons.automirrored.filled.DriveFileMove
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.sp2.model.TaskList
import com.example.sp2.ui.components.EmptyState
import com.example.sp2.ui.components.PriorityChip
import com.example.sp2.ui.components.TaskListFolderCard

@Composable
fun TasksScreen(
    onAddTask: () -> Unit = {},
    onEditTask: (Int) -> Unit = {},
    onOpenFolders: () -> Unit = {},
    taskViewModel: TaskViewModel = viewModel(),
    taskListViewModel: TaskListViewModel = viewModel()
) {

    val tasks by taskViewModel.tasks.collectAsState()
    val taskLists by taskListViewModel.taskLists.collectAsState()

    var taskToMove by remember { mutableStateOf<Task?>(null) }

    var searchQuery by remember { mutableStateOf("") }

    // Filters by title or description, keyword-only
    val displayedTasks = if (searchQuery.isBlank()) {
        tasks
    } else {
        tasks.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    it.description.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTask) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.task_add)
                )
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // Search bar, always visible, above the Folders entry
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(stringResource(R.string.search_tasks)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Entry point into the Folders section
            Column(modifier = Modifier.padding(16.dp)) {

                TaskListFolderCard(
                    taskList = TaskList(id = -1, name = stringResource(R.string.task_folders_title)),
                    taskCount = taskLists.size,
                    onClick = onOpenFolders
                )
            }

            // Every task, regardless of which folder it's in
            if (displayedTasks.isEmpty()) {

                Column(modifier = Modifier.padding(20.dp)) {
                    EmptyState(
                        title = stringResource(R.string.tasks_empty_title),
                        description = stringResource(R.string.tasks_empty_description)
                    )
                }

            } else {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    items(
                        items = displayedTasks,
                        key = { it.id }
                    ) { task ->

                        TaskRow(
                            task = task,
                            onEdit = { onEditTask(task.id) },
                            onMove = { taskToMove = task },
                            onToggle = { taskViewModel.toggleTask(task) },
                            onDelete = { taskViewModel.deleteTask(task) }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }

    // Move task dialog
    taskToMove?.let { task ->

        AlertDialog(
            onDismissRequest = { taskToMove = null },
            title = { Text(stringResource(R.string.task_move)) },
            text = {
                Column {
                    taskLists.forEach { taskList ->
                        TextButton(
                            onClick = {
                                taskViewModel.moveTaskToList(task = task, listId = taskList.id)
                                taskToMove = null
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(taskList.name)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { taskToMove = null }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TaskRow(
    task: Task,
    onEdit: () -> Unit,
    onMove: () -> Unit,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {

    var showMenu by remember { mutableStateOf(false) }

    Box {

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = {},
                    onLongClick = { showMenu = true }
                ),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Checkbox(checked = task.completed, onCheckedChange = { onToggle() })

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {

                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        textDecoration = if (task.completed) {
                            TextDecoration.LineThrough
                        } else {
                            TextDecoration.None
                        }
                    )

                    if (task.description.isNotBlank()) {
                        Text(text = task.description, style = MaterialTheme.typography.bodyMedium)
                    }

                    PriorityChip(priority = task.priority)
                }
            }
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {

            DropdownMenuItem(
                text = { Text(stringResource(R.string.task_edit)) },
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                onClick = { showMenu = false; onEdit() }
            )

            DropdownMenuItem(
                text = { Text(stringResource(R.string.task_move)) },
                leadingIcon = {
                    Icon(Icons.AutoMirrored.Filled.DriveFileMove, contentDescription = null)
                },
                onClick = { showMenu = false; onMove() }
            )

            DropdownMenuItem(
                text = { Text(stringResource(R.string.task_delete)) },
                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) },
                onClick = { showMenu = false; onDelete() }
            )
        }
    }
}