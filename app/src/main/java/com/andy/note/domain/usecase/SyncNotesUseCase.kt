package com.andy.note.domain.usecase

import com.andy.note.domain.repository.NoteRepository
import javax.inject.Inject

class SyncNotesUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke() {
        repository.syncNotes()
    }
}
