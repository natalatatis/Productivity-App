package com.example.sp2.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.sp2.data.local.entity.NoteListEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteListDao {

    @Query("SELECT * FROM note_lists ORDER BY name ASC")
    fun getAllLists(): Flow<List<NoteListEntity>>

    @Query("SELECT * FROM note_lists WHERE id = :listId LIMIT 1")
    suspend fun getListById(listId: Int): NoteListEntity?

    @Insert
    suspend fun insertList(noteList: NoteListEntity): Long

    @Update
    suspend fun updateList(noteList: NoteListEntity)

    @Delete
    suspend fun deleteList(noteList: NoteListEntity)
}