package com.example.sp2.model

data class NoteList(
    val id: Int = 0,
    val name: String
) {
    companion object {
        const val GENERAL_FOLDER_ID = 1
    }
}