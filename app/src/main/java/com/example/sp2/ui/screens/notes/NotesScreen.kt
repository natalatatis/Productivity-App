package com.example.sp2.ui.screens.notes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sp2.R
import com.example.sp2.model.NoteList
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.clickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    noteId: Int? = null,
    initialListId: Int? = null,
    onBack: () -> Unit,
    viewModel: NotesViewModel = viewModel(),
    noteListViewModel: NoteListViewModel = viewModel()
) {

    val noteLists by noteListViewModel.noteLists.collectAsState()

    // Stores the ID after a new note is created
    var currentNoteId by rememberSaveable {
        mutableStateOf(noteId)
    }

    // The folder this note belongs to. Starts as whatever folder it
    // was created from (or General by default); overwritten once an
    // existing note loads, so its own saved folder is respected
    var noteListId by rememberSaveable {
        mutableStateOf(initialListId ?: NoteList.GENERAL_FOLDER_ID)
    }

    var showFolderMenu by remember { mutableStateOf(false) }

    var title by rememberSaveable {
        mutableStateOf("")
    }

    var content by rememberSaveable {
        mutableStateOf("")
    }

    // Prevents autosave while an existing note is being loaded
    var noteLoaded by remember {
        mutableStateOf(noteId == null)
    }

    // Keeps the original note date
    var noteDate by rememberSaveable {
        mutableStateOf(
            SimpleDateFormat(
                "MMMM d, yyyy",
                Locale.getDefault()
            ).format(Date())
        )
    }

    // Loads an existing note
    LaunchedEffect(noteId) {

        if (noteId != null) {

            val note = viewModel.getNote(noteId)

            if (note != null) {
                currentNoteId = note.id
                title = note.title
                content = note.content
                noteDate = note.date
                noteListId = note.listId ?: NoteList.GENERAL_FOLDER_ID
            }

            noteLoaded = true
        }
    }

    // Autosaves whenever the title, content, or folder changes
    LaunchedEffect(title, content, noteListId, noteLoaded) {

        if (!noteLoaded) {
            return@LaunchedEffect
        }

        if (title.isBlank() && content.isBlank()) {
            return@LaunchedEffect
        }

        viewModel.autoSaveNote(
            noteId = currentNoteId,
            title = title,
            content = content,
            date = noteDate,
            listId = noteListId,
            onNoteCreated = { newId ->
                currentNoteId = newId
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {

                    // Tapping the current folder name opens a picker —
                    // this replaces the plain "Notes" title
                    androidx.compose.foundation.layout.Box {

                        androidx.compose.foundation.layout.Row(
                            modifier = Modifier.clickable {
                                showFolderMenu = true
                            },
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {

                            Text(
                                text = noteLists.find { it.id == noteListId }?.name
                                    ?: stringResource(R.string.notes_title)
                            )

                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null
                            )
                        }

                        DropdownMenu(
                            expanded = showFolderMenu,
                            onDismissRequest = { showFolderMenu = false }
                        ) {

                            noteLists.forEach { noteList ->
                                DropdownMenuItem(
                                    text = { Text(noteList.name) },
                                    onClick = {
                                        noteListId = noteList.id
                                        showFolderMenu = false
                                    }
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(
                                R.string.nav_back
                            )
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(
                    horizontal = 20.dp,
                    vertical = 16.dp
                ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Note date, shown at the very top, above the title
            Text(
                text = noteDate,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(
                    horizontal = 4.dp
                )
            )

            // Note title
            TextField(
                value = title,
                onValueChange = {
                    title = it
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = stringResource(R.string.note_title),
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                textStyle = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            // Divider
            HorizontalDivider(
                modifier = Modifier.padding(
                    vertical = 4.dp
                )
            )

            // Note content
            TextField(
                value = content,
                onValueChange = {
                    content = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                placeholder = {
                    Text(
                        text = stringResource(
                            R.string.note_content_placeholder
                        )
                    )
                },
                textStyle = MaterialTheme.typography.bodyLarge,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }
    }
}