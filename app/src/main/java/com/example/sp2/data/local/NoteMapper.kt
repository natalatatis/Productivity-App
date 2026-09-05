package com.example.sp2.data.local

import com.example.sp2.data.local.entity.NoteEntity
import com.example.sp2.model.Note

// Converts a Note into a database entity
fun Note.toEntity(): NoteEntity {
    return NoteEntity(
        id = id,
        title = title,
        content = content,
        date = date
    )
}

// Converts a database entity into a Note
fun NoteEntity.toNote(): Note {
    return Note(
        id = id,
        title = title,
        content = content,
        date = date
    )
}