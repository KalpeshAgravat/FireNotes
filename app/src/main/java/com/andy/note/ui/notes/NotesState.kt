package com.andy.note.ui.notes

import com.andy.note.domain.model.Note

data class NotesState(
    val notes: List<Note> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null
)
