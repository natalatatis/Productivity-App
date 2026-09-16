package com.example.sp2.ui.screens.notes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.sp2.ui.components.NoteCard
import com.example.sp2.ui.components.NoteListFolderCard

@Composable
fun NotesListScreen(
    onAddNote: () -> Unit,
    onOpenNote: (Int) -> Unit,
    onOpenFolders: () -> Unit,
    viewModel: NotesViewModel = viewModel(),
    noteListViewModel: NoteListViewModel = viewModel()
) {

    // Observes notes and folders stored in Room
    val notes by viewModel.notes.collectAsState()
    val noteLists by noteListViewModel.noteLists.collectAsState()

    var noteToMove by remember { mutableStateOf<Note?>(null) }

    var searchQuery by remember { mutableStateOf("") }

    // Filters by title or content, keyword-only
    val displayedNotes = if (searchQuery.isBlank()) {
        notes
    } else {
        notes.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    it.content.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        floatingActionButton = {

            FloatingActionButton(onClick = onAddNote) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.note_add)
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
                placeholder = { Text(stringResource(R.string.search_notes)) },
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

                NoteListFolderCard(
                    noteList = NoteList(id = -1, name = stringResource(R.string.note_folders_title)),
                    noteCount = noteLists.size,
                    onClick = onOpenFolders
                )
            }

            // Every note, regardless of which folder it's in
            if (displayedNotes.isEmpty()) {

                Column(modifier = Modifier.padding(20.dp)) {
                    EmptyState(title = stringResource(R.string.notes_empty_title))
                }

            } else {

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    items(
                        items = displayedNotes,
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
    }

    // Move-to-folder dialog
    noteToMove?.let { note ->

        AlertDialog(
            onDismissRequest = { noteToMove = null },
            title = { Text(stringResource(R.string.task_move)) },
            text = {
                Column {
                    noteLists.forEach { noteList ->
                        TextButton(
                            onClick = {
                                viewModel.moveNoteToList(note, noteList.id)
                                noteToMove = null
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(noteList.name)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { noteToMove = null }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}