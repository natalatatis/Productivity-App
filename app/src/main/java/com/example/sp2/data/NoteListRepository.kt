package com.example.sp2.data

import com.example.sp2.data.local.dao.NoteDao
import com.example.sp2.data.local.dao.NoteListDao
import com.example.sp2.data.local.entity.NoteListEntity
import com.example.sp2.data.local.toEntity
import com.example.sp2.data.local.toNoteList
import com.example.sp2.model.NoteList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteListRepository(
    private val noteListDao: NoteListDao,
    private val noteDao: NoteDao
) {

    val noteLists: Flow<List<NoteList>> =
        noteListDao.getAllLists().map { entities ->
            entities.map { it.toNoteList() }
        }

    suspend fun getListById(id: Int): NoteList? {
        return noteListDao.getListById(id)?.toNoteList()
    }

    // Guarantees a non-deletable "General" folder always exists,
    // reserved at a fixed id — called once when the app opens
    suspend fun ensureGeneralFolder() {
        if (noteListDao.getListById(NoteList.GENERAL_FOLDER_ID) == null) {
            noteListDao.insertList(
                NoteListEntity(id = NoteList.GENERAL_FOLDER_ID, name = "General")
            )
        }
    }

    suspend fun addList(name: String): Long {
        return noteListDao.insertList(NoteList(name = name).toEntity())
    }

    suspend fun updateList(noteList: NoteList) {
        noteListDao.updateList(noteList.toEntity())
    }

    // Deletes the notes inside the folder and then the folder itself
    suspend fun deleteList(noteList: NoteList) {
        noteDao.deleteNotesByListId(noteList.id)
        noteListDao.deleteList(noteList.toEntity())
    }
}