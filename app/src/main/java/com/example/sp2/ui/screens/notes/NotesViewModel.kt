package com.example.sp2.ui.screens.notes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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
        onNoteCreated: (Int) -> Unit
    ) {

        autoSaveJob?.cancel()

        autoSaveJob = viewModelScope.launch {

            // Waits until the user stops typing
            delay(500)

            if (title.isBlank() && content.isBlank()) {
                return@launch
            }

            if (noteId == null) {

                // Creates the note for the first time
                val newId = repository.addNote(
                    Note(
                        id = 0,
                        title = title.trim(),
                        content = content.trim(),
                        date = date
                    )
                )

                onNoteCreated(
                    newId.toInt()
                )

            } else {

                // Updates the existing note
                repository.updateNote(
                    Note(
                        id = noteId,
                        title = title.trim(),
                        content = content.trim(),
                        date = date
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
}