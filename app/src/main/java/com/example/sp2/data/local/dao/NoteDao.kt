package com.example.sp2.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.sp2.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

// Defines the database operations for notes
@Dao
interface NoteDao {

    // Gets all notes
    @Query("SELECT * FROM notes ORDER BY id DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    // Adds a note
    @Insert
    suspend fun insertNote(note: NoteEntity): Long

    // Updates a note
    @Update
    suspend fun updateNote(note: NoteEntity)

    // Deletes a note
    @Delete
    suspend fun deleteNote(note: NoteEntity)
}