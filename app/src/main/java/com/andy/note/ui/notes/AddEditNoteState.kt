package com.andy.note.ui.notes

data class AddEditNoteState(
    val title: String = "",
    val content: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
