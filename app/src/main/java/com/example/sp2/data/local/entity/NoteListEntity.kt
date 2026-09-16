package com.example.sp2.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_lists")
data class NoteListEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String
)