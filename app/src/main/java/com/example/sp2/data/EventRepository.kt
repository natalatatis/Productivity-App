package com.example.sp2.data

import com.example.sp2.data.local.dao.EventDao
import com.example.sp2.data.local.toEntity
import com.example.sp2.data.local.toEvent
import com.example.sp2.model.Event
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EventRepository(
    private val eventDao: EventDao
) {

    val events: Flow<List<Event>> =
        eventDao.getAllEvents().map { entities -> entities.map { it.toEvent() } }

    suspend fun addEvent(event: Event) {
        eventDao.insertEvent(event.copy(id = 0).toEntity())
    }

    suspend fun updateEvent(event: Event) {
        eventDao.updateEvent(event.toEntity())
    }

    suspend fun deleteEvent(event: Event) {
        eventDao.deleteEvent(event.toEntity())
    }
}