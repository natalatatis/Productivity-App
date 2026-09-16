package com.example.sp2.data.local

import com.example.sp2.data.local.entity.NoteEntity
import com.example.sp2.model.Note

fun Note.toEntity(): NoteEntity {
    return NoteEntity(
        id = id,
        title = title,
        content = content,
        date = date,
        listId = listId
    )
}

fun NoteEntity.toNote(): Note {
    return Note(
        id = id,
        title = title,
        content = content,
        date = date,
        listId = listId
    )
}