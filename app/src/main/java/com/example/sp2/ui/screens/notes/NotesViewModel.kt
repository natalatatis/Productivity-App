package com.example.sp2.ui.screens.notes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sp2.R
import com.example.sp2.data.NotesRepository
import com.example.sp2.data.local.DatabaseProvider
import com.example.sp2.model.Note
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


// Handles note data for the UI
class NotesViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val database =
        DatabaseProvider.getDatabase(application)

    private val repository =
        NotesRepository(database.noteDao())

    val notes: StateFlow<List<Note>> =
        repository.notes.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private var autoSaveJob: Job? = null

    suspend fun getNote(noteId: Int): Note? {
        return repository.getNoteById(noteId)
    }

    fun autoSaveNote(
        noteId: Int?,
        title: String,
        content: String,
        date: String,
        listId: Int?,
        onNoteCreated: (Int) -> Unit
    ) {

        autoSaveJob?.cancel()

        autoSaveJob = viewModelScope.launch {

            // Waits until the user stops typing
            delay(500)

            if (title.isBlank() && content.isBlank()) {
                return@launch
            }

            // Falls back to a default title when the person only
            // typed content, so the note never shows up blank
            val finalTitle = title.trim().ifBlank {
                getApplication<Application>().getString(R.string.note_untitled)
            }

            if (noteId == null) {

                // Creates the note for the first time
                val newId = repository.addNote(
                    Note(
                        id = 0,
                        title = finalTitle,
                        content = content.trim(),
                        date = date,
                        listId = listId
                    )
                )

                onNoteCreated(
                    newId.toInt()
                )

            } else {

                // Updates the existing note, keeping its folder
                repository.updateNote(
                    Note(
                        id = noteId,
                        title = finalTitle,
                        content = content.trim(),
                        date = date,
                        listId = listId
                    )
                )
            }
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    fun moveNoteToList(note: Note, listId: Int) {
        viewModelScope.launch {
            repository.updateNote(note.copy(listId = listId))
        }
    }
}