package com.example.sp2.ui.screens.notes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.fillMaxWidth
import com.example.sp2.R
import com.example.sp2.model.NoteList
import com.example.sp2.ui.components.EmptyState
import com.example.sp2.ui.components.NoteCard
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteFolderDetailScreen(
    noteList: NoteList,
    onBack: () -> Unit,
    onOpenNote: (Int) -> Unit,
    onAddNote: () -> Unit,
    viewModel: NotesViewModel = viewModel()
) {

    val notes by viewModel.notes.collectAsState()

    val notesInFolder = notes.filter {
        (it.listId ?: com.example.sp2.model.NoteList.GENERAL_FOLDER_ID) == noteList.id
    }

    var noteToMove by remember { mutableStateOf<com.example.sp2.model.Note?>(null) }
    val noteListViewModel: NoteListViewModel = viewModel()
    val noteLists by noteListViewModel.noteLists.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(noteList.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddNote) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.note_add)
                )
            }
        }
    ) { paddingValues ->

        if (notesInFolder.isEmpty()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(20.dp)
            ) {
                EmptyState(
                    title = stringResource(R.string.notes_empty_title)
                )
            }

        } else {

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(
                    items = notesInFolder,
                    key = { it.id }
                ) { note ->

                    NoteCard(
                        note = note,
                        onOpen = { onOpenNote(note.id) },
                        onMove = { noteToMove = note },
                        onDelete = { viewModel.deleteNote(note) }
                    )
                }
            }
        }
    }

    noteToMove?.let { note ->

        androidx.compose.material3.AlertDialog(
            onDismissRequest = { noteToMove = null },
            title = { Text(stringResource(R.string.task_move)) },
            text = {
                Column {
                    noteLists.forEach { list ->
                        androidx.compose.material3.TextButton(
                            onClick = {
                                viewModel.moveNoteToList(note, list.id)
                                noteToMove = null
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(list.name)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { noteToMove = null }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}