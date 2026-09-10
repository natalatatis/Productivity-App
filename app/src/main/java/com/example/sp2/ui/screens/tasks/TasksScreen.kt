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
import androidx.compose.material.icons.filled.DriveFileMove
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import com.example.sp2.ui.components.EmptyState
import com.example.sp2.ui.components.PriorityChip
import com.example.sp2.ui.components.SectionHeader
import com.example.sp2.ui.components.TaskListFolderCard
import com.example.sp2.model.TaskList

// Displays tasks and task lists
@Composable
fun TasksScreen(
    onAddTask: () -> Unit = {},
    onEditTask: (Int) -> Unit = {},
    onOpenList: (Int) -> Unit = {},
    taskViewModel: TaskViewModel = viewModel(),
    taskListViewModel: TaskListViewModel = viewModel()
) {

    // Tasks stored in Room
    val tasks by taskViewModel.tasks.collectAsState()

    // Lists stored in Room
    val taskLists by taskListViewModel.taskLists.collectAsState()

    // Tasks that do not belong to any list
    val tasksWithoutList =
        tasks.filter {
            it.listId == null
        }

    // Controls new list dialog
    var showNewListDialog by remember {
        mutableStateOf(false)
    }

    var newListName by remember {
        mutableStateOf("")
    }

    // Task selected for moving
    var taskToMove by remember {
        mutableStateOf<Task?>(null)
    }

    // List selected for deletion
    var listToDelete by remember {
        mutableStateOf<TaskList?>(null)
    }

    Scaffold(
        floatingActionButton = {

            FloatingActionButton(
                onClick = onAddTask
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription =
                        stringResource(
                            R.string.task_add
                        )
                )
            }
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            // Screen title
            item {

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                SectionHeader(
                    title =
                        stringResource(
                            R.string.tasks_title
                        )
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )
            }

            // No tasks and no lists
            if (
                tasks.isEmpty() &&
                taskLists.isEmpty()
            ) {

                item {

                    EmptyState(
                        title =
                            stringResource(
                                R.string.tasks_empty_title
                            ),
                        description =
                            stringResource(
                                R.string.tasks_empty_description
                            )
                    )
                }
            }

            // Tasks not assigned to a list
            if (tasksWithoutList.isNotEmpty()) {

                item {

                    Text(
                        text =
                            stringResource(
                                R.string.task_no_list
                            ),
                        style =
                            MaterialTheme.typography.titleMedium
                    )
                }

                items(
                    items = tasksWithoutList,
                    key = {
                        "task_${it.id}"
                    }
                ) { task ->

                    TaskRow(
                        task = task,

                        onEdit = {
                            onEditTask(task.id)
                        },

                        onMove = {
                            taskToMove = task
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

            // Each list and its tasks
            taskLists.forEach { taskList ->

                val tasksForList =
                    tasks.filter {
                        it.listId == taskList.id
                    }

                item(
                    key =
                        "list_${taskList.id}"
                ) {

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    TaskListFolderCard(
                        taskList = taskList,
                        taskCount = tasksForList.size,

                        onClick = {
                            onOpenList(taskList.id)
                        },

                        onLongClick = {
                            listToDelete = taskList
                        }
                    )
                }

                items(
                    items = tasksForList,
                    key = {
                        "task_${it.id}"
                    }
                ) { task ->

                    Box(
                        modifier =
                            Modifier.padding(
                                start = 16.dp
                            )
                    ) {

                        TaskRow(
                            task = task,

                            onEdit = {
                                onEditTask(task.id)
                            },

                            onMove = {
                                taskToMove = task
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

            // Create new list
            item {

                TextButton(
                    onClick = {
                        showNewListDialog = true
                    }
                ) {

                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )

                    Text(
                        text =
                            stringResource(
                                R.string.task_new_list
                            )
                    )
                }

                Spacer(
                    modifier = Modifier.height(80.dp)
                )
            }
        }
    }

    // Create list dialog
    if (showNewListDialog) {

        AlertDialog(
            onDismissRequest = {
                showNewListDialog = false
                newListName = ""
            },

            title = {
                Text(
                    text =
                        stringResource(
                            R.string.task_new_list
                        )
                )
            },

            text = {

                OutlinedTextField(
                    value = newListName,

                    onValueChange = {
                        newListName = it
                    },

                    label = {
                        Text(
                            text =
                                stringResource(
                                    R.string.task_list_name
                                )
                        )
                    },

                    singleLine = true
                )
            },

            confirmButton = {

                Button(
                    onClick = {

                        taskListViewModel.addList(
                            newListName
                        )

                        newListName = ""

                        showNewListDialog = false
                    },
                    enabled =
                        newListName.isNotBlank()
                ) {

                    Text(
                        text =
                            stringResource(
                                R.string.action_create
                            )
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        newListName = ""
                        showNewListDialog = false
                    }
                ) {

                    Text(
                        text =
                            stringResource(
                                R.string.action_cancel
                            )
                    )
                }
            }
        )
    }

    // Move task dialog
    taskToMove?.let { task ->

        AlertDialog(
            onDismissRequest = {
                taskToMove = null
            },

            title = {
                Text(
                    text =
                        stringResource(
                            R.string.task_move
                        )
                )
            },

            text = {

                Column {

                    // Move outside all lists
                    TextButton(
                        onClick = {

                            taskViewModel.moveTaskToList(
                                task = task,
                                listId = null
                            )

                            taskToMove = null
                        },
                        modifier =
                            Modifier.fillMaxWidth()
                    ) {

                        Text(
                            text =
                                stringResource(
                                    R.string.task_no_list
                                )
                        )
                    }

                    // Move to one of the lists
                    taskLists.forEach { taskList ->

                        TextButton(
                            onClick = {

                                taskViewModel.moveTaskToList(
                                    task = task,
                                    listId = taskList.id
                                )

                                taskToMove = null
                            },
                            modifier =
                                Modifier.fillMaxWidth()
                        ) {

                            Text(
                                text = taskList.name
                            )
                        }
                    }
                }
            },

            confirmButton = {},

            dismissButton = {

                TextButton(
                    onClick = {
                        taskToMove = null
                    }
                ) {

                    Text(
                        text =
                            stringResource(
                                R.string.action_cancel
                            )
                    )
                }
            }
        )
    }

    // Confirms deletion of a task list
    listToDelete?.let { taskList ->

        val taskCount = tasks.count {
            it.listId == taskList.id
        }

        AlertDialog(
            onDismissRequest = {
                listToDelete = null
            },

            title = {
                Text(
                    text = stringResource(
                        R.string.task_delete_list_title
                    )
                )
            },

            text = {

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        text = stringResource(
                            R.string.task_delete_list_message,
                            taskList.name
                        )
                    )

                    if (taskCount > 0) {

                        Text(
                            text = stringResource(
                                R.string.task_delete_list_warning,
                                taskCount
                            ),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },

            confirmButton = {

                Button(
                    onClick = {

                        taskListViewModel.deleteList(
                            taskList
                        )

                        listToDelete = null
                    }
                ) {

                    Text(
                        text = stringResource(
                            R.string.task_delete_list_confirm
                        )
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        listToDelete = null
                    }
                ) {

                    Text(
                        text = stringResource(
                            R.string.action_cancel
                        )
                    )
                }
            }
        )
    }
}


// Displays an individual task row
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TaskRow(
    task: Task,
    onEdit: () -> Unit,
    onMove: () -> Unit,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {

    // Controls long-press menu
    var showMenu by remember {
        mutableStateOf(false)
    }

    Box {

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = {
                        // Can later open task directly
                    },
                    onLongClick = {
                        showMenu = true
                    }
                ),

            shape = RoundedCornerShape(20.dp),

            color =
                MaterialTheme.colorScheme
                    .surfaceVariant
                    .copy(alpha = 0.4f)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

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

                    verticalArrangement =
                        Arrangement.spacedBy(6.dp)
                ) {

                    Text(
                        text = task.title,

                        style =
                            MaterialTheme.typography
                                .titleMedium,

                        textDecoration =
                            if (task.completed) {
                                TextDecoration.LineThrough
                            } else {
                                TextDecoration.None
                            }
                    )

                    if (
                        task.description.isNotBlank()
                    ) {

                        Text(
                            text = task.description,
                            style =
                                MaterialTheme.typography
                                    .bodyMedium
                        )
                    }

                    PriorityChip(
                        priority = task.priority
                    )
                }
            }
        }

        // Long press options
        DropdownMenu(
            expanded = showMenu,

            onDismissRequest = {
                showMenu = false
            }
        ) {

            // Edit
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
                        imageVector =
                            Icons.Default.Edit,
                        contentDescription = null
                    )
                },

                onClick = {
                    showMenu = false
                    onEdit()
                }
            )

            // Move
            DropdownMenuItem(
                text = {
                    Text(
                        stringResource(
                            R.string.task_move
                        )
                    )
                },

                leadingIcon = {
                    Icon(
                        imageVector =
                            Icons.Default.DriveFileMove,
                        contentDescription = null
                    )
                },

                onClick = {
                    showMenu = false
                    onMove()
                }
            )

            // Delete
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
                        imageVector =
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