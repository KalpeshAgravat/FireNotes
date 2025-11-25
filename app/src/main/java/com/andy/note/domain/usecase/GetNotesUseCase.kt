package com.andy.note.domain.usecase

import com.andy.note.domain.model.Note
import com.andy.note.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotesUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    // Overloading invoke operator lets us call the class like a function: getNotesUseCase()
    operator fun invoke(): Flow<List<Note>> {
        return repository.getNotes()
    }
}
