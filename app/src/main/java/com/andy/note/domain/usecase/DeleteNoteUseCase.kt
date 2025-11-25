package com.andy.note.domain.usecase

import com.andy.note.domain.repository.NoteRepository
import javax.inject.Inject

class DeleteNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(id: String) {
        repository.deleteNote(id)
    }
}
