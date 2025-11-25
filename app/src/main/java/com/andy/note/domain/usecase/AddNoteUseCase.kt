package com.andy.note.domain.usecase

import com.andy.note.domain.model.Note
import com.andy.note.domain.repository.NoteRepository
import javax.inject.Inject

class AddNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    @Throws(Exception::class) // Optional: Document that this might throw validation errors
    suspend operator fun invoke(note: Note) {
        if (note.title.isBlank()) {
            throw Exception("The title of the note can't be empty.")
        }
        if (note.content.isBlank()) {
            throw Exception("The content of the note can't be empty.")
        }
        repository.insertNote(note)
    }
}
