package com.example.sp2.ui.screens.notes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sp2.data.NotesRepository
import com.example.sp2.data.local.DatabaseProvider
import com.example.sp2.model.Note
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

    // Notes observed from Room
    val notes: StateFlow<List<Note>> =
        repository.notes.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Saves a new note
    fun saveNote(
        title: String,
        content: String
    ) {

        if (title.isBlank() && content.isBlank()) {
            return
        }

        val note = Note(
            id = 0,
            title = title.trim(),
            content = content.trim(),
            date = SimpleDateFormat(
                "MMMM d, yyyy",
                Locale.getDefault()
            ).format(Date())
        )

        viewModelScope.launch {
            repository.addNote(note)
        }
    }

    // Updates an existing note
    fun updateNote(note: Note) {
        viewModelScope.launch {
            repository.updateNote(note)
        }
    }

    // Deletes a note
    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }
}