package com.andy.note.ui.notes

import com.andy.note.domain.model.Note

sealed class NotesEvent {
    data class DeleteNote(val note: Note) : NotesEvent()
    // We could add more like RestoreNote, OrderNotes, etc.
    object Refresh : NotesEvent()
}
