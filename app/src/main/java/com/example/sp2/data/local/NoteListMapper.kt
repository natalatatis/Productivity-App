package com.example.sp2.data.local

import com.example.sp2.data.local.entity.NoteListEntity
import com.example.sp2.model.NoteList

fun NoteList.toEntity(): NoteListEntity {
    return NoteListEntity(
        id = id,
        name = name
    )
}

fun NoteListEntity.toNoteList(): NoteList {
    return NoteList(
        id = id,
        name = name
    )
}