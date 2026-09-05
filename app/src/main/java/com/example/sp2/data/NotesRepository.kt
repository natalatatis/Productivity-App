package com.example.sp2.data

import com.example.sp2.model.Note

object NotesRepository {

    private val _notes = mutableListOf<Note>()

    val notes: List<Note>
        get() = _notes

    fun addNote(note: Note) {
        _notes.add(note)
    }
}