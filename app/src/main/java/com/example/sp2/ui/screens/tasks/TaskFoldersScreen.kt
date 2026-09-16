package com.example.sp2.ui.screens.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sp2.R
import com.example.sp2.model.TaskList
import com.example.sp2.ui.components.EmptyState
import com.example.sp2.ui.components.TaskListFolderCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFoldersScreen(
    onBack: () -> Unit,
    onOpenFolder: (Int) -> Unit,
    taskViewModel: TaskViewModel = viewModel(),
    taskListViewModel: TaskListViewModel = viewModel()
) {

    val tasks by taskViewModel.tasks.collectAsState()
    val taskLists by taskListViewModel.taskLists.collectAsState()

    var showNewFolderDialog by remember { mutableStateOf(false) }
    var newFolderName by remember { mutableStateOf("") }
    var folderToDelete by remember { mutableStateOf<TaskList?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.task_folders_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.nav_back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            items(
                items = taskLists,
                key = { it.id }
            ) { taskList ->

                val taskCount = tasks.count {
                    (it.listId ?: TaskList.GENERAL_FOLDER_ID) == taskList.id
                }

                TaskListFolderCard(
                    taskList = taskList,
                    taskCount = taskCount,
                    onClick = { onOpenFolder(taskList.id) },
                    onLongClick = {
                        if (taskList.id != TaskList.GENERAL_FOLDER_ID) {
                            folderToDelete = taskList
                        }
                    }
                )
            }

            item {
                TextButton(onClick = { showNewFolderDialog = true }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Text(text = stringResource(R.string.task_new_list))
                }
            }
        }
    }

    if (showNewFolderDialog) {

        AlertDialog(
            onDismissRequest = {
                showNewFolderDialog = false
                newFolderName = ""
            },
            title = { Text(stringResource(R.string.task_new_list)) },
            text = {
                OutlinedTextField(
                    value = newFolderName,
                    onValueChange = { newFolderName = it },
                    label = { Text(stringResource(R.string.task_list_name)) },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        taskListViewModel.addList(newFolderName)
                        newFolderName = ""
                        showNewFolderDialog = false
                    },
                    enabled = newFolderName.isNotBlank()
                ) {
                    Text(stringResource(R.string.action_create))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        newFolderName = ""
                        showNewFolderDialog = false
                    }
                ) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    folderToDelete?.let { taskList ->

        val taskCount = tasks.count {
            (it.listId ?: TaskList.GENERAL_FOLDER_ID) == taskList.id
        }

        AlertDialog(
            onDismissRequest = { folderToDelete = null },
            title = { Text(stringResource(R.string.task_delete_list_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

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
                        taskListViewModel.deleteList(taskList)
                        folderToDelete = null
                    }
                ) {
                    Text(stringResource(R.string.task_delete_list_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { folderToDelete = null }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}