package com.example.sp2.ui.screens.notes

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
import com.example.sp2.model.Note
import com.example.sp2.model.NoteList
import com.example.sp2.ui.components.EmptyState
import com.example.sp2.ui.components.NoteListFolderCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteFoldersScreen(
    onBack: () -> Unit,
    onOpenFolder: (Int) -> Unit,
    notesViewModel: NotesViewModel = viewModel(),
    noteListViewModel: NoteListViewModel = viewModel()
) {

    val notes by notesViewModel.notes.collectAsState()
    val noteLists by noteListViewModel.noteLists.collectAsState()

    var showNewFolderDialog by remember { mutableStateOf(false) }
    var newFolderName by remember { mutableStateOf("") }
    var folderToDelete by remember { mutableStateOf<NoteList?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.note_folders_title)) },
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

        if (noteLists.isEmpty()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(20.dp)
            ) {

                EmptyState(
                    title = stringResource(R.string.note_folders_empty_title),
                    description = stringResource(R.string.note_folders_empty_description)
                )

                Spacer(modifier = Modifier.height(20.dp))

                TextButton(onClick = { showNewFolderDialog = true }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Text(text = stringResource(R.string.note_new_folder))
                }
            }

        } else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(
                    items = noteLists,
                    key = { it.id }
                ) { noteList ->

                    val noteCount = notes.count { it.listId == noteList.id }

                    NoteListFolderCard(
                        noteList = noteList,
                        noteCount = noteCount,
                        onClick = { onOpenFolder(noteList.id) },
                        onLongClick = {
                            // "General" can't be deleted
                            if (noteList.id != NoteList.GENERAL_FOLDER_ID) {
                                folderToDelete = noteList
                            }
                        }
                    )
                }

                item {
                    TextButton(onClick = { showNewFolderDialog = true }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        Text(text = stringResource(R.string.note_new_folder))
                    }
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
            title = { Text(stringResource(R.string.note_new_folder)) },
            text = {
                OutlinedTextField(
                    value = newFolderName,
                    onValueChange = { newFolderName = it },
                    label = { Text(stringResource(R.string.note_folder_name)) },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        noteListViewModel.addList(newFolderName)
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

    folderToDelete?.let { noteList ->

        val noteCount = notes.count { it.listId == noteList.id }

        AlertDialog(
            onDismissRequest = { folderToDelete = null },
            title = { Text(stringResource(R.string.note_delete_folder_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                    Text(
                        text = stringResource(
                            R.string.note_delete_folder_message,
                            noteList.name
                        )
                    )

                    if (noteCount > 0) {
                        Text(
                            text = stringResource(
                                R.string.note_delete_folder_warning,
                                noteCount
                            ),
                            color = androidx.compose.material3.MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        noteListViewModel.deleteList(noteList)
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