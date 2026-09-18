package com.example.sp2.data.local

import com.example.sp2.data.local.entity.EventEntity
import com.example.sp2.model.Event
import java.time.LocalDate
import java.time.LocalTime

fun EventEntity.toEvent(): Event {
    return Event(
        id = id,
        title = title,
        description = description,
        date = LocalDate.parse(date),
        time = time?.let { LocalTime.parse(it) }
    )
}

fun Event.toEntity(): EventEntity {
    return EventEntity(
        id = id,
        title = title,
        description = description,
        date = date.toString(),
        time = time?.toString()
    )
}