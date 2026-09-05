package com.example.sp2.ui.screens.notes

import androidx.lifecycle.ViewModel
import com.example.sp2.data.NotesRepository
import com.example.sp2.model.Note
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotesViewModel : ViewModel() {

    private val _notes = MutableStateFlow(NotesRepository.notes)
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    fun saveNote(
        title: String,
        content: String
    ) {
        if (title.isBlank() && content.isBlank()) return

        val note = Note(
            id = _notes.value.size + 1,
            title = title.trim(),
            content = content.trim(),
            date = SimpleDateFormat(
                "MMMM d, yyyy",
                Locale.getDefault()
            ).format(Date())
        )

        NotesRepository.addNote(note)
        _notes.value = NotesRepository.notes
    }
}