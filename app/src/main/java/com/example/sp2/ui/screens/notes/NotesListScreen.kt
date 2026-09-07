package com.example.sp2.ui.screens.notes

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sp2.R
import com.example.sp2.model.Note

@Composable
fun NotesListScreen(
    onAddNote: () -> Unit,
    onOpenNote: (Int) -> Unit,
    viewModel: NotesViewModel = viewModel()
) {

    // Observes the notes stored in Room
    val notes by viewModel.notes.collectAsState()

    Scaffold(
        floatingActionButton = {

            // Creates a new note
            FloatingActionButton(
                onClick = onAddNote
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(
                        R.string.note_add
                    )
                )
            }
        }
    ) { paddingValues ->

        // Empty state
        if (notes.isEmpty()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = stringResource(
                        R.string.notes_empty_title
                    )
                )
            }

        } else {

            // Displays saved notes
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(
                    items = notes,
                    key = { it.id }
                ) { note ->

                    NoteCard(
                        note = note,

                        // Opens the selected note
                        onOpen = {
                            onOpenNote(note.id)
                        },

                        // Deletes the note from Room
                        onDelete = {
                            viewModel.deleteNote(note)
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NoteCard(
    note: Note,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {

    // Controls the long-press menu
    var showMenu by remember {
        mutableStateOf(false)
    }

    Box {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = {
                        onOpen()
                    },
                    onLongClick = {
                        showMenu = true
                    }
                )
        ) {

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                // Note title
                Text(
                    text = note.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Note date
                Text(
                    text = note.date
                )

                // Note preview
                if (note.content.isNotBlank()) {

                    Text(
                        text = note.content,
                        modifier = Modifier.padding(
                            top = 8.dp
                        ),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Menu shown after long pressing the note
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = {
                showMenu = false
            }
        ) {

            // Delete option
            DropdownMenuItem(
                text = {
                    Text(
                        stringResource(
                            R.string.note_delete
                        )
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(
                            R.string.note_delete_description
                        )
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