package com.example.sp2.data

import com.example.sp2.data.local.dao.NoteDao
import com.example.sp2.data.local.toEntity
import com.example.sp2.data.local.toNote
import com.example.sp2.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Handles note data operations
class NotesRepository(
    private val noteDao: NoteDao
) {

    // Gets all notes from Room
    val notes: Flow<List<Note>> =
        noteDao.getAllNotes().map { entities ->
            entities.map { it.toNote() }
        }

    // Gets one note
    suspend fun getNoteById(noteId: Int): Note? {
        return noteDao.getNoteById(noteId)?.toNote()
    }

    // Adds a note and returns its generated ID
    suspend fun addNote(note: Note): Long {
        return noteDao.insertNote(
            note.copy(id = 0).toEntity()
        )
    }



    // Updates a note
    suspend fun updateNote(note: Note) {
        noteDao.updateNote(
            note.toEntity()
        )
    }

    // Deletes a note
    suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(
            note.toEntity()
        )
    }
}