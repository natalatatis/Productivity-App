package com.example.sp2.ui.screens.notes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sp2.R

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

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {

                                // Opens the selected note
                                onOpenNote(note.id)
                            }
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            // Note title
                            Text(
                                text = note.title
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
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}