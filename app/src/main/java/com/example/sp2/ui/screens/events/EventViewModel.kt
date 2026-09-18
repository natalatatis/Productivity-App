package com.example.sp2.ui.screens.events

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sp2.data.EventRepository
import com.example.sp2.data.local.DatabaseProvider
import com.example.sp2.model.Event
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class EventViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository =
        EventRepository(DatabaseProvider.getDatabase(application).eventDao())

    val events: StateFlow<List<Event>> =
        repository.events.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addEvent(title: String, description: String, date: LocalDate, time: LocalTime?) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.addEvent(
                Event(title = title.trim(), description = description.trim(), date = date, time = time)
            )
        }
    }

    fun updateEvent(event: Event) {
        viewModelScope.launch {
            repository.updateEvent(event)
        }
    }

    fun deleteEvent(event: Event) {
        viewModelScope.launch {
            repository.deleteEvent(event)
        }
    }
}