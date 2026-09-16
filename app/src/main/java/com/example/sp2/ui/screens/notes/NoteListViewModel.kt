package com.example.sp2.ui.screens.notes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sp2.data.NoteListRepository
import com.example.sp2.data.local.DatabaseProvider
import com.example.sp2.model.NoteList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NoteListViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val database = DatabaseProvider.getDatabase(application)

    private val repository = NoteListRepository(
        noteListDao = database.noteListDao(),
        noteDao = database.noteDao()
    )

    val noteLists: StateFlow<List<NoteList>> = repository.noteLists.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        viewModelScope.launch {
            repository.ensureGeneralFolder()
        }
    }

    fun addList(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.addList(name.trim())
        }
    }

    fun deleteList(noteList: NoteList) {
        viewModelScope.launch {
            repository.deleteList(noteList)
        }
    }
}