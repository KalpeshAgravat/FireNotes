package com.andy.note.domain.repository

import com.andy.note.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {

    // Local + Remote Sync Logic
    fun getNotes(): Flow<List<Note>>

    suspend fun getNoteById(id: String): Note?

    suspend fun insertNote(note: Note)

    suspend fun deleteNote(id: String)

    // Specific Sync functions
    suspend fun syncNotes()


    // Add this function
    fun syncRealtime(): kotlinx.coroutines.flow.Flow<Unit>
}
